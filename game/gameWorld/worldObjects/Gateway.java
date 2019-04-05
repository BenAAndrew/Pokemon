package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;

/**
 * <h1>Gateway</h1>
 * Class for transporting the player to other worlds
 * on collision. Specifies the world Index and player
 * position in the new world. Uses reloadWorld in GameEnvironment
 * to execute changes
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class Gateway extends CollidableObject{
    private String worldKey;
    private int playerX, playerY;
    private GameEnvironment gameEnvironment;

    /**
     * This constructor declares Gateway with a worldKey, playerX & playerY
     * for reloading the world and player position on collision.
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for gateway width
     * @param height float for gateway height
     * @param bitmap Bitmap image for gateway
     * @param gameScreen GameScreen for parent CollidableObject
     * @param worldKey String for new world name
     * @param playerX int for player x position in new world
     * @param playerY int for player y position in new world
     */
    public Gateway(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, String worldKey, int playerX, int playerY) {
        super(x, y, width, height, bitmap, gameScreen);
        this.worldKey = worldKey;
        this.playerX = playerX;
        this.playerY = playerY;
        this.gameEnvironment = ((GameEnvironment) mGameScreen);
    }

    @Override
    public void handleCollision(GameObject obj){ }

    /**
     * Overrides onFirstCollision to reload world if
     * colliding with a player
     * @author Ben Andrew
     * @param obj GameObject to determine whether a
     * player.
     * @return boolean to whether reload was called
     */
    @Override
    public boolean onFirstCollision(GameObject obj) {
        if (obj instanceof Player) {
            gameEnvironment.loadWorld(worldKey,playerX,playerY);
            return true;
        }
        return false;
    }

}
