package uk.ac.qub.eeecs.game.gameWorld.inventory;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.Animation;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Animated;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.CollidableObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>World Item</h1>
 * Class for creating items present in the GameWorld. Holds state
 * and type for the item, is picked up by player on collision and can be animated.
 * Holds a PreviewItem for item properties when in inventory.
 *
 * @author Ben Andrew & Shannon Turley
 * @version 1.0
 */
public class WorldItem extends CollidableObject implements Animated{
    //tracks items current state
    protected enum State {IN_WORLD, PICKED_UP, IN_INVENTORY}
    //only money has a separate behaviour so is defined in type
    protected enum Type {GENERAL, MONEY}
    protected State currentState = State.IN_WORLD;
    protected Type type = Type.GENERAL;
    //PreviewItem holding items properties which is what is actually stored in inventory
    private PreviewItem previewItem;

    //animated items only
    private AnimationManager animationManager;
    private String animationName;

    /**
     * This constructor declares parent CollidableObject, AnimationManager if applicable
     * and also sets PreviewItem
     * @author Ben Andrew
     * @param x float of item x position
     * @param y float of item y position
     * @param width float of item width
     * @param height float of item height
     * @param game GameScreen for parent CollidableObject
     * @param previewItem PreviewItem for image/animation details and stored for item information
     */
    public WorldItem(float x, float y, float width, float height, GameScreen game, PreviewItem previewItem) {
        super(x, y, width, height, previewItem.getImage(), game);
        if(previewItem.getAnimationJson() != null){
            this.animationManager = new AnimationManager(this);
            this.animationName = previewItem.getAnimationName();
            animationManager.addAnimation("txt/animation/"+previewItem.getAnimationJson()+".JSON");
            if(previewItem.getName().equalsIgnoreCase("money")){
                type = Type.MONEY;
            }
        }
        this.previewItem = previewItem;
    }

    /**
     * Update checks when item is picked up to add to inventory either as
     * an item or to add to money. also updates animation and parent CollidableObject
     * @author Ben Andrew & Shannon Turley
     * @param elapsedTime ElapsedTime used in update methods
     * @param inventory Inventory to add PreviewItems to on pickup
     */
    public void update(ElapsedTime elapsedTime, Inventory inventory) {
        //Shannon Turley
        if (currentState.equals(State.PICKED_UP)) {
            currentState = State.IN_INVENTORY;
            //Ben Andrew
            if(type == Type.GENERAL)
                inventory.addItem(previewItem);
            else
                inventory.transaction(1);
        }
        //Ben Andrew
        if(animationManager != null){
            animationManager.play(animationName, elapsedTime);
            animationManager.update(elapsedTime);
        }
        super.update(elapsedTime);
    }

    /**
     * Draws object if within screen bounds and is in world.
     * @author Ben Andrew & Shannon Turley
     * @param elapsedTime ElapsedTime used in parent/ animation draw method
     * @param graphics2D IGraphics2D used in parent/ animation draw method
     * @param layerViewport LayerViewport used in parent/ animation draw method
     * @param screenViewport ScreenViewport used in parent/ animation draw method
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport){
        //Ben Andrew
        if(Tools.checkOnScreen(this.mBound,layerViewport) && currentState == State.IN_WORLD) {
            if(animationManager == null){
                //Shannon Turley
                super.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
            } else {
                animationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
            }
        }
    }

    @Override
    public void handleCollision(GameObject obj){

    }

    /**
     * Changes item state to picked up the first time a player collides with the item
     * @author Shannon Turley
     * @param obj GameObject to be checked whether a player has collided
     */
    @Override
    public boolean onFirstCollision(GameObject obj) {
        if (currentState.equals(State.IN_WORLD)) {
            if (obj instanceof Player) {
                currentState = State.PICKED_UP;
            }
        }
        return true;
    }

    /**
     * Returns the currently playing animation of this World Item
     * @return Animation current animation
     */
    @Override
    public Animation getCurrentAnimation() {
        return animationManager.getCurrentAnimation();
    }

    /**
     * Returns the current frame of the current animation of this World Item
     * @return int current animation frame number
     */
    @Override
    public int getCurrentFrame() {
        return animationManager.getCurrentAnimation().getCurrentFrame();
    }
}
