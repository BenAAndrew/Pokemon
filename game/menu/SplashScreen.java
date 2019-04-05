package uk.ac.qub.eeecs.game.menu;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.tools.Progress;
import uk.ac.qub.eeecs.game.tools.ProgressBar;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.engine.AssetManager;

import android.util.Log;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;

/*
 * <h1>SplashScreen</h1>
 * Displaying an instance of ProgressBar; visualising the task on-screen to the User
 * Future Expansion:- Commences loading assests from AssetManager
 *
 * @author Daniel Bell :)
 * @version 1.0
 */
public class SplashScreen extends GameScreen {
    /**
     * Properties
     */
    // width & height - used in Constructing PushButton navigate
    private int splashWidth, splashHeight;
    // touched - Has the screen been touched? Will skip Splash Screen; Flag
    // Otherwise, will continue on with loading the whole SplashScreen
    private boolean touched = false;

    // ********************************************************************************************
    ///////////////////////
    //  Future Expansion
    ///////////////////////
    // fileIO - enables assets to load in
    private FileIO fileIO;
    // assetManager - Asynchronous task of which handles the loading of assets
    private AssetManager assetManager;
    // currentProgress - How much AssetManager has loaded in, in live time; / 100
    private float currentProgress = 0;
    // List of information about assets to be loaded, including file path and file type.
    private ArrayList<AssetManager> itemList;
    // progressBar - instantiation
    private ProgressBar splashProgressBar;
    // progress - Utilised to Draw the current progress to screen
    private Progress progress;
    // ********************************************************************************************

    // visible - Flag;
    private boolean visible = false;

