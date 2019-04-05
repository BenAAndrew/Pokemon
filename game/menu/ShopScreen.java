package uk.ac.qub.eeecs.game.menu;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.DialogHandler;
/**
 * <h1>Shop screen</h1>
 * A screen class used for user interactions, such as buying items for the player to equip/consume
 *
 * @author  Kristina Geddis, Ben Andrew
 * @version 1.0
 */

public class ShopScreen extends GameScreen{
    //Private variables
    protected PushButton exit;
    private int width, height;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 30;
    private GameObject background;

    protected DialogHandler dialogHandler;

    //Arraylist of item pushbuttons seen on screen
    protected ArrayList<PushButton> itemButtons = new ArrayList<PushButton>();
    //Arraylist of preview items corresponding to push buttons to get item information
    protected ArrayList<PreviewItem> items = new ArrayList<PreviewItem>();
    protected Inventory inventory;
    //Index of the currently selected item
    private int selectedItemIndex;

    //Constructor for testing purposes
    public ShopScreen(Game game){
        super("ShopScreen", game);
    }

    /**
     * This constructor initialises ShopScreen with a dialogHandler and buttons
     * @param game Game for current game
     * @param background Bitmap used for setting background image
     * @param font Typeface used for dialogHandler
     * @param fontSize used for setting the size of the font in dialogHandler
     * @param charactersPerLine used for dialogHandler
     * @author  Kristina Geddis
     */
    public ShopScreen(Game game, Bitmap background, Typeface font, int fontSize, int charactersPerLine) {
        super("ShopScreen", game);

        width = (int)mDefaultLayerViewport.getWidth();
        height = (int)mDefaultLayerViewport.getHeight();

        dialogHandler = new DialogHandler(fontSize, charactersPerLine, font);

        this.background = new GameObject(width/2,height/2,height*1.5f, height, background,this);

        exit = new PushButton(width/2,height*0.1f,BUTTON_WIDTH,BUTTON_HEIGHT,"resume",this);
        isVisible = false;
    }

    /**
     * This method takes the index of a previewItem and creates a dialog for selling that item
     * which is then shown. Also stores the item index in selectedItemIndex
     * @param index int index of previewItem in items to be shown
     * @author Ben Andrew
     */
    public void setItemDialog(int index){
        PreviewItem previewItem = items.get(index);
        dialogHandler.clear();
        dialogHandler.add(this, previewItem.getDescription(),true);
        dialogHandler.add(this, "Would you like to buy this for "+String.valueOf(previewItem.getPrice()), previewItem);
        dialogHandler.getCurrentDialog().setHidden(false);
        selectedItemIndex = index;
    }

    /**
     * This method goes through all preview items and adds a pushbutton corresponding to each preview item.
     * These have their positions calculated to be drawn on screen.
     * @author  Kristina Geddis
     */
    public void setIcons(){
        itemButtons = new ArrayList<PushButton>();
        float spaceIntervals = width*0.25f;
        for(int i = 0; i < items.size(); i++){
            float x = (spaceIntervals*(i+1));
            itemButtons.add(new PushButton(x, height*0.35f, 50, 50, items.get(i).getImageName(), this));
        }
    }

    /**
     * This method assigns the shops items and inventory. This is called when the player
     * collides with the shop to initialise the shop screen items
     * @param items ArrayList<PreviewItem> used for setting items
     * @param inventory Inventory used for setting inventory
     * @author  Kristina Geddis
     */
    public void setShopData(ArrayList<PreviewItem> items, Inventory inventory){
        this.items = items;
        this.inventory = inventory;
        setIcons();
    }

    /**
     * This method sets visible to the passed parameter
     * @param visible boolean to be set to visible
     * @author Kristina Geddis
     */
    public void setVisible(boolean visible){
        isVisible = visible;
    }

    /**
     * This method resumes to the main game screen when the resume button is pushed
     * @author  Kristina Geddis
     */
    public void resumeGame(){
        if(exit.isPushTriggered()){
            dialogHandler.clear();
            isVisible=false;
        }
    }

    /**
     * This method updates buttons, items and the dialogHandler
     * @param elapsedTime ElapsedTime used for update methods
     * @author  Kristina Geddis
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        if(isVisible){
            resumeGame();
            exit.update(elapsedTime,mDefaultLayerViewport,mDefaultScreenViewport);
            for(int i = 0; i < itemButtons.size(); i++){
                itemButtons.get(i).update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
                if(itemButtons.get(i).isPushTriggered()){
                    setItemDialog(i);
                }
            }
            dialogHandler.update(elapsedTime,inventory);
            if(dialogHandler.isInteractionSuccessful()){
                items.remove(selectedItemIndex);
                setIcons();
                dialogHandler.setInteractionSuccessful(false);
            }
        }
    }

    /**
     * This method draws;
     * -Buttons
     * -dialogHandler which outputs current message about item, i.e. item description dialog
     * -Background image
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @author  Kristina Geddis
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(255, 218, 86));
        background.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        for(GameObject items : itemButtons)
            items.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        if(dialogHandler.getCurrentDialog() != null){
            dialogHandler.getCurrentDialog().draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        }
        exit.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
    }
}