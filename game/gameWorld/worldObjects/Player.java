package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.Animation;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.fight.AttackMove;
import uk.ac.qub.eeecs.game.fight.Effect;
import uk.ac.qub.eeecs.game.fight.Fightable;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.inventory.EquippableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.io.playerControl.DPadControl;
/**
 * <h1>Player</h1>
 * Class for handling player updates, including current gameWorld,
 * fight properties, movement and animation
 *
 * @author  Matthew Breen & Shannon Turley
 * @version 1.0
 */
public class Player extends CollidableObject implements Animated, Fightable {
    private String gameWorld;
    protected AnimationManager animationManager;

    protected final float WALK_SPEED = 5.0f;
    protected final float MAX_HEALTH = 100;
    protected final int MAX_PERCENT = 100;

    protected boolean stopMovement;

    //stores the last object that collided with player
    public GameObject lastCollidedWith;

    protected float armour;
    protected float currentArmour;
    protected float currentHealth;
    protected float evade;
    protected float currentEvade;

    private Inventory inventory;
    protected ArrayList<AttackMove> attackMoves;
    protected AttackMove basicAttack;
    protected ArrayList<Effect> currentEffects;

    protected Random random;

    /**
     * Constructor initialises animation, gameworld, attack and movement properties
     * @param startX float staring x position
     * @param startY float starting y position
     * @param gameScreen GameScreen to render player to
     * @param gameWorld int id of GameWorld to begin in
     */
    public Player(float startX, float startY, GameScreen gameScreen, String gameWorld, float health, float evade, float armour) {
        super(startX, startY, 30, 40, null, gameScreen);
        animationManager = new AnimationManager(this);
        animationManager.addAnimation("txt/animation/CharacterAnimation2.JSON");
        animationManager.setCurrentAnimation("IdleDown");
        this.gameWorld = gameWorld;
        stopMovement = false;
        this.attackMoves = new ArrayList<>();
        if(health < MAX_HEALTH)
            this.currentHealth = health;
        else
            this.currentHealth = MAX_HEALTH;
        this.evade = evade;
        this.currentEvade = evade;
        this.armour = armour;
        this.currentArmour = armour;
        currentEffects = new ArrayList<>();
        random = new Random();
    }

    /**
     * Initialises Player with a starting position and game screen to render to
     * @param startX starting x position
     * @param startY starting y position
     * @param gameScreen game screen to render to
     * @author Shannon Turley
     */
    public Player(int startX, int startY, GameScreen gameScreen){
        super(startX, startY, 30, 40, null, gameScreen);
        random = new Random();
    }

    /**
     * Initialises Player with a game screen to render to
     * @param gameScreen game screen to render to
     * @author Matthew Breen
     */
    public Player(GameScreen gameScreen) {
        super(0, 0, 30, 40, null, gameScreen);
    }

    /**
     * Gets the int id of the game world the player is currently in
     * @return int game world
     * @author Ben Andrew
     */
    public String getGameWorld() {
        return gameWorld;
    }

    /**
     * Sets the game world the player is currently in
     * @param gameWorld
     * @author Ben Andrew
     */
    public void setGameWorld(String gameWorld) {
        this.gameWorld = gameWorld;
    }

    /**
     * Updates the player based on the elapsed time, using the dpad control to process any movement
     * on screen. If player is not allowed to move, they will stand still. Otherwise they will move
     * and animate in the direction they are walking in.
     * Update also updates the attack values of the player.
     * @param elapsedTime time elapsed since last update
     * @param dPadControl D Pad Control
     * @author Shannon Turley
     */
    public void update(ElapsedTime elapsedTime, DPadControl dPadControl) {
        if (stopMovement) {
            processAnimation(0, 0, elapsedTime);
        } else {
            processMovement(dPadControl);
            processAnimation(dPadControl.getXDirection(), dPadControl.getYDirection(), elapsedTime);
        }
        super.update(elapsedTime);
        updateAttackValues();
    }

