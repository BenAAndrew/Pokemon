package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.Animation;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>Animated Object</h1>
 * Class for implementing Animation on CollidableObjects in the world.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class AnimatedObject extends CollidableObject implements Animated{
    private AnimationManager animationManager;
    private String animationName;

    /**
     * This constructor declares the parent CollidableObject and also
     * the AnimationManager with the JSON name and starting animation.
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for object width
     * @param height float for object height
     * @param gameScreen GameScreen for parent CollidableObject
     * @param animationJSON String for setting AnimationManager Animation JSON
     * @param animationName String for setting animation name
     */
    public AnimatedObject(float x, float y, float width, float height, GameScreen gameScreen, String animationJSON, String animationName) {
        super(x, y, width, height, null, gameScreen);
        this.animationManager = new AnimationManager(this);
        this.animationName = animationName;
        animationManager.addAnimation("txt/animation/"+animationJSON+".JSON");
    }

    /**
     * Draws the object with animation manager when the object is on screen
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime for AnimationManager draw
     * @param graphics2D Graphics2D for AnimationManager draw
     * @param layerViewport LayerViewport for AnimationManager draw
     * @param screenViewport ScreenManager for AnimationManager draw
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        if(Tools.checkOnScreen(this.mBound,layerViewport)) {
            animationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        }
    }

    /**
     * Updates AnimationManager setting current animation to animation name
     * and calling AnimationManager update
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime for AnimationManager play
     */
    public void update(ElapsedTime elapsedTime){
        animationManager.play(animationName, elapsedTime);
        animationManager.update(elapsedTime);
    }

    @Override
    public Animation getCurrentAnimation() {
        return animationManager.getCurrentAnimation();
    }

    @Override
    public int getCurrentFrame() {
        return animationManager.getCurrentAnimation().getCurrentFrame();
    }
}
