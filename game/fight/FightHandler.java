package uk.ac.qub.eeecs.game.fight;

import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.ChoiceDialog;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.DialogHandler;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;


/**
 * <h1>Fight Handler</h1>
 * This class is for processing the logic in fight screen.
 * This includes displaying player attack options, processing player
 * attack, processing enemy attacks, processing running away and
 * displaying effect information.
 *
 * @author  Matthew Breen & Shannon Turley
 * @version 1.0
 */
public class FightHandler {

    private final int MAX_PERCENT = 100;

    //Turn state is used to keep track of what state the fight is currently in
    public enum TurnState{
        NONE, FIGHT, ITEM, RUN, RAN_AWAY, PLAYER_EFFECTS, PLAYER_MOVE, ENEMY_EFFECTS, ENEMY_MOVE
    }

    private TurnState turnState;
    private GameScreen gameScreen;
    private Typeface font;
    private int fontSize;
    private int charactersPerLine;

    private int yPositionText;
    private int xIndentFightButtons;
    private int xIndentRunButtons;

    protected Player player;
    protected Enemy enemy;

    //The dialog handler is used to display information to the user
    protected DialogHandler dialogHandler;

    //This is used to store the select move so it can be accessed later
    protected String chosenMove;

    protected AnimationManager enemyAnimationManager;
    protected AnimationManager playerAnimationManager;

    protected Random random;

    /**
     * This constructor declares FightHandler with the following parameters
     * and sets indentation and y position of text.
     * @author Matthew Breen
     * @param gameScreen GameScreen for the screen using this class
     * @param font Typeface for the font you want this class to use
     * @param fontSize int for the font size
     * @param charactersPerLine int for the characters per line you want the text to display
     */
    public FightHandler(GameScreen gameScreen, Typeface font, int fontSize, int charactersPerLine){
        this.dialogHandler = new DialogHandler(fontSize,charactersPerLine,font);
        this.gameScreen = gameScreen;
        int width = (int)gameScreen.getDefaultLayerViewport().getWidth();
        int height = (int)gameScreen.getDefaultLayerViewport().getHeight();
        this.font = font;
        this.fontSize = fontSize;
        this.charactersPerLine = charactersPerLine;
        yPositionText = (int)(height*0.1);
        xIndentFightButtons =(int)(width*0.2);
        xIndentRunButtons =(int)(width*0.6);
        random =new Random();
    }

    //Used for testing
    protected FightHandler(){ }

    /**
     * This method resets the fight handler so it is ready for a new fight
     * @author Matthew Breen
     * @param player Player the player taking part in the fight
     * @param enemy Enemy the enemy taking part in the fight
     */
    public void reset(Player player, Enemy enemy){
        this.dialogHandler.clear();
        this.player = player;
        player.resetEffects();
        this.enemy = enemy;
        this.enemyAnimationManager = enemy.getAnimationManager();
        turnState = TurnState.NONE;
        chosenMove = "";
    }

    /**
     * This method updates fight handler and calls method to update turn state
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used for update methods
     */
    public void update(ElapsedTime elapsedTime){
        dialogHandler.update(elapsedTime);
        enemyAnimationManager.update(elapsedTime);
        playerAnimationManager.update(elapsedTime);
        updateTurnState(elapsedTime);

    }

