package uk.ac.qub.eeecs.game.menu;

import android.graphics.Typeface;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.ExternalStorageHandler;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.io.MenuButton;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;


/**
 * <h1>Screen manager</h1>
 * A class used for storing all the screens and buttons on main game screen for
 * handling which should be visible at any given moment
 *
 * @author  Kristina Geddis
 * @version 1.0
 */

public class ScreenManager {
    //Screens
    private InventoryScreen inventoryScreen;
    private PauseScreen pauseScreen;
    public ShopScreen shopScreen;
    public FightScreen fightScreen;
    private QuestScreen questScreen;
    public DeathScreen deathScreen;
    public WinScreen winScreen;
    public ConsumableScreen consumableScreen;
    //Buttons
    private ArrayList<MenuButton> menuButtons = new ArrayList<MenuButton>();

    //button sizing
    private final float BUTTON_STARTING_X = 0.83f;
    private final float BUTTON_SPACING = 0.104f;
    private final float BUTTON_Y_POSITION = 0.925f;
    private final float BUTTON_SIZE = 0.13f;

    /**
     * This constructor initialises ScreenManager with all its screens and buttons
     * @param mGame Game for current game
     * @param gameEnvironment GamEnvironment used for declaring pushbuttons and fightscreen
     * @param externalStorageHandler ExternalStorageHandler used for handling game saves in pauseScreen
     * @param assetManager AssetManager used for fetching images for the screen constructors
     * @param mPlayer Player used for fightScreen and winScreen
     * @author  Kristina Geddis
     */
    public ScreenManager(Game mGame, GameEnvironment gameEnvironment, ExternalStorageHandler externalStorageHandler, AssetManager assetManager, Player mPlayer,
                         Typeface font, int fontsize, int charactersPerLine, LayerViewport layerViewport){
        pauseScreen = new PauseScreen(mGame, externalStorageHandler);
        questScreen = new QuestScreen(mGame, font, fontsize, charactersPerLine);
        inventoryScreen = new InventoryScreen(mGame, assetManager.getBitmap("Inventory"), assetManager.getBitmap("InfoWindow"), font, fontsize);
        fightScreen = new FightScreen(mGame, mPlayer, font, fontsize, charactersPerLine, gameEnvironment);
        shopScreen = new ShopScreen(mGame, assetManager.getBitmap("NPCMarket"), font, fontsize, charactersPerLine);
        winScreen = new WinScreen(mGame, mPlayer,font, 45);
        deathScreen = new DeathScreen(mGame, font, fontsize);
        consumableScreen = new ConsumableScreen(mGame);

        float width = layerViewport.getWidth();
        float height = layerViewport.getHeight();
        float startingX = BUTTON_STARTING_X*width;
        float spacingX = BUTTON_SPACING*width;
        float y = BUTTON_Y_POSITION*height;
        float size = BUTTON_SIZE*height;
        menuButtons.add(new MenuButton(startingX,y,size,size,gameEnvironment, MenuButton.MenuButtonType.INVENTORY, "InventoryIcon"));
        menuButtons.add(new MenuButton(startingX+spacingX,y,size,size,gameEnvironment, MenuButton.MenuButtonType.PAUSE, "CogIcon"));
        menuButtons.add(new MenuButton(startingX-spacingX,y,size,size,gameEnvironment, MenuButton.MenuButtonType.QUEST, "QuestIcon"));
    }

    public void setFightScreen(String enemy){
        fightScreen.display(enemy);
    }

    /**
     * This method shows the shopScreen with its items when the player collides with it
     * @param items ArrayList<PreviewItem> used for setShopData method
     * @param inventory Inventory used for setShopData method
     * @author  Kristina Geddis
     */
    public void setShopScreen(ArrayList<PreviewItem> items, Inventory inventory){
        shopScreen.setShopData(items, inventory);
        shopScreen.setVisible(true);
    }

    /**
     * Sets the current screen to be the consumable screen
     * @param player Player that inventory will come from
     * @param gameEnvironment Game Environment to show consumable screen on
     * @authot Shannon Turley
     */
    public void setConsumableScreen(Player player, GameEnvironment gameEnvironment){
        consumableScreen.setConsumableData(player, gameEnvironment);
        consumableScreen.setVisible(true);
    }
    /**
     * This method updates screens and buttons
     * @param elapsedTime ElapsedTime used for update methods
     * @param mPlayer Player used for update methods
     * @param gameEnvironment GameEnvironment used for debug method
     * @param mDefaultLayerViewport LayerViewport used for button update methods
     * @param mDefaultScreenViewport ScreenViewport used for button update methods
     * @author  Kristina Geddis
     */
    public void update(ElapsedTime elapsedTime, Player mPlayer, GameEnvironment gameEnvironment, LayerViewport mDefaultLayerViewport, ScreenViewport mDefaultScreenViewport){
        if(pauseScreen.isVisible()){
            pauseScreen.update(elapsedTime, mPlayer, gameEnvironment);
        }else if(deathScreen.isVisible()){
            deathScreen.update(elapsedTime);
        }else if(winScreen.isVisible()){
            winScreen.update(elapsedTime);
        }else if(inventoryScreen.isVisible()){
            inventoryScreen.update(elapsedTime, mPlayer);
        }else if(consumableScreen.isVisible()){
            consumableScreen.update(elapsedTime);
        }else if(fightScreen.isVisible()){
            fightScreen.update(elapsedTime);
        }else if(questScreen.isVisible()){
            questScreen.update(elapsedTime, mPlayer.getInventory().getQuests());
        }else if(shopScreen.isVisible()){
            shopScreen.update(elapsedTime);
        }
        else {
            for(MenuButton menuButton : menuButtons){
                menuButton.update(elapsedTime,mDefaultLayerViewport,mDefaultScreenViewport,inventoryScreen,pauseScreen,questScreen);
            }
        }

    }

    /**
     * This method determines if any screen is visible
     * @return boolean to whether any screen is visible or not
     * @author Kristina Geddis
     */
    public boolean screenIsVisible(){
        return pauseScreen.isVisible() || inventoryScreen.isVisible() ||fightScreen.isVisible() || questScreen.isVisible() || shopScreen.isVisible() || deathScreen.isVisible() || winScreen.isVisible();
    }

    /**
     * This method draws;
     * -Screens
     * -Buttons
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @param mDefaultLayerViewport LayerViewport used in button draw methods
     * @param mDefaultScreenViewport ScreenViewport used in button draw methods
     * @author  Kristina Geddis
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport mDefaultLayerViewport, ScreenViewport mDefaultScreenViewport){
        if(pauseScreen.isVisible()){
            pauseScreen.draw(elapsedTime, graphics2D);
        }else if(deathScreen.isVisible()){
            deathScreen.draw(elapsedTime,graphics2D);
        }else if(winScreen.isVisible()){
            winScreen.draw(elapsedTime,graphics2D);
        }else if(consumableScreen.isVisible()){
            consumableScreen.draw(elapsedTime,graphics2D);
        }else if(fightScreen.isVisible()){
            fightScreen.draw(elapsedTime, graphics2D);
        }else if(inventoryScreen.isVisible()){
            inventoryScreen.draw(elapsedTime,graphics2D);
        }else if(questScreen.isVisible()){
            questScreen.draw(elapsedTime,graphics2D);
        } else if(shopScreen.isVisible()){
            shopScreen.draw(elapsedTime,graphics2D);
        }
        else{
            for(MenuButton menuButton : menuButtons){
                menuButton.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
            }
        }
    }
}
