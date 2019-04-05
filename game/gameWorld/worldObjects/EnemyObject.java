package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.fight.Fightable;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;

/**
 * <h1>EnemyObject</h1>
 * Class for handling opening the fight screen on collision with the player
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public class EnemyObject extends CollidableObject {

    private int chanceOfEnemy;
    protected Random random;
    private ArrayList<String> enemies;

    /**
     * This constructor declares an enemy with a static image with the following parameters
     * @author Matthew Breen
     * @param x float is x position of the enemy object image
     * @param y float is y position of the enemy object image
     * @param width float is the width of the enemy object image
     * @param height float is the height of the enemy object image
     * @param bitmap Bitmap is the bitmap for enemy object image
     * @param gameScreen GameScreen is the game screen creating this object
     * @param chance int is the percentage chance of opening fight screen on collision
     * @param enemies ArrayList<String> is the enemies this object can open a fight screen with
     * @param solid Boolean is whether the player can walk through the object or not
     */
    public EnemyObject(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, int chance, ArrayList<String> enemies, Boolean solid) {
        super(x, y, width, height, bitmap, gameScreen);
        this.random = new Random();
        this.chanceOfEnemy = chance;
        this.enemies = enemies;
        this.solid = solid;
    }

    /**
     * This method opens the fight screen with a random chance decided by chance on first collision with player
     * @author Matthew Breen
     * @param obj GameObject the object colliding with this for first time
     */
    @Override
    public boolean onFirstCollision(GameObject obj) {
        if(obj instanceof Player){
            int chance = random.nextInt(100);
            if (chance <= chanceOfEnemy) {
                ((GameEnvironment) mGameScreen).screenManager.setFightScreen(enemies.get(random.nextInt(enemies.size())));
            }
        }
        return true;
    }
}
