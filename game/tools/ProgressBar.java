package uk.ac.qub.eeecs.game.tools;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/*
 * <h1>ProgressBar</h1>
 * Acts as intermediary for constructing an instance of GameObject, for base 'Red' ProgressBar;
 * and instance of Sprite for the dynamic, for overlaying 'Green' Progress.
 * Utilised solely by SplashScreen
 *
 * @author Daniel Bell :)
 * @version 1.0
 */
public class ProgressBar extends GameObject {
    /**
     * Properties
     */
    // progress - Utilised to Draw the current Progress on-screen
    private Progress progress;
    // currentProgress - live
    private float currentProgress;
    // duration - maximum Progress that is possible for the ProgressBar (2sec)
    private float duration;

// ************************************************************************************************

    /**
     * Constructor - Creates a ProgressBar
     *
     * @param x                      x Co-ordinate
     * @param y                      y Co-ordinate
     * @param width                  width of ProgressBar
     * @param height                 height of ProgressBar
     * @param progressBarBackground  ProgressBar background; .png
     * @param progression            ProgressBar's current Progression; .png
     * @param duration               current value this bar is representing
     * @param mGameScreen            instance of GameScreen used by ProgressBar
     * @author                       Daniel Bell :)
     */
    public ProgressBar(float x, float y, float width, float height, String progressBarBackground, String progression, float duration, GameScreen mGameScreen) {
        super(x, y, width, height, mGameScreen.getGame().getAssetManager().getBitmap(progressBarBackground), mGameScreen);

        this.currentProgress = 0.0f;
        this.duration = duration;
        this.progress = new Progress(x, y, width, height, progression, mGameScreen);
    }

    // ********************************************************************************************

    /**
     * Accessor(s) & Mutator(s)
     * @author Daniel Bell :)
     */

    // progress - Accessor & Mutator (in-line functions)
    public Progress getProgress() { return progress; }
    public void setProgress(Progress prog) { this.progress = prog; }

    // currentProgress - Accessor & Mutator (in-line functions)
    public float getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currProgress) {
        this.currentProgress = currProgress;
    }

    // maxProgress - Accessor & Mutator (in-line functions)
    public float getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // ********************************************************************************************

    /**
     * Class Method(s)
     */

    /**
     * In effect, enables GREEN Progress bitmap to move from Left -> Right
     * Updates new width for appearing on-screen
     * @param newProgress   Originally(float)timeSinceStart
     * Future Expansion:-   MAXIMUM_TIME
     * @author              Daniel Bell :)
     */
    public void updateProgress(float newProgress) {
        // Can add MAXIMUM_TIME as parameter for Future Expansion, (e.g. variating value)
        if (newProgress > 0) {
            // Assigns latest update of progress made to our referenced var. currentProgress, for these calculations
            this.currentProgress = newProgress;
            // Where latest value of currentProgress is used to re-determine our ProgressBar's width
            progress.setNewWidth(progress.calculateWidth(duration, newProgress));

            // Below line of code, 'Green' Progress appears over ProgressBar
            // progress.setNewWidth(newProgress);
        }
    }

    /**
     * Compares value of currentProgress with maxProgress; to determine if loading has finished
     * @author              Daniel Bell :)
     */
    public boolean isDone() {
        return currentProgress == duration;
    }

    // ********************************************************************************************

    /**
     * Abstract Override Method(s)
     */

    /**
     * Updates Time passing and Progress on top of ProgressBar
     * @param elapsedTime       ElapsedTime used for update methods
     * @author                  Daniel Bell :)
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);
        updateProgress((float)elapsedTime.totalTime);
        this.progress.update(elapsedTime);
    }

    /**
     * Draws background and Progress on-screen
     * @param elapsedTime       ElapsedTime required for draw methods
     * @param graphics2D        IGraphics2D required for draw methods
     * @author                  Daniel Bell :)
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        super.draw(elapsedTime, graphics2D);
        this.progress.draw(elapsedTime, graphics2D);
        // System.out.println(this.progress.getWidth());
    }
}