    /**
     * This method checks what the current turn state is, updates it and does actions appropriate to that turn state
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used for update methods
     */
    protected void updateTurnState(ElapsedTime elapsedTime){
        switch (turnState){

            case FIGHT:
                fightUpdate();
                break;

            case PLAYER_MOVE:
                if(dialogHandler.getCurrentDialog() == null || dialogHandler.getCurrentDialog().getHidden()) {
                    processEffects(enemy);
                    turnState = TurnState.ENEMY_EFFECTS;
                }
                break;

            case PLAYER_EFFECTS:
                if(dialogHandler.getCurrentDialog() == null || dialogHandler.getCurrentDialog().getHidden()) {
                    if(chosenMove.equals("")){
                        turnState = TurnState.PLAYER_MOVE;
                    }
                    else{
                        playerAttack(chosenMove, elapsedTime);
                    }
                }
                break;

            case ENEMY_EFFECTS:
                if(dialogHandler.getCurrentDialog().getHidden()) {
                    enemyAttack(elapsedTime);
                }
                break;

            case ENEMY_MOVE:
                if(dialogHandler.getCurrentDialog().getHidden()) {
                    turnState = TurnState.NONE;
                }
                break;

            case RUN:
                if(dialogHandler.getCurrentDialog() instanceof ChoiceDialog && !((ChoiceDialog) dialogHandler.getCurrentDialog()).getChoice().equals("")){
                    runningAway(((ChoiceDialog)dialogHandler.getCurrentDialog()).getChoice().equals("Yes"));
                }
                else if(dialogHandler.getCurrentDialog() == null || dialogHandler.getCurrentDialog().getHidden()) {
                    ArrayList<String> buttons = new ArrayList<>();
                    buttons.add("Yes");
                    buttons.add("No");
                    dialogHandler.add(new ChoiceDialog(gameScreen,"Do you want to try and run away?",font, fontSize,charactersPerLine,buttons, yPositionText, xIndentRunButtons));
                }
                break;

            case ITEM:
                dialogHandler.add(gameScreen,"Player used item", yPositionText);
                processEffects(player);
                turnState = TurnState.PLAYER_EFFECTS;
                break;

            case RAN_AWAY:
                if(dialogHandler.getCurrentDialog().getHidden()){
                    gameScreen.setVisible(false);
                }
                break;
        }
    }

    /**
     * This method if processes the fight state. If fight handler has just transitioned to
     * this state it creates a choice Dialog of player moves. If not it checks if any move
     * has been selected if so does the appreciate action.
     * @author Matthew Breen
     */
    protected void fightUpdate(){
        if(dialogHandler.getCurrentDialog() instanceof ChoiceDialog && !((ChoiceDialog) dialogHandler.getCurrentDialog()).getChoice().equals("")) {
            String move = ((ChoiceDialog)dialogHandler.getCurrentDialog()).getChoice();
            if (move.equals("Back")) {
                turnState = TurnState.NONE;
                dialogHandler.clear();
            }
            else{
                chosenMove = move;
                processEffects(player);
                turnState = TurnState.PLAYER_EFFECTS;
            }
        }
        else if(dialogHandler.getCurrentDialog() == null || dialogHandler.getCurrentDialog().getHidden()) {
            ArrayList<String> buttons = new ArrayList<>();
            for(AttackMove move : player.getAttackMoves()){
                buttons.add(move.getName());
            }
            buttons.add("Back");
            dialogHandler.add(new ChoiceDialog(gameScreen,"",font, fontSize,charactersPerLine,buttons, yPositionText, xIndentFightButtons));
        }
    }

    /**
     * This method processes a player running away. If they choose to it works out if the they where
     * successful or not.
     * @author Matthew Breen
     * @param run Boolean whether or not the player has chosen to run away
     */
    protected void runningAway(boolean run){
        if(run){
            int chance = (int)(player.getEvade() - enemy.getEvade() + MAX_PERCENT/2);
            if(chance > random.nextInt(MAX_PERCENT)){
                dialogHandler.add(gameScreen,"You successfully ran away", yPositionText);
                dialogHandler.getCurrentDialog().setHidden(false);
                turnState = TurnState.RAN_AWAY;
            }
            else{
                dialogHandler.add(gameScreen,"You failed to run away", yPositionText);
                processEffects(player);
                turnState = TurnState.PLAYER_EFFECTS;
            }
        }
        else{
            dialogHandler.clear();
            turnState = TurnState.NONE;
        }
    }

    /**
     * This method processes getting the player attack. I takes a string that is the attack name
     * and finds the player attack that it is for and attacks the enemy with it.
     * @author Matthew Breen
     * @param attack String is the name of the attack
     * @param elapsedTime ElapsedTime is ended for attack method
     */
    protected void playerAttack(String attack, ElapsedTime elapsedTime){
        AttackMove chosen = null;
        for(AttackMove move : player.getAttackMoves()) {
            if(move.getName().equals(attack)){
                chosen = move;
                break;
            }
        }
        attack(chosen, player, enemy, elapsedTime);
        turnState = TurnState.PLAYER_MOVE;
        chosenMove = "";
    }

    /**
     * This method processes getting the enemy attack. I randomly selects one of the enemies attack
     * moves and attacks the player with it.
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime is ended for attack method
     */
    protected void enemyAttack(ElapsedTime elapsedTime){
        ArrayList<AttackMove> moves = enemy.getAttackMoves();
        int x = random.nextInt(moves.size());
        attack(moves.get(x), enemy, player, elapsedTime);
        turnState = TurnState.ENEMY_MOVE;
    }