    // Associated in-line functions (Accessor(s) & Mutator(s))
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible){ this.visible = visible; }

    // MAXIMUM_TIME - used to compare value of timeSinceStart to in below conditioning; cannot be modified
    final float MAXIMUM_TIME = (float) 2.0;
    // hasStarted - Flag - in relation to counting time
    boolean hasStarted = false;
    // timeSinceStart - records time passed since first loop of update()
    double timeSinceStart = -1;

    // ********************************************************************************************

    // Default Constructor - for SplashScreenTest
    public SplashScreen(Game game){
        super("SplashScreen", game);
    }

    /**
     * Constructor - Creates a GameScreen
     *
     * Parameters to be passed through Constructor, in order to establish an instance of SplashScreen :-
     * @param name      identifies GameScreen
     * @param game      instance of Game used by SplashScreen
     * @author          Daniel Bell :)
     */
    public SplashScreen(String name, Game game) {
        super("SplashScreen", game);

        // loads the bitmaps needed for the progress bar.
        mGame.getAssetManager().loadAndAddBitmap("ProgressBarRed", "img/ProgressBarRed.png");
        mGame.getAssetManager().loadAndAddBitmap("ProgressBarGreen", "img/ProgressBarGreen.png");

        // ****************************************************************************************
        ///////////////////////
        //  Future Expansion
        ///////////////////////
        // Loads-in game contexts to fileIO
        fileIO = new FileIO(mGame.getActivity().getApplicationContext());
        // itemList.size to be used for maxProgress in ProgressBar Constructor
        itemList = new ArrayList<>();
        // Loads-in required Bitmaps for placing onto SplashScreen
        AssetManager assetManager = mGame.getAssetManager();
        mGame.getAssetManager().loadAssets("txt/assets/Assets.JSON");
        // ProgressBar Constructor (maxProgress) = itemList.size();
        // ****************************************************************************************

        // Values utilised in below ProgressBar Constructor, for establishing an instance for on-screen use
        // Getters called upon originate from abstract class Game
        splashWidth = mGame.getScreenWidth();
        splashHeight = mGame.getScreenHeight();
        int progressBarWidth = ((splashWidth / 3) * 2); // 2/3rds the width of Splash Screen
        int progressBarHeight = 100;

        // Constructs the instance splashProgressBar of user-defined type ProgressBar for Draw()
        // (splashWidth / 2) - can also be used as definition of Mid-Point co-ordinate
        splashProgressBar = new ProgressBar((splashWidth / 2), splashHeight, progressBarWidth, progressBarHeight, "ProgressBarRed", "ProgressBarGreen", MAXIMUM_TIME, this);
    }

    // ********************************************************************************************

    /**
     * Abstract Override Methods - from GameScreen
     */

    /**
     * Updates Time passing and Progress on top of ProgressBar
     * Future Expansion:- Pulls down Assets, only allowing User to skip screen once all assets have been loaded in
     * @param elapsedTime       ElapsedTime used for update methods
     * @author                  Daniel Bell :)
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        // splashProgressBar.updateProgress(assetManager.getCurrentProgress());
        splashProgressBar.update(elapsedTime);

        // elapsedTime - Call by Value, passed in through parameter list
        elapsedTime.stepTime = 0;

        // When Touch is detected, this will skip past our Splash Screen and go to the Main Menu
        Input input = mGame.getInput();
        List<TouchEvent> touchEvents = input.getTouchEvents();

        // currentProgress - Has live updates assigned here, from assetManager's background task
        currentProgress = (int)timeSinceStart;

        // splashProgressBar.updateProgress(currentProgress);
        // progress.setWidth((float)timeSinceStart);
        // progress.setMinimumX((float)timeSinceStart);
        // progress.calculateWidth(TRIGGER_TIME, currentProgress);

        // ****************************************************************************************
        ///////////////////////
        //  Future Expansion
        ///////////////////////
        // Only enable User to skip SplashScreen once all Assets have been loaded in
        if (currentProgress >= (int)timeSinceStart) {
            // When User touches screen, boolean flag "touched" = true
            // Transitions to Main Menu
            if (touchEvents.size() > 0) { // Touch on-screen is considered in size of the contact press
                // When size of touchEvent on-screen is over 0; then SplashScreen considers event as a "full touch"
                touched = true;
                MenuScreen menuScreen = new MenuScreen(mGame);
                mGame.getScreenManager().addScreen(menuScreen);
            }
        }
        // ****************************************************************************************

        // GameEnvironment.log.addLog("elapsedTime.totalTime", " " + elapsedTime.totalTime);

        if (!hasStarted) {
            timeSinceStart = elapsedTime.totalTime; // initialised
            hasStarted = true;
        } else {
            timeSinceStart += elapsedTime.stepTime;
            if ((elapsedTime.totalTime - timeSinceStart) >= MAXIMUM_TIME || touched == true) { // Equiv. splashProgressBar.isDone(); as currentProgress = (int)timeSinceStart
                // Change Screen
                MenuScreen menuScreen = new MenuScreen(mGame);
                mGame.getScreenManager().addScreen(menuScreen);
            }
            // when removed/ commented out; ProgressBar is fully Green

            // GameEnvironment.log.addLog("timeSinceStart", " " + timeSinceStart);
            // GameEnvironment.log.addLog("(float)timeSinceStart", " " + (float)timeSinceStart);
            // splashProgressBar.
            // updateProgress((float)timeSinceStart, MAXIMUM_TIME);
        }

        /*
        if(splashProgressBar.isDone() || touched == true) {
            MenuScreen menuScreen = new MenuScreen(mGame);
            mGame.getScreenManager().addScreen(menuScreen);
        }
        */
    }

    /**
     * Draws background, RGB of GrassMap and ProgressBar on-screen
     * @param elapsedTime       ElapsedTime required for draw methods
     * @param graphics2D        IGraphics2D required for draw methods
     * @author                  Daniel Bell :)
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Entire screen will be this RGB colour
        graphics2D.clear(Color.rgb(101, 194, 150)); // Resembles Green Grass in-game
        splashProgressBar.draw(elapsedTime, graphics2D);
    }
}
