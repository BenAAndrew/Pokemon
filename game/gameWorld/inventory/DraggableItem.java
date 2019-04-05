package uk.ac.qub.eeecs.game.gameWorld.inventory;

import android.graphics.Bitmap;
import java.util.List;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
/**
 * <h1>Draggable item</h1>
 * A class used for enabling items to be dragged and long pressed in inventory
 *
 * @author  Kristina Geddis
 * @version 1.0
 */

public class DraggableItem extends GameObject {
    //Private variables
    private float startingX, startingY;
    private float yChange = 0;
    protected boolean isTapped = false;
    protected boolean isDragged = false;

    /**
     * This constructor is used to initialise DraggableItem as a game object and makes a copy of its starting position
     * @param x float used to set the x position of the draggable item
     * @param y float used to set the y position of the draggable item
     * @param width float used to set the width of the draggable item
     * @param height float used to set the height of the draggable item
     * @param bitmap Bitmap used to set the image of the item
     * @param gameScreen GameScreen used for InventoryScreen
     * @author Kristina geddis
     */
    public DraggableItem(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, gameScreen);
        this.startingX = x;
        this.startingY = y;
    }

    /**
     * This method updates and processes input touch handling
     * @param elapsedTime ElapsedTime used for update method
     * @param layerViewport LayerViewport used for handleTouchEvent method
     * @param screenViewport ScreenViewport used for handleTouchEvent method
     * @author Kristina Geddis
     */
    public void update(ElapsedTime elapsedTime, LayerViewport layerViewport, ScreenViewport screenViewport){
        super.update(elapsedTime);
        List<TouchEvent> touchEvents = mGameScreen.getGame().getInput().getTouchEvents();
        for(TouchEvent touchEvent: touchEvents) {
            handleTouchEvent(touchEvent, layerViewport, screenViewport);
        }

    }

    /**
     * This method decides which touchEvent has been triggered on the draggable item i.e. if a drag event then we set the
     * boolean flag of isDragged to be true. It also makes sure that if an item is being dragged then
     * the position of the item also changes. Also checks for long presses and releasing presses.
     * @param touchEvent TouchEvent used to identify touch event type and position
     * @param layerViewport LayerViewport used to convert touch event position into screen position
     * @param screenViewport ScreenViewport used to convert touch event position into screen position
     * @author Kristina Geddis
     */
    public void handleTouchEvent(TouchEvent touchEvent, LayerViewport layerViewport, ScreenViewport screenViewport){
        Vector2 touchLocation = new Vector2();
        ViewportHelper.convertScreenPosIntoLayer(screenViewport, touchEvent.x, touchEvent.y, layerViewport, touchLocation);
        if(touchEvent.type == TouchEvent.TOUCH_DRAGGED && mBound.contains(touchLocation.x, touchLocation.y)){
            isDragged = true;
        }else if(touchEvent.type == TouchEvent.TOUCH_LONG_PRESS && mBound.contains(touchLocation.x, touchLocation.y)){
            isTapped = true;
        }
        else if(touchEvent.type == TouchEvent.TOUCH_UP){
            isDragged = false;
            isTapped = false;
        }

        if(isDragged){
            position.x = touchLocation.x;
            position.y = touchLocation.y;
        } else {
            position.x = startingX;
            position.y = startingY+yChange;
        }
    }

    /**
     * This method sets the starting position. Used when redeclaring item position such as when an item
     * is equipped
     * @param startingX float to set starting x
     * @param startingY float to set starting y
     * @author Kristina Geddis
     */
    public void setPosition(float startingX, float startingY) {
        this.startingX = startingX;
        this.startingY = startingY;
    }

    /**
     * This method returns whether tapped
     * @return boolean of whether tapped
     * @author Kristina Geddis
     */
    public boolean isTapped() {
        return isTapped;
    }

    /**
     * This method returns whether dragged
     * @return boolean of whether dragged
     * @author Kristina Geddis
     */
    public boolean isDragged() {
        return isDragged;
    }

    /**
     * This method calculates the bounding box for snappingMap and returns whether an item overlaps with it
     * @param object GameObject to be checked whether overlapping with draggable item
     * @return boolean to whether overlapping
     * @author Kristina Geddis
     */
    public boolean overlaps(GameObject object){
        return getBound().getLeft() < object.getBound().getRight() && getBound().getRight() > object.getBound().getLeft() &&
                getBound().getBottom() < object.getBound().getTop() && getBound().getTop() > object.getBound().getBottom();
    }

    /**
     * This method changes the y position of the draggable item. This is used when the screen is scrolled
     * @param y float amount of change in y
     * @author Kristina Geddis
     */
    public void addToYDifference(float y){
        this.yChange += y;
    }
}
