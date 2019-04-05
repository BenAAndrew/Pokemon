package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.Animation;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>AnimatedEnemyObject</h1>
 * Class for handling opening the fight screen on collision with the player with an animated game object
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public class AnimatedEnemyObject extends EnemyObject implements Animated{
    private AnimationManager animationManager;
    private String animationName;

    /**
     * This constructor declares an enemy with a static image with the following parameters
     * @author Matthew Breen
     * @param x float is x position of the enemy object image
     * @param y float is y position of the enemy object image
     * @param width float is the width of the enemy object image
     * @param height float is the height of the enemy object image
     * @param gameScreen GameScreen is the game screen creating this object
     * @param chance int is the percentage chance of opening fight screen on collision
     * @param enemies ArrayList<String> is the enemies this object can open a fight screen with
     * @param animationJSON String is the JSON file for the animation of this object
     * @param animationName String is the name of the animation in the JSON
     * @param solid Boolean is whether the player can walk through the object or not
     */
    public AnimatedEnemyObject(float x, float y, float width, float height, GameScreen gameScreen, int chance, ArrayList<String> enemies, String animationJSON, String animationName, Boolean solid) {
        super(x, y, width, height, null, gameScreen, chance, enemies, solid);
        this.animationManager = new AnimationManager(this);
        this.animationName = animationName;
        animationManager.addAnimation("txt/animation/"+animationJSON+".JSON");
    }

    /**
     * This method draws the animated enemy frame
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used for draw
     * @param graphics2D IGraphics2D used for draw
     * @param layerViewport LayerViewport used for draw
     * @param screenViewport ScreenViewport used for draw
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        if(Tools.checkOnScreen(this.mBound,layerViewport)) {
            animationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        }
    }

    /**
     * This method updates the animation each frame
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime needed for update methods
     */
    public void update(ElapsedTime elapsedTime){
        animationManager.play(animationName, elapsedTime);
        animationManager.update(elapsedTime);
    }

    /**
     * This method returns the current animation
     * @author Matthew Breen
     * @return Animation is the current animation
     */
    @Override
    public Animation getCurrentAnimation() {
        return animationManager.getCurrentAnimation();
    }

    /**
     * This method returns the current frame of the animation
     * @author Matthew Breen
     * @return int is the current frame of the animation
     */
    @Override
    public int getCurrentFrame() {
        return animationManager.getCurrentAnimation().getCurrentFrame();
    }
}
