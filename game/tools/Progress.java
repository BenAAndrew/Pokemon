package uk.ac.qub.eeecs.game.tools;

import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;

/*
 * <h1>Progress</h1>
 * Draws live progress of given task to screen
 * Utilised solely by ProgressBar
 *
 * @author Daniel Bell :)
 * @version 1.0
 */
public class Progress extends Sprite {
    /**
     * Properties
     */
    // minimumX - Minimum x value possible for progress when displayed
    private float minimumX;
    // maximumWidth - Maximum width possible for progress when displayed
    private float maximumWidth;

    // ********************************************************************************************

    /**
     * Constructor - for the Progress.
     *
     * @param x                      x Co-ordinate
     * @param y                      y Co-ordinate
     * @param width                  width of current Progress
     * @param height                 height of current Progress
     * @param progression            ProgressBar's current Progression; .png
     * @param mGameScreen            instance of GameScreen used by Progress
     * @author                       Daniel Bell :)
     */
    public Progress(float x, float y, float width, float height, String progression, GameScreen mGameScreen) {
        super(x, y, width, height, mGameScreen.getGame().getAssetManager().getBitmap(progression), mGameScreen);

        minimumX = x;
        maximumWidth = width;
    }

    // ********************************************************************************************

    /**
     * Accessor(s) & Mutator(s)
     * @author Daniel Bell :)
     */

    // minimumX - Accessor & Mutator (in-line functions)
    public float getMinimumX() { return minimumX; }
    public void setMinimumX(float minX) { this.minimumX = minX; }

    // maximumWidth - Accessor & Mutator (in-line functions)
    public float getMaximumWidth() { return maximumWidth; }
    public void setMaximumWidth(float maxWidth) { this.maximumWidth = maxWidth; }

    // ********************************************************************************************

    /**
     * Class Method(s)
     */

    /**
     * Calculates the Width of Progress for live update
     *
     * @param newWidth  updated live by calculating these vars
     * @author          Daniel Bell :)
     */
    public void setNewWidth(float newWidth) {
        // newXCoord - re-established X Co-ordinate of 'Green' Progress.
        // Width dimension can increase while 'Green' Progress stays put in the same position
        float newXCoord = 0;
        // newHalfWidth - for 'Green' Progress
        float newHalfWidth = 0;

        if ((newWidth >= 0) && (newWidth <= maximumWidth)) {
            newXCoord = (minimumX - (maximumWidth / 2) + (newWidth / 2));
            newHalfWidth = (newWidth / 2);
        } else if (newWidth >= maximumWidth) {
            newXCoord = minimumX;
            newHalfWidth = (maximumWidth / 2);
        }

        // Pushes revised value of X Co-ordinate to the 'Green' Progress
        position.x = newXCoord;
        this.setWidth(newWidth);

        // GameEnvironment.log.addLog("NewHalfWidth", "" + newHalfWidth);
        // D/NewHalfWidth: 10.67668

        // Assigns the newly created value of HalfWidth to the 'Green' Progress
        mBound.halfWidth = newHalfWidth;
    }

    /**
     * @param maxProgress       that ProgressBar can represent
     * @param currProgress      of the ProgressBar
     * @return newWidth         updated live by calculating these vars
     * @author                  Daniel Bell :)
     */
    // Re-calculates the Width of ProgressBar for live update
    // maxProgress - Originally duration
    // currProgress - Originally currentProgress
    public float calculateWidth(float maxProgress, float currProgress) {
        float newWidth = ((maximumWidth / maxProgress) * currProgress);
        // GameEnvironment.log.addLog("calculateWidth !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", " " + newWidth);
        return newWidth;
    }
}