    /***
     * Updates the player's attack values based on equipped items in inventory.
     * @author Shannon Turley and Matthew Breen
     */
    private void updateAttackValues() {
        if (inventory.getEquipped(EquippableItem.Category.ARMOUR) != null)
            armour = inventory.getEquipped(EquippableItem.Category.ARMOUR).getArmour();
        else{
            armour = 0;
        }
        if(attackMoves.size() > 0) {
            if (inventory.getEquipped(EquippableItem.Category.WEAPON) != null) {
                attackMoves.set(0, inventory.getEquipped(EquippableItem.Category.WEAPON).getWeaponMove());
            } else {
                attackMoves.set(0, basicAttack);
            }
        }
    }

    /**
     * Draws the character's current animation
     * @param elapsedTime time elapsed since last update
     * @param graphics2D graphics 2d
     * @param layerViewport layer viewport to draw to
     * @param screenViewport screen viewport to draw to
     * @author Shannon Turley
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {
        animationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
    }

    /**
     * Get the currently playing animation of the player
     * @author Shannon Turley
     * @return Animation currently playing animation
     */
    @Override
    public Animation getCurrentAnimation() {
        return animationManager.getCurrentAnimation();
    }

    /**
     * Get index of the current frame of the current animation
     * @author Shannon Turley
     * @return int current frame index
     */
    @Override
    public int getCurrentFrame() {
        return animationManager.getCurrentAnimation().getCurrentFrame();
    }

    /***
     * Ensures the player can't leave the world boundaries by adjusting its position on reaching
     * boundaries
     * @param worldBoundaries int array of world boundaries
     */
    public void confinePlayerToWorld(int[] worldBoundaries) {
        if (getBound().getLeft() < worldBoundaries[0])
            position.x = worldBoundaries[0] + 15.0f;
        else if (getBound().getRight() > worldBoundaries[1])
            position.x = worldBoundaries[1] - 15.0f;

        if (getBound().getBottom() < worldBoundaries[3])
            position.y = worldBoundaries[3] + 20.0f;
        else if (getBound().getTop() > worldBoundaries[2])
            position.y = worldBoundaries[2] - 20.0f;
    }

    /***
     * Moves the player according to dpad control
     * @author Shannon Turley
     * @param dPadControl DPadControl object to receiving user input
     */
    private void processMovement(DPadControl dPadControl) {
        position.x += dPadControl.getXDirection() * WALK_SPEED;
        position.y += dPadControl.getYDirection() * WALK_SPEED;
    }

    /***
     * Processes character animation - if x or y direction is > 0,
     * character will have a walking animation, otherwise an idle animation
     * @author Shannon Turley
     * @param x float x direction
     * @param y float y direction
     * @param elapsedTime time elapsed since last update
     */
    private void processAnimation(float x, float y, ElapsedTime elapsedTime) {
        if (x != 0 || y != 0) {
            processWalkingAnimation(x, y, elapsedTime);
        } else {
            processIdleAnimation(elapsedTime);
        }
        animationManager.update(elapsedTime);
    }

    /**
     * Plays a walking animation in the direction the player is walking
     * @author Shannon Turley
     * @param x float x direction
     * @param y float y direction
     * @param elapsedTime time elapsed since last update
     */
    protected void processWalkingAnimation(float x, float y, ElapsedTime elapsedTime) {
        if (x != 0) {
            if (x == 1) animationManager.play("WalkRight", elapsedTime);
            else animationManager.play("WalkLeft", elapsedTime);
        } else if (y != 0) {
            if (y == 1) animationManager.play("WalkUp", elapsedTime);
            else animationManager.play("WalkDown", elapsedTime);
        }
    }

    /**
     * Play an idle animation in the direction the player was previously walking
     * @author Shannon Turley
     * @param elapsedTime time elapsed since last update
     */
    protected void processIdleAnimation(ElapsedTime elapsedTime) {
        String currentAnimation = animationManager.getCurrentAnimation().getName();
        if (currentAnimation.contains("Walk")) {
            String walkDirection = currentAnimation.split("k")[1];
            animationManager.play("Idle" + walkDirection, elapsedTime);
        }
    }

    /**
     * This method returns the name of the player
     * @author Matthew Breen
     * @return String is the name of the player for text in fight system
     */
    @Override
    public String getName() {
        return "Player";
    }

    /**
     * This method returns whether an attack would hit the player or not based on a random chance related to the current evade
     * @author Matthew Breen
     * @return Boolean is whether it hit or not
     */
    @Override
    public boolean isHit() {
        int chance = random.nextInt(MAX_PERCENT)+1;
        if (chance > currentEvade)
            return true;
        else
            return false;
    }

