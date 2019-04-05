package uk.ac.qub.eeecs.game.menu;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.ExternalStorageHandler;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameSave.SaveGameWriter;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>Pause Screen</h1>
 * A screen class used for user settings, such as entering debug mode, updates volume settings,
 * saving game and exiting current game to loading screen.
 *
 * @author  Kristina Geddis, Ben Andrew, Shannon Turley
 * @version 1.0
 */

public class PauseScreen extends GameScreen {
    //Private variables
    private SaveGameWriter saveGameWriter;
    protected PushButton resume, exit, debug, save, audioUp, audioDown;

    private Paint volumeBarPaint;
    private Paint backgroundVolumePaint;

    protected float volume;
    //Rectangle draw as the background for the volume bar
    private Rect backgroundVolume;
    private int spacingX, spacingY;

    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_WIDTH = 120;
    private static final int AUDIO_WIDTH = 50;

    private static final int VOLUME_BACKGROUND_COLOUR = Color.rgb(78, 135, 135);
    private static final int VOLUME_BAR_COLOUR = Color.rgb(130, 232, 67);

    private boolean visible = false;

    //Constructor for testing purposes
    public PauseScreen(Game game){
        super("PauseScreen", game);
    }

    /**
     * This constructor initialises PauseScreen with save game writer and all buttons and volume rectangles
     * @param game Game for current game
     * @param externalStorageHandler
     * @author  Kristina Geddis
     */
    public PauseScreen(Game game, ExternalStorageHandler externalStorageHandler) {
        super("PauseScreen", game);
        saveGameWriter = new SaveGameWriter(externalStorageHandler);

        this.spacingX = (int) mDefaultLayerViewport.getWidth();
        this.spacingY = (int) mDefaultLayerViewport.getHeight();

        backgroundVolumePaint = new Paint();
        backgroundVolumePaint.setColor(VOLUME_BACKGROUND_COLOUR);
        volumeBarPaint = new Paint();
        volumeBarPaint.setColor(VOLUME_BAR_COLOUR);

        this.volume = ((float) Math.round(mGame.getAudioManager().getMusicVolume() * 10)) / 10;

        resume = new PushButton(spacingX * 0.25f, spacingY * 0.8f, BUTTON_WIDTH, BUTTON_HEIGHT, "resume", "resumeSelected", this);
        save = new PushButton(spacingX * 0.25f, spacingY * 0.5f, BUTTON_WIDTH, BUTTON_HEIGHT, "save", "saveSelected", this);
        debug = new PushButton(spacingX * 0.75f, spacingY * 0.8f, BUTTON_WIDTH, BUTTON_HEIGHT, "debug", "debugSelected", this);
        exit = new PushButton(spacingX * 0.75f, spacingY * 0.5f, BUTTON_WIDTH, BUTTON_HEIGHT, "exit", "exitSelected", this);
        audioUp = new PushButton(spacingX * 0.8f, spacingY * 0.2f, AUDIO_WIDTH, BUTTON_HEIGHT, "volUp", "volUpColoured", this);
        audioDown = new PushButton(spacingX * 0.2f, spacingY * 0.2f, AUDIO_WIDTH, BUTTON_HEIGHT, "volDown", "volDownColoured", this);
        backgroundVolume = new Rect((int)(spacingX * 0.4f),(int)(spacingY * 0.8f) -15,(int)(spacingX * 0.8f),(int)(spacingY * 0.8f) +15);

    }

    /**
     * This method resumes to the main game screen (hides current screen) when the resume button is pushed
     * @author  Kristina Geddis
     */
    public void resumeGame() {
        if (resume.isPushTriggered()) {
            visible = false;
        }
    }

    /**
     * This method rounds the volume to one decimal place, and
     * changes the music volume depending on which pushbutton was triggered
     * @authod Kristina Geddis
     */
    public void changeVolume() {
        float currentVolume = ((float) Math.round(mGame.getAudioManager().getMusicVolume() * 10)) / 10;
        if (audioDown.isPushTriggered()) {
            if (currentVolume > 0) {
                this.volume = currentVolume - 0.1f;
                mGame.getAudioManager().setMusicVolume(volume);
            }
        } else if (audioUp.isPushTriggered()) {
            if (currentVolume < 1) {
                this.volume = currentVolume + 0.1f;
                mGame.getAudioManager().setMusicVolume(volume);
            }
        }
    }

    /**
     * This method calls writeSavedGame when the save button is pushed
     * @param player Player is used by writeSavedGame method
     * @author Ben Andrew
     */
    public void saveGame(Player player) {
        if (save.isPushTriggered()) {
            saveGameWriter.writeSavedGame(player);
        }
    }

    /**
     * This method calls toggleDebug when the debug button is pushed
     * @param gameEnvironment GameEnvironment is the object that has the toggleDebug method
     * @author Ben Andrew
     */
    public void debugGame(GameEnvironment gameEnvironment){
        if(debug.isPushTriggered()) {
            gameEnvironment.toggleDebug();
        }
    }

    /**
     * This method calls onBackPressed (exits game) and returns to the menu screen
     * when the exit button is pushed
     * @author Shannon Turley
     */
    public void exitGame() {
        if (exit.isPushTriggered()) {
            mGame.onBackPressed();
        }
    }

    /**
     * This method updates; resume, save, debug and exit buttons, and updates the music volume
     * @param elapsedTime ElapsedTime used for update methods
     * @param player Player used for save method
     * @param gameEnvironment GameEnvironment used for debug method
     * @author  Kristina Geddis
     */
    public void update(ElapsedTime elapsedTime, Player player, GameEnvironment gameEnvironment) {
        resumeGame();
        resume.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
        saveGame(player);
        save.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
        debugGame(gameEnvironment);
        debug.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
        exitGame();
        exit.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
        changeVolume();
        audioDown.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
        audioUp.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
    }

    @Override
    public void update(ElapsedTime elapsedTime) {

    }


    /**
     * This method draws;
     * -Buttons
     * -Rectangle for visually displaying the volume bar
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @author  Kristina Geddis
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(255, 218, 86));

        resume.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        save.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        debug.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        exit.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);

        audioDown.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        audioUp.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        graphics2D.drawRect(backgroundVolume, backgroundVolumePaint, mDefaultLayerViewport, mDefaultScreenViewport);
        Rect volumeBar = new Rect((int)(spacingX * 0.4f),(int)(spacingY * 0.8f) -15,(int)(spacingX * 0.4f) + (int)(volume * (spacingX * 0.4f)),(int)(spacingY * 0.8f) +15);
        graphics2D.drawRect(volumeBar, volumeBarPaint, mDefaultLayerViewport, mDefaultScreenViewport);
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
