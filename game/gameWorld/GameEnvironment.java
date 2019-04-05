package uk.ac.qub.eeecs.game.gameWorld;

import android.graphics.Color;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.Bar;
import uk.ac.qub.eeecs.gage.util.CollisionDetector;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.ExternalStorageHandler;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.WorldManager;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameSave.OpenSavedGame;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld.GameWorld;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld.LoadItems;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld.LoadQuests;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.WorldItem;
import uk.ac.qub.eeecs.game.gameWorld.io.playerControl.DPadControl;
import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.AnimatedObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.CollidableObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.EnemyObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC.AStarNavigation;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC.NPC;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Shop;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.AnimatedEnemyObject;
import uk.ac.qub.eeecs.game.menu.ScreenManager;

/**
 * <h1>Game Environment</h1>
 * GameScreen of main game environment. Renders and updates player
 * and in world objects as well as using worldManager to change worlds.
 * Also decides when to show other screens and confines player and screen to
 * the world.
 *
 * @author  Ben Andrew, Matthew Breen, Shannon Turley, Kristina Geddis & Daniel Bell
 * @version 1.0
 */
public class GameEnvironment extends GameScreen {
    private LayerViewport mGameLayerViewport;
    private GameObject mGameBackground;
    protected Player mPlayer;

    //containers for all gameobjects, items & quests
    public ArrayList<GameObject> mObjects;
    public HashMap<String,PreviewItem> mItems;
    private ArrayList<Quest> quests;

    private DPadControl mMovementDPadControl;
    private OpenSavedGame openSavedGame;
    private ExternalStorageHandler externalStorageHandler;
    private WorldManager worldManager;
    protected GameWorld gameWorld;
    public ScreenManager screenManager;
    public Bar healthBar;
    private AStarNavigation aStarNavigation;

    //log and fps for debugging the game internally
    public static uk.ac.qub.eeecs.game.tools.Log log = new uk.ac.qub.eeecs.game.tools.Log();
    private int fps;

    //variables for text properties across the game
    private int fontSize;
    private Typeface font;
    private final int CHARACTERS_PER_LINE = 42;
    private final int TEXT_SCALE_VALUE = 60;
    private final String FONT_NAME = "8bit";

    /**
     * This constructor declares GameEnvironment with font details,
     * OpenSavedGame & WorldManger. Also calls setup methods. Used in testing.
     * @author Ben Andrew
     * @param game Game to declare parent GameScreen
     */
    public GameEnvironment(Game game){
        super("GameEnvironment", game);
        //scale text based on screen size
        fontSize = game.getScreenWidth() / TEXT_SCALE_VALUE;
        font = game.getAssetManager().getFont(FONT_NAME);
        openSavedGame = new OpenSavedGame(mGame.getFileIO());
        worldManager = new WorldManager(mGame.getFileIO(), mGame.getAssetManager(), this, fontSize, CHARACTERS_PER_LINE, font);
        setupViewports();
        setupGameObjects();
        setupControlHUD();
    }

    /**
     * This constructor declares GameEnvironment with font details, OpenSavedGame (which opens
     * assets or external gameSave depending on boolean) & WorldManger.
     * Also calls setup methods.
     * @author Ben Andrew
     * @param game Game to declare parent GameScreen
     * @param loadGame boolean of whether to load defualt save in assets
     * or external save
     */
    public GameEnvironment(Game game, ExternalStorageHandler externalStorageHandler, boolean loadGame) {
        super("GameEnvironment", game);
        //declare ExternalStorageHandler for interacting with files in device storage
        this.externalStorageHandler = externalStorageHandler;

        //scale text based on screen size
        fontSize = game.getScreenWidth() / TEXT_SCALE_VALUE;
        font = game.getAssetManager().getFont(FONT_NAME);

        //decides whether to use default or external save based on boolean
        if(!loadGame){
            openSavedGame = new OpenSavedGame(mGame.getFileIO());
        } else{
            openSavedGame = new OpenSavedGame(externalStorageHandler);
        }

        //initialises worldManager to load starting world
        worldManager = new WorldManager(mGame.getFileIO(), mGame.getAssetManager(), this, fontSize, CHARACTERS_PER_LINE, font);
        setupViewports();
        setupGameObjects();
        setupControlHUD();
    }

