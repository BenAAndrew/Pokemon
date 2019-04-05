package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.CollisionDetector;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>CollidableObject</h1>
 * Class for handling a game object that can be collided with
 *
 * @author  Matthew Breen & Ben Andrew
 * @version 1.0
 */
public class CollidableObject extends GameObject {
    private GameObject seperateCollisionBox;
    protected Boolean solid = true;

    /**
     * This constructor declares an collidable object with the following parameters
     * @author Matthew Breen
     * @param x float is x position of the object image
     * @param y float is y position of the object image
     * @param width float is the width of the object image
     * @param height float is the height of the object image
     * @param bitmap Bitmap is the bitmap for object image
     * @param gameScreen GameScreen is the game screen creating this object
     */
    public CollidableObject(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, gameScreen);
    }

    public CollidableObject(float x, float y, float width, float height, float collisionHeightModifier, Bitmap bitmap, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, gameScreen);
        seperateCollisionBox = new GameObject(x, y-collisionHeightModifier, width, height-2*collisionHeightModifier, null, gameScreen);
    }

    /**
     * This method is called on a collision with a object
     * @author Matthew Breen
     * @param obj GameObject the object colliding with this
     */
    public void handleCollision(GameObject obj) {
        if(solid) {
            if (seperateCollisionBox != null) {
                CollisionDetector.determineAndResolveCollision(obj, seperateCollisionBox);
            } else {
                CollisionDetector.determineAndResolveCollision(obj, this);
            }
        }
    }

    /**
     * This method is called on the first collision with a object
     * @author Matthew Breen
     * @param obj GameObject the object colliding with this for first time
     */
    public boolean onFirstCollision(GameObject obj) {
        return true;
    }

    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport mGameLayerViewport, ScreenViewport mDefaultScreenViewport){
        if(Tools.checkOnScreen(this.mBound,mGameLayerViewport)){
            super.draw(elapsedTime,graphics2D,mGameLayerViewport,mDefaultScreenViewport);
        }
    }

    public GameObject getSeperateCollisionBox() {
        return seperateCollisionBox;
    }
}
