package uk.ac.qub.eeecs.game.menu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.ExternalStorageHandler;

/**
 * <h1>Menu Screen</h1>
 * Class for main menu screen with play and load buttons as well as loading
 * assets, playing music & animating the background with an image.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class MenuScreen extends GameScreen {
    private Paint paint;
    private final String MUSIC_NAME = "BackgroundMusic";

    //button properties
    private PushButton play;
    private PushButton load;
    private final String[] PLAY_BUTTON_NAME = {"play", "playSelected"};
    private final float PLAY_WIDTH = 0.45f;
    private final String LOAD_BUTTON_DISABLED = "loadDisabled";
    private final String[] LOAD_BUTTON_NAME = {"load", "loadSelected"};
    private final float LOAD_WIDTH = 0.4f;
    private final float BUTTON_Y_DIFFERENCE = 0.15f;
    private final float HEIGHT = 0.2f;

    //constants of background image details
    private final String IMAGE_NAME = "Pokemon";
    protected final int ON_SCREEN_IMAGE_WIDTH = 1200;
    protected final int IMAGE_WIDTH = 1439;
    private final int IMAGE_HEIGHT = 878;

    //properties for background image animation
    private Rect dest;
    protected int left = 0;
    private Bitmap background;
    protected boolean directionRight = true;

    protected ExternalStorageHandler externalStorageHandler;
    private final String GAME_SAVE = "gameSave.JSON";
    private boolean saveFound = false;

    //for testing purposes
    public MenuScreen(Game game, boolean saveFound){
        super("MenuScreen", game);
        this.saveFound = saveFound;
    }

    /**
     * This constructor declares the parent GameScreen and also creates the
     * buttons and background image. Also loads assets and plays music and checks if a save is found
     * to enable / disable load button.
     * @author Ben Andrew
     * @param game Game to declare parent GameScreen
     */
    public MenuScreen(Game game) {
        super("MenuScreen", game);

        // Load in the bitmaps used on the main menu screen
        mGame.getAssetManager().loadAssets("txt/assets/Assets.JSON");

        int w = (int)mDefaultLayerViewport.getWidth();
        int h = (int)mDefaultLayerViewport.getHeight();

        //used to check if save is present and later passed to GameEnvironment
        externalStorageHandler = new ExternalStorageHandler(game.getContext());
        if(externalStorageHandler.searchExternalStorage(GAME_SAVE)){
            saveFound = true;
        }

        // Create the push buttons
        play = new PushButton(w/2, h/2+h*BUTTON_Y_DIFFERENCE, w*PLAY_WIDTH, h*HEIGHT,
                PLAY_BUTTON_NAME[0], PLAY_BUTTON_NAME[1],this);

        if(saveFound){
            //enabled loadButton
            load = new PushButton(w/2, h/2-h*BUTTON_Y_DIFFERENCE, w*LOAD_WIDTH, h*HEIGHT,
                    LOAD_BUTTON_NAME[0], LOAD_BUTTON_NAME[1],this);
        } else {
            //disabled loadButton
            load = new PushButton(w/2, h/2-h*BUTTON_Y_DIFFERENCE, w*LOAD_WIDTH, h*HEIGHT, LOAD_BUTTON_DISABLED,this);
        }

        // Get background image
        background = mGame.getAssetManager().getBitmap(IMAGE_NAME);

        // Screen positioning conversion for image to fill screen
        int width = (int) (ViewportHelper.convertXDistanceFromLayerToScreen(w, mDefaultLayerViewport, mDefaultScreenViewport) * 1.5f);
        int height = (int) ViewportHelper.convertYDistanceFromLayerToScreen(h, mDefaultLayerViewport, mDefaultScreenViewport);
        dest = new Rect(0, 0, width, height);

        paint = new Paint();
        //Daniel Bell
        Music music = getGame().getAssetManager().getMusic(MUSIC_NAME);
        music.setLooping(true);
        game.getAudioManager().playMusic(music);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Updates the menu screen to check buttons to load game.
     * Also updates background image positioning.
     * @author Ben Andrew (Image Update, rest was found in original demo)
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        // Process any touch events occurring since the update
        Input input = mGame.getInput();

        //checks which way the image is moving
        //left changes source image x for animation
        if(directionRight){
            left++;
            //flips direction when at right edge
            if(left + ON_SCREEN_IMAGE_WIDTH >= IMAGE_WIDTH){
                directionRight = false;
            }
        } else {
            left--;
            //flips direction when at left edge
            if(left <= 0){
                directionRight = true;
            }
        }

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {
            //only allow button to be pushed if a save is found
            if(saveFound)
                load.update(elapsedTime);

            play.update(elapsedTime);

            if (play.isPushTriggered())
                //launch GameEnvironment on default new game
                mGame.getScreenManager().addScreen(new GameEnvironment(mGame, externalStorageHandler, false));
            else if(load.isPushTriggered())
                //launch GameEnvironment on loaded game
                mGame.getScreenManager().addScreen(new GameEnvironment(mGame, externalStorageHandler, true));
        }
    }

    /**
     * Draw the menu screen with background image and buttons
     * @author Ben Andrew
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.WHITE);
        //sets source image from left with ON_SCREEN_IMAGE_WIDTH width. left is updated to change source to animate image
        graphics2D.drawBitmap(background, new Rect(left, 0,left+ON_SCREEN_IMAGE_WIDTH,IMAGE_HEIGHT), dest, paint);
        play.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        load.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