    /**
     * Inverts log state i.e. if visible hide, and if hidden show.
     * @author Ben Andrew
     */
    public void toggleDebug() {
        log.toggleVisible();
    }

    /**
     * Loads all world properties including background,
     * world objects, player, shopItems and aStarNaviation.
     * @author Ben Andrew
     * @param worldKey int of index of world to be loaded
     * @param playerX int of player X position in new world
     * @param playerY int of player Y position in new world
     */
    public void loadWorld(String worldKey, int playerX, int playerY){
        //get world at given index
        gameWorld = worldManager.getWorld(worldKey);
        //load objects for that world
        mObjects = gameWorld.getGameObjects(mItems,quests);
        mGameBackground = gameWorld.getMap();
        //set player properties
        mPlayer.setGameWorld(worldKey);
        mPlayer.position.x = playerX;
        mPlayer.position.y = playerY;
        //get shop items in new world
        for(GameObject g : mObjects){
            if(g instanceof Shop){
                ((Shop) g).getShopItems(mItems);
            }
        }
        //get world dimensions to recreate AStarNavigation with the new dimensions & objects
        int[] worldDimensions = gameWorld.getWorldDimensions();
        int[] worldBoundaries = gameWorld.getWorldBoundaries();
        aStarNavigation = new AStarNavigation(worldDimensions, worldBoundaries, mObjects);
    }

    /**
     * Declares viewports.
     * Taken from default gage project
     */
    private void setupViewports() {
        // Setup the screen viewport to use the full screen.
        mDefaultScreenViewport.set(0, 0, mGame.getScreenWidth(), mGame.getScreenHeight());

        // Calculate the layer height that will preserved the screen aspect ratio
        // given an assume 480 layer width.
        float layerHeight = mGame.getScreenHeight() * (480.0f / mGame.getScreenWidth());

        mDefaultLayerViewport.set(240.0f, layerHeight / 2.0f, 240.0f, layerHeight / 2.0f);
        mGameLayerViewport = new LayerViewport(240.0f, layerHeight / 2.0f, 240.0f, layerHeight / 2.0f);
    }

    /**
     * Gets all world items and quests to store in GameEnvironment for later use.
     * Also sets up player inventory and attack moves, and calls loadingWorld to create
     * starting world.
     * @author Ben Andrew, Daniel Bell :) & ... TODO: Add authors
     */
    private void setupGameObjects() {
        //Loads item assets and sets to mItems
        PreviewItem.loadItemAssets(getGame().getAssetManager());
        LoadItems loadItems = new LoadItems(mGame.getFileIO(), mGame.getAssetManager());
        mItems = loadItems.getItems();
        //Loads quest assets and sets to quests
        LoadQuests loadQuests = new LoadQuests(mGame.getFileIO());
        quests = loadQuests.getQuests();
        // Creates the Player
        mPlayer = openSavedGame.getPlayer(this);
        //Sets player inventory to one loaded from game save
        mPlayer.setInventory(openSavedGame.getInventory(mItems));
        //Sets player attack moves
        mPlayer.setAttackMoves(openSavedGame.getAttackMoves());
        //calls loadWorld to create the world the player should be loaded into
        loadWorld(mPlayer.getGameWorld(), (int) mPlayer.position.x, (int) mPlayer.position.y);
    }

    /**
     * Initialises HUD components such as the DPAD, Screen Manager and the Health Bar.
     * @author Kristina Geddis, Matthew Breen & Shannon Turley
     */
    private void setupControlHUD() {
        // Load in the assets used
        AssetManager assetManager = mGame.getAssetManager();

        // SETUP DPAD
        mMovementDPadControl = new DPadControl(70.0f, 70.0f, 35.0f,
                35.0f, this, assetManager);

        screenManager = new ScreenManager(mGame, this, externalStorageHandler, assetManager, mPlayer, font, fontSize, CHARACTERS_PER_LINE, mDefaultLayerViewport);
        healthBar = new Bar(100.0f,247.5f,160.0f,30.0f,assetManager.getBitmap("healthBarFill"),assetManager.getBitmap("healthBar"),new Vector2(2,3),Bar.Orientation.Horizontal,113,113,1000,this);
    }

