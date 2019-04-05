package uk.ac.qub.eeecs.game.fight;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;

/**
 * <h1>Enemy</h1>
 * Class is for an enemy for the player to fight. So processes fight calculations, stats and animations for the enemy.
 *
 * @author  Matthew Breen & Shannon Turley
 * @version 1.0
 */
public class Enemy extends GameObject implements Fightable {

    protected final int MAX_PERCENT = 100;

    protected Random random;

    private String name;

    protected float armour;
    protected float currentArmour;
    protected float maxHealth;
    protected float currentHealth;
    protected float evade;
    protected float currentEvade;
    private ArrayList<AttackMove> attackMoves;
    protected ArrayList<Effect> currentEffects;

    private ArrayList<PreviewItem> rewardItems;
    private int rewardMoney;

    private AnimationManager animationManager;

    /**
     * This constructor declares an enemy with a static image with the following parameters
     * @author Matthew Breen
     * @param name String is the name of the enemy
     * @param x float is x position of the enemy image
     * @param y float is y position of the enemy image
     * @param width float is the width of the enemy image
     * @param height float is the height of the enemy image
     * @param bitmap Bitmap is the bitmap for enemy image
     * @param gameScreen GameScreen is the game screen creating this object
     * @param health int is the max health of the enemy
     * @param evade int is the default evade of the enemy
     * @param attackMoves ArrayList<AttackMove> is the moves the enemy can use during a fight
     * @param rewardItems ArrayList<PreviewItem> is the items you get as a reward for defeating this enemy
     * @param rewardMoney int is the money you get as a reward for defeating this enemy
     */
    public Enemy(String name, float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, int health, int evade, ArrayList<AttackMove> attackMoves, ArrayList<PreviewItem> rewardItems,int rewardMoney) {
        super(x, y, width, height, bitmap, gameScreen);
        this.name = name;
        this.maxHealth = health;
        this.evade = evade;
        this.armour = 0;
        this.attackMoves = attackMoves;
        this.rewardMoney = rewardMoney;
        this.rewardItems = rewardItems;
        currentEffects = new ArrayList<>();
        currentHealth = maxHealth;
        currentEvade = evade;
        currentArmour = armour;
        random = new Random();
    }

    /**
     * This constructor declares an animated enemy with the following parameters
     * @author Matthew Breen & Shannon Turley
     * @param name String is the name of the enemy
     * @param x float is x position of the enemy image
     * @param y float is y position of the enemy image
     * @param width float is the width of the enemy image
     * @param height float is the height of the enemy image
     * @param gameScreen GameScreen is the game screen creating this object
     * @param health int is the max health of the enemy
     * @param evade int is the default evade of the enemy
     * @param attackMoves ArrayList<AttackMove> is the moves the enemy can use during a fight
     * @param rewardItems ArrayList<PreviewItem> is the items you get as a reward for defeating this enemy
     * @param rewardMoney int is the money you get as a reward for defeating this enemy
     */
    public Enemy(String name, float x, float y, float width, float height, GameScreen gameScreen, int health, int evade, ArrayList<AttackMove> attackMoves, ArrayList<PreviewItem> rewardItems,int rewardMoney) {
        super(x, y, width, height, null, gameScreen);
        this.name = name;
        this.maxHealth = health;
        this.evade = evade;
        this.armour = 0;
        this.attackMoves = attackMoves;
        this.rewardMoney = rewardMoney;
        this.rewardItems = rewardItems;
        currentEffects = new ArrayList<>();
        currentHealth = maxHealth;
        currentEvade = evade;
        currentArmour = armour;

        animationManager = new AnimationManager(this);
        animationManager.addAnimation("txt/animation/"+name+"Animation.JSON");

        random = new Random();
    }

    //for testing
    protected Enemy(GameScreen gameScreen){super(gameScreen);}

    /**
     * This method returns whether an attack would hit the enemy or not based on a random chance related to the current evade
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
     * This method adds an effect to the enemy if it's number of turns is 0 the health change is done instantly
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
            damage(effect.getHealthChange());
            heal(effect.getHealthChange());
        }
    }

    /**
     * This method processes the current effects of the enemy and returns messages if necessary.
     * It processes the number of remaining turns and if it has no turns remaining deletes it
     * If not, it processes the effect on the enemy and returns messages.
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
            damage(effect.getHealthChange());
            heal(effect.getHealthChange());
        }
        return messages;
    }

    /**
     * This method applies an armour modifier to damage and takes it off player
     * @author Matthew Breen
     * @param damage float is the amount of raw damage being taken by enemy
     */
    private void damage(float damage){
        if(damage < 0) {
            currentHealth += damage * (1 - (currentArmour / MAX_PERCENT));
        }
    }

    /**
     * This method adds health passed to the enemy's health and if it is over max health the enemies health is set to max health
     * @author Matthew Breen
     * @param health float is the amount you want to heal
     */
    @Override
    public void heal(float health) {
        if(health > 0) {
            this.currentHealth += health;
            if (currentHealth > maxHealth)
                currentHealth = maxHealth;
        }
    }

    /**
     * This method returns the current health of the enemy
     * @author Matthew Breen
     * @return float is the current health of the enemy
     */
    @Override
    public float getHealth() {
        return currentHealth;
    }

    /**
     * This method returns the max health of the enemy
     * @author Matthew Breen
     * @return float is the max health of the enemy
     */
    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    /**
     * This method returns the current evade of the enemy
     * @author Matthew Breen
     * @return float is the current evade of the enemy
     */
    @Override
    public float getEvade() {
        return currentEvade;
    }

    /**
     * This method returns the name of the enemy
     * @author Matthew Breen
     * @return String is the name of the enemy for text in fight system
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * This method returns the the enemies animation manager
     * @author Shannon Turely
     * @return AnimationManager is the enemies animation manager
     */
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * This method draws the enemy on the screen
     * @author Matthew Breen and Shannon Turley
     * @param elapsedTime ElapsedTime used for draw
     * @param graphics2D IGraphics2D used for draw
     * @param layerViewport LayerViewport used for draw
     * @param screenViewport ScreenViewport used for draw
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        if(animationManager != null)
            animationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        else
            super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
    }

    /**
     * This method returns the current armour of the enemy
     * @author Matthew Breen
     * @return float is the current armour of the enemy
     */
    public float getArmour() {
        return currentArmour;
    }

    /**
     * This method returns the attack move of the enemy
     * @author Matthew Breen
     * @return ArrayList<AttackMove> is the moves the enemy can use during a fight
     */
    public ArrayList<AttackMove> getAttackMoves() {
        return attackMoves;
    }

    /**
     * This method returns the reward items of the enemy
     * @author Matthew Breen
     * @return ArrayList<PreviewItem> is the items you get as a reward for defeating this enemy
     */
    public ArrayList<PreviewItem> getRewardItems() {
        return rewardItems;
    }

    /**
     * This method returns the reward money of the enemy
     * @author Matthew Breen
     * @return int is the money you get as a reward for defeating this enemy
     */
    public int getRewardMoney() {
        return rewardMoney;
    }
}
