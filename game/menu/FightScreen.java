package uk.ac.qub.eeecs.game.menu;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.Bar;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.fight.AttackMove;
import uk.ac.qub.eeecs.game.fight.Effect;
import uk.ac.qub.eeecs.game.fight.Enemy;
import uk.ac.qub.eeecs.game.fight.FightHandler;
import uk.ac.qub.eeecs.game.fight.Fightable;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.JSONReader;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>FightScreen</h1>
 * Class for displaying the fight sequence.
 * It displays and processes buttons for fight, item and run away.
 * Displays enemy and player health bars. Processes death and win states.
 *
 * @author  Matthew Breen & Shannon Turley
 * @version 1.0
 */
public class FightScreen extends GameScreen {

    private final int HEALTH_BAR_MIN = 13;
    private final int MAX_PERCENT = 100;

    protected Player player;
    private GameObject playerImage;
    protected Bar playerHealthBar;

    protected Enemy enemy;
    protected Bar enemyHealthBar;

    protected PushButton fightButton;
    protected PushButton itemButton;
    protected PushButton runAwayButton;

    private int width, height;

    private Paint paint;

    protected FightHandler fightHandler;

    protected GameEnvironment gameEnvironment;

    protected AnimationManager playerAnimationManager;

    /**
     * This constructor declares FightHandler with the following parameters and creates fight handler
     * and sets indents and y position of text.
     * @author Matthew Breen & Shaanon Turley
     * @param game Game for the game creating this screen
     * @param player Player for player information in fight
     * @param font Typeface for the font you want this class to use
     * @param fontSize int for the font size
     * @param charactersPerLine int for the characters per line you want the text to display
     * @param gameEnvironment GameEnvironment for the Game Environment creating this
     */
    public FightScreen(Game game, Player player, Typeface font, int fontSize, int charactersPerLine, GameEnvironment gameEnvironment) {
        super("FightScreen", game);
        fightHandler = new FightHandler(this, font, fontSize, charactersPerLine);
        this.gameEnvironment = gameEnvironment;
        this.player = player;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
        width = (int)mDefaultLayerViewport.getWidth();
        height = (int)mDefaultLayerViewport.getHeight();
        playerImage = new GameObject(width*0.8f,height*0.5f,150,180,mGame.getAssetManager().getBitmap("PlayerClose"),this);
        playerAnimationManager = new AnimationManager(playerImage);
        playerAnimationManager.addAnimation("txt/animation/PlayerFightAnimation.JSON");
        fightHandler.setPlayerAnimationManager(playerAnimationManager);
        enemyHealthBar = new Bar(width*0.45f,height*0.85f,160.0f,30.0f,mGame.getAssetManager().getBitmap("healthBarFill"),mGame.getAssetManager().getBitmap("healthBar"),new Vector2(2,3),Bar.Orientation.Horizontal,113,114,1000,this);
        playerHealthBar = new Bar(width*0.45f,height*0.3f,160.0f,30.0f,mGame.getAssetManager().getBitmap("healthBarFill"),mGame.getAssetManager().getBitmap("healthBar"),new Vector2(2,3),Bar.Orientation.Horizontal,113,114,1000,this);
        fightButton = new PushButton(width*0.15f,height*0.1f,100,38,"Fight","FightPressed",this);
        itemButton = new PushButton(width*0.45f,height*0.1f+3,80,33,"Item","ItemPressed",this);
        runAwayButton = new PushButton(width*0.80f,height*0.1f,170,38,"RunAway","RunAwayPressed",this);
        isVisible = false;
    }

    //for testing
    protected FightScreen(Game game){super("FightScreen",game);}

    /**
     * This method sets up the fight screen for a new fight and displays it
     * @author Matthew Breen
     * @param enemyName String the name of the enemy the player is fighting
     */
    public void display(String enemyName){
        readEnemyFromJSON(enemyName);
        fightHandler.reset(player, enemy);
        enemyHealthBar.forceMaxValue();
        playerHealthBar.forceValue((int)(player.getHealth()/player.getMaxHealth()*MAX_PERCENT)+HEALTH_BAR_MIN);
        isVisible = true;
    }

