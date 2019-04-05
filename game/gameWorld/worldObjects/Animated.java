package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import uk.ac.qub.eeecs.gage.engine.animation.Animation;

/**
 * <h1>Animated</h1>
 * Interface used for any animated object
 * @author Shannon Turley
 * @version 1.0
 */
public interface Animated {
    public Animation getCurrentAnimation();
    public int getCurrentFrame();
}