    /**
     * Checks whether any NPCs in the gameWorld are currently talking.
     * @author Ben Andrew
     * @return boolean to whether an NPC is talking
     */
    public boolean NPCtalking() {
        for (GameObject g : mObjects) {
            if (g instanceof NPC) {
                if (((NPC) g).npcSpeechIsVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates gameEnvironment FPS, playerMovement
     * and gameObjects or Screen depending on which is shown.
     * @author Ben Andrew, Daniel Bell (fps) & Kristina Geddis (screenManager check)
     * @return boolean to whether an NPC is talking
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        // Daniel Bell :)
        // fps - Parses from data-types double to float to then int for output
        fps = (int) (1.0f / (float) elapsedTime.stepTime);

        //Ben Andrew
        //ensure Player is still if in another screen or talking to an NPC
        if(NPCtalking() || screenManager.screenIsVisible())
            mPlayer.setStopMovement(true);
        else
            mPlayer.setStopMovement(false);

        //Ben Andrew & Kristina Geddis
        if(!screenManager.screenIsVisible()){
            // Consider any user provided input
            processCollision();
            // Update the  game mObjects
            updateGameObjects(elapsedTime);
            // Update the GUI elements
            updateGUIGameObjects(elapsedTime);
            // Update NPCs movement
            if(!NPCtalking())
                aStarNavigation.update(elapsedTime,mObjects,mPlayer);
        }
        //update ScreenManager for active screen or overlay buttons in main game
        screenManager.update(elapsedTime, mPlayer, this, mDefaultLayerViewport, mDefaultScreenViewport);
    }

    /**
     * Processes collisions on CollidableObjects setting player lastCollidedWith
     * to the collided with object or null if no collisions
     * @author Matthew Breen & Ben Andrew
     */
    private void processCollision(){
        //collision flag
        boolean collision = false;
        //to check if it has collided with an enemy
        boolean enemyCollision = false;

        for (GameObject obj : mObjects) {
            if (obj instanceof CollidableObject)
                //check if collision
                if (CollisionDetector.isCollision(mPlayer.getBound(), obj.getBound())) {
                    //checks enemy collision as you only want it to collided with one enemy to avoid infinite opening of fight screen
                    if(obj != mPlayer.lastCollidedWith && !enemyCollision){
                        //execute objects first collision collision behaviour
                        ((CollidableObject) obj).onFirstCollision(mPlayer);
                        //sets players last collided with object
                        mPlayer.lastCollidedWith = obj;
                    }
                    if(obj instanceof EnemyObject)
                        enemyCollision = true;
                    //handles ongoing collision
                    ((CollidableObject) obj).handleCollision(mPlayer);
                    collision = true;
                }
        }
        //if no collisions occured sets lastCollidedWith to null
        if(!collision)
            mPlayer.lastCollidedWith = null;
    }

    /**
     * Updates GameObjects handles all variations of objects
     * and confines player & viewport to the world
     * @param elapsedTime Elapsed time information
     */
    private void updateGameObjects(ElapsedTime elapsedTime) {
        // updates player
        mPlayer.update(elapsedTime, mMovementDPadControl);

        //updates all gameObjects
        for(GameObject c : mObjects){
            //updates required for animated objects
            if(c instanceof AnimatedObject || c instanceof AnimatedEnemyObject){
                c.update(elapsedTime);
            }
            //WorldItems have custom update
            if(c instanceof WorldItem){
                ((WorldItem) c).update(elapsedTime, mPlayer.getInventory());
            }
            //NPCs also have custom update
            if(c instanceof NPC){
                ((NPC) c).update(elapsedTime, mPlayer, this);
            }
        }
        //confines player & viewport to world
        mPlayer.confinePlayerToWorld(gameWorld.getWorldBoundaries());
        confineViewportToWorld();
    }

    /**
     * Focuses the layer viewport on the player position and makes sure the viewport can't leave the world.
     * @author Shannon Turley
     */
    private void confineViewportToWorld(){
        // Focus the layer viewport on the Player
        mGameLayerViewport.x = mPlayer.position.x;
        mGameLayerViewport.y = mPlayer.position.y;

        // Ensure the viewport cannot leave the confines of the world
        if (mGameLayerViewport.getLeft() < 0)
            mGameLayerViewport.x -= mGameLayerViewport.getLeft();
        else if (mGameLayerViewport.getRight() > mGameBackground.getWidth())
            mGameLayerViewport.x -= (mGameLayerViewport.getRight() - mGameBackground.getWidth());

        if (mGameLayerViewport.getBottom() < 0)
            mGameLayerViewport.y -= mGameLayerViewport.getBottom();
        else if (mGameLayerViewport.getTop() > mGameBackground.getHeight())
            mGameLayerViewport.y -= (mGameLayerViewport.getTop() - mGameBackground.getHeight());
    }


    /**
     * Updates non button GUI elements (buttons are found in screenManager)
     * @author Matthew Breen & Shannon Turley
     * @param elapsedTime Elapsed time information
     */
    private void updateGUIGameObjects(ElapsedTime elapsedTime) {
        //Matthew Breen
        healthBar.forceValue((int)mPlayer.getHealth()+13);
        //Shannon Turley
        mMovementDPadControl.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
    }

    /**
     * Draws the gameWorld including background and objects, or current screen
     * if a screen is open
     * @author Ben Andrew
     * @param elapsedTime Elapsed time information
     * @param graphics2D Graphics2D for draw methods
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // Set the screen to black and define a clip based on the viewport
        graphics2D.clear(Color.BLACK);
        graphics2D.clipRect(mDefaultScreenViewport.toRect());

        if(!screenManager.screenIsVisible()){
            // Draw the background first of all
            mGameBackground.draw(elapsedTime, graphics2D, mGameLayerViewport, mDefaultScreenViewport);
            // Draw game objects
            drawGameObjects(elapsedTime,graphics2D);
            // Following should only be drawn if not in a dialog
            if(!NPCtalking()){
                log.draw(graphics2D, mDefaultLayerViewport, mDefaultScreenViewport, fps);
                mMovementDPadControl.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
                healthBar.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
            }
        }
        //draws the screen if in a screen, or just buttons
        screenManager.draw(elapsedTime,graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }

    /**
     * Draws the gameObjects in order of their vertical height to make objects appear
     * 3D i.e. the player walks in front of houses and also behind them. Also draws
     * NPC dialogs if any are active
     * @author Ben Andrew
     * @param elapsedTime Elapsed time information
     * @param graphics2D Graphics2D for draw methods
     */
    private void drawGameObjects(ElapsedTime elapsedTime, IGraphics2D graphics2D){
        // Draw game mObjects above the player
        for(GameObject obj : mObjects) {
            if(obj.position.y > mPlayer.position.y){
                drawObject(elapsedTime, graphics2D, obj);
            }
        }

        // Draw the Player
        mPlayer.draw(elapsedTime, graphics2D, mGameLayerViewport,mDefaultScreenViewport);

        // Draw game mObjects below or inline with the player
        for(GameObject obj : mObjects) {
            if (obj.position.y <= mPlayer.position.y) {
                drawObject(elapsedTime, graphics2D, obj);
            }
        }

        // Draw NPC dialogs if any
        for(GameObject obj : mObjects){
            if(obj instanceof NPC){
                ((NPC) obj).getCurrentDialog().draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
            }
        }
    }

    /**
     * Handles drawing gameObject as NPC has a different parameter list for its draw method
     * @author Ben Andrew
     * @param elapsedTime Elapsed time information
     * @param graphics2D Graphics2D for draw methods
     * @param object GameObject to be drawn
     */
    private void drawObject(ElapsedTime elapsedTime, IGraphics2D graphics2D, GameObject object){
        if(object instanceof NPC){
            ((NPC) object).draw(elapsedTime, graphics2D, mGameLayerViewport, mDefaultLayerViewport, mDefaultScreenViewport);
        } else {
            object.draw(elapsedTime, graphics2D, mGameLayerViewport, mDefaultScreenViewport);
        }
    }

    public GameWorld getGameWorld(){
        return gameWorld;
    }
}