    /**
     * This method processes an attack made by a fightable object. It processes whether the move is self directed or not if so
     * adds effect to the one using the move, if not it is checked if the move hits the defender is so adds the effect. It also
     * displays this information to the user and plays animations.
     * @author Matthew Breen and Shannon Turley
     * @param move AttackMove this is the move the defender is using
     * @param attacker Fightable this is object using the move
     * @param defender Fightable this is opponent to the object using the move
     * @param elapsedTime ElapsedTime this is used for animations
     */
    protected void attack(AttackMove move, Fightable attacker, Fightable defender, ElapsedTime elapsedTime){
        if(move.isSelfEffect()){
            if(attacker instanceof Enemy){
                playEnemyAnimation("self", elapsedTime);
            }
            attacker.addEffect(move.getEffect());
            dialogHandler.add(gameScreen,attacker.getName()+" used "+ move.getName()+". "+move.getDescription(), yPositionText);
        }
        else if(defender.isHit()){
            if(attacker instanceof Enemy){
                playEnemyAnimation("attack", elapsedTime);
            } else if(attacker instanceof Player){
                playPlayerAnimation("attack", elapsedTime);
            }
            defender.addEffect(move.getEffect());
            dialogHandler.add(gameScreen,attacker.getName()+" used "+ move.getName()+". "+move.getDescription(), yPositionText);
        }
        else{
            dialogHandler.add(gameScreen,attacker.getName()+" tried to use "+ move.getName()+" but missed", yPositionText);
        }
        dialogHandler.getCurrentDialog().setHidden(false);
    }

    /**
     * This method draws the textDialogs which displays information to the user
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used for draw
     * @param graphics2D IGraphics2D used for draw
     * @param layerViewport LayerViewport used for draw
     * @param screenViewport ScreenViewport used for draw
     */
    public void drawText(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport){
        if(!turnState.equals(TurnState.NONE) && dialogHandler.getCurrentDialog() != null){
            dialogHandler.getCurrentDialog().draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        }
    }

    /**
     * This method sets the current turnState to whatever is passed
     * @author Matthew Breen
     * @param turnState TurnState is what you want the turn state to be updated to
     */
    public void setTurnState(TurnState turnState){
        this.turnState = turnState;
    }

    /**
     * This method returns the current turn state
     * @author Matthew Breen
     * @return TurnState is the current turn state
     */
    public TurnState getTurnState(){
        return turnState;
    }

    /**
     * This method updates an fightable objects effects and displays any information returned
     * @author Matthew Breen
     * @param effected Fightable is the object you want to update effects of
     */
    protected void processEffects(Fightable effected){
        ArrayList<String> messages = effected.processEffects();
        for(String message : messages){
            dialogHandler.add(gameScreen,effected.getName()+message, yPositionText);
        }
    }

    /**
     * Plays enemy animations for attacking and self affecting moves
     * @param type String type of attack
     * @param elapsedTime time elapsed since last update
     * @author Shannon Turley
     */
    public void playEnemyAnimation(String type, ElapsedTime elapsedTime){
        if(type.equalsIgnoreCase("attack")){
            enemyAnimationManager.play("Attack", elapsedTime);
        } else if(type.equalsIgnoreCase("self")){
            enemyAnimationManager.play("Self", elapsedTime);
        } else{
            enemyAnimationManager.stop();
        }
    }

    /**
     * Plays player animations for attacking and self affecting moves
     * @param type String type of attack
     * @param elapsedTime time elapsed since last update
     * @author Shannon Turley
     */
    public void playPlayerAnimation(String type, ElapsedTime elapsedTime){
        if(type.equalsIgnoreCase("attack")){
            playerAnimationManager.play("Attack", elapsedTime);
        } else if(type.equalsIgnoreCase("self")){
            playerAnimationManager.play("Self", elapsedTime);
        } else{
            playerAnimationManager.stop();
        }
    }

    /**
     * Sets the player animation manager
     * @param animationManager Animation manager to use
     * @author Shannon Turley
     */
    public void setPlayerAnimationManager(AnimationManager animationManager){
        this.playerAnimationManager = animationManager;
    }
}