    /**
     * This method reads the enemies details from JSON
     * @author Matthew Breen
     * @param enemyName String the name of the enemy the player is fighting
     */
    protected void readEnemyFromJSON(String enemyName){
        JSONReader reader = new JSONReader(mGame.getFileIO(),"txt/assets/Enemies.JSON");
        try {
            JSONObject obj = reader.getJson().getJSONObject(enemyName);
            ArrayList<AttackMove> attackMoves = new ArrayList<>();
            for(int i =0; i <  obj.getJSONArray("moves").length();i++){
                JSONObject move = obj.getJSONArray("moves").getJSONObject(i);
                JSONObject effect = move.getJSONObject("effect");
                Effect moveEffect;
                if(effect.has("numTurns")){
                    moveEffect = new Effect(effect.getInt("evadeChange"),effect.getInt("armourChange"),effect.getInt("healthChange"), effect.getInt("numTurns"),effect.getString("endMessage"),effect.getString("turnMessage"));
                }
                else{
                    moveEffect = new Effect(effect.getInt("healthChange"));
                }
                attackMoves.add(new AttackMove(move.getString("name"),move.getString("description"),moveEffect,move.getBoolean("selfEffect")));
            }
            ArrayList<PreviewItem> items = new ArrayList<>();
            for(int i =0; i <  obj.getJSONArray("items").length();i++){
                items.add(gameEnvironment.mItems.get(obj.getJSONArray("items").getString(i)));
            }
            enemy = new Enemy(enemyName,width * 0.15f, height * 0.75f, obj.getInt("width"), obj.getInt("height"), this, obj.getInt("health"),obj.getInt("evade"),attackMoves,items,obj.getInt("money"));
        }
        catch (JSONException e){
            GameEnvironment.log.addLog("Fight Screen","'"+enemyName+"' failed to load from txt/assets/Enemies.JSON");
            isVisible=false;
        }
    }

    /**
     * This method updates fight handler state if run away button is pressed
     * @author Matthew Breen
     */
    public void ranAway(){
        if(runAwayButton.isPushTriggered())
            fightHandler.setTurnState(FightHandler.TurnState.RUN);
    }

    /**
     * This method updates fight handler state if item button is pressed
     * @author Matthew Breen
     */
    public void fight(){
        if(fightButton.isPushTriggered()) {
            fightHandler.setTurnState(FightHandler.TurnState.FIGHT);
        }
    }

    /**
     * If the item button is pressed, shows the consumable screen. If an item has been consumed,
     * the turn ends.
     * @author Shannon Turley
     */
    public void item(){
        if(gameEnvironment.screenManager.consumableScreen.shouldEndTurn()){
            gameEnvironment.screenManager.consumableScreen.setShouldEndTurn(false);
            fightHandler.setTurnState(FightHandler.TurnState.ITEM);
        } else if(itemButton.isPushTriggered()){
            gameEnvironment.screenManager.setConsumableScreen(player, gameEnvironment);
            this.isVisible = false;
        }
    }

    /**
     * This method updates fight screen appropriately to fight handler state
     * @author Matthew Breen & Shannon Turley
     * @param elapsedTime ElapsedTime needed for update methods
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        if(fightHandler.getTurnState() == FightHandler.TurnState.NONE) {
            ranAway();
            fight();
            item();
            runAwayButton.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
            fightButton.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
            itemButton.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
        }
        if(enemy != null) {
            enemy.update(elapsedTime);
            processHealthBar(enemyHealthBar,enemy,elapsedTime);
        }
        playerAnimationManager.update(elapsedTime);
        processHealthBar(playerHealthBar,player,elapsedTime);
        fightHandler.update(elapsedTime);
        deathOrWinCheck();
    }

    /**
     * This method checks to see if player and won or lost opening the appropriate screen
     * @author Matthew Breen
     */
    protected void deathOrWinCheck(){
        if(enemyHealthBar.getValue() <= HEALTH_BAR_MIN){
            isVisible =false;
            gameEnvironment.screenManager.winScreen.display(enemy);
        }
        else if(playerHealthBar.getValue() <= HEALTH_BAR_MIN){
            gameEnvironment.screenManager.deathScreen.setVisible(true);
        }
    }

    /**
     * This method updates a health bar for a fightable object gradually changing the health each update
     * @author Matthew Breen
     */
    protected void processHealthBar(Bar bar, Fightable object, ElapsedTime elapsedTime){
        bar.update(elapsedTime);
        if(bar.getValue()-HEALTH_BAR_MIN > (int)(object.getHealth()/object.getMaxHealth()*MAX_PERCENT))
            bar.addValue(-1);
        else if(bar.getValue()-HEALTH_BAR_MIN < (int)(object.getHealth()/object.getMaxHealth()*MAX_PERCENT))
            bar.addValue(1);
    }

    /**
     * This method draws the gui for the fight screen
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used for draw
     * @param graphics2D IGraphics2D used for draw
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(0, 206, 209));
        runAwayButton.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        fightButton.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        itemButton.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);

        Vector2 textLocation = new Vector2();

        if(enemy != null) {
            enemy.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
            enemyHealthBar.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
            ViewportHelper.convertLayerPosIntoScreen(mDefaultLayerViewport,width*0.3f,height*0.92f, mDefaultScreenViewport,textLocation);
            graphics2D.drawText(enemy.getName(), textLocation.x,textLocation.y, paint);
        }

        playerAnimationManager.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        playerHealthBar.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        ViewportHelper.convertLayerPosIntoScreen(mDefaultLayerViewport,width*0.3f,height*0.37f, mDefaultScreenViewport,textLocation);
        graphics2D.drawText(player.getName(), textLocation.x,textLocation.y, paint);
        fightHandler.drawText(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
    }
}