    /**
     * This method adds an effect to the player if it's number of turns is 0 the health change is done instantly
     * @author Matthew Breen
     * @param effect Effect is the effect of an attack move
     */
    @Override
    public void addEffect(Effect effect) {
        if(effect.getNumTurns() > 0)
            currentEffects.add(effect.copy());
        else{
            evade += effect.getEvadeChange();
            armour += effect.getArmourChange();
            if(effect.getHealthChange() < 0){
                currentHealth += effect.getHealthChange() * (1 - (armour / MAX_PERCENT));
            }
            else
                heal(effect.getHealthChange());
        }
    }

    /**
     * This method processes the current effects of the player and returns messages if necessary.
     * It processes the number of remaining turns and if it has no turns remaining deletes it
     * If not it processes the effect on the enemy and returns messages.
     * @author Matthew Breen
     * @return ArrayList<String> is the messages returned
     */
    @Override
    public ArrayList<String> processEffects() {
        ArrayList<String> messages = new ArrayList<>();
        ArrayList<Effect> removed = new ArrayList<>();
        currentEvade = evade;
        currentArmour = armour;
        for(Effect effect : currentEffects){
            effect.processTurn();
            if(effect.getNumTurns() < 0){
                if(!effect.getEndMessage().isEmpty())
                    messages.add(effect.getEndMessage());
                removed.add(effect);
            }
            else
                currentArmour += effect.getArmourChange();
        }
        for(Effect effect : removed){
            currentEffects.remove(effect);
        }
        for(Effect effect : currentEffects) {
            if(!effect.getTurnMessage().isEmpty()){
                messages.add(effect.getTurnMessage());
            }
            currentEvade += effect.getEvadeChange();
            if(effect.getHealthChange() < 0){
                currentHealth += effect.getHealthChange() * (1 - (currentArmour / MAX_PERCENT));
            }
            else
                heal(effect.getHealthChange());
        }
        return messages;
    }

    /**
     * This method adds health passed to the players health and if it is over max health health is set to max health
     * @author Matthew Breen
     * @param health float is the amount you want to heal
     */
    @Override
    public void heal(float health) {
        if(health > 0) {
            this.currentHealth += health;
            if (currentHealth > MAX_HEALTH)
                currentHealth = MAX_HEALTH;
        }
    }

    /**
     * This method removes all effects and resets evade and armour
     * @author Matthew Breen
     */
    public void resetEffects(){
        currentEvade = evade;
        currentArmour = armour;
        currentEffects = new ArrayList<>();
    }

    /**
     * This method returns the current health of the player
     * @author Matthew Breen
     * @return float is the current health of the player
     */
    @Override
    public float getHealth() {
        return currentHealth;
    }

    /**
     * This method returns the max health of the player
     * @author Matthew Breen
     * @return float is the max health of the player
     */
    @Override
    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    /**
     * This method returns the current evade of the player
     * @author Matthew Breen
     * @return float is the current evade of the player
     */
    @Override
    public float getEvade() {
        return currentEvade;
    }

    /**
     * This method returns the current armour of the player
     * @author Matthew Breen
     * @return float is the current armour of the player
     */
    public float getArmour() {
        return currentArmour;
    }

    /**
     * This method sets the whether the player can move or not
     * @author Matthew Breen
     * @param stopMovement Boolean is whether the player can move or not
     */
    public void setStopMovement(boolean stopMovement) {
        this.stopMovement = stopMovement;
    }

    /**
     * Get the inventory associated with the player
     * @return inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Set the inventory associated with the player
     * @param inventory Inventory to be set
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * This method returns the attack move of the player
     * @author Matthew Breen
     * @return ArrayList<AttackMove> is the moves the player can use during a fight
     */
    public ArrayList<AttackMove> getAttackMoves() {
        return attackMoves;
    }

    /**
     * This method sets the attack move of the player
     * @author Matthew Breen
     * @param attackMoves  ArrayList<AttackMove> is the moves the player can use during a fight
     */
    public void setAttackMoves(ArrayList<AttackMove> attackMoves) {
        if(attackMoves.size() > 0)
            this.basicAttack = attackMoves.get(0);
        this.attackMoves = attackMoves;
    }

    public void setCurrentHealth(float health){
        this.currentHealth = health;
    }
}