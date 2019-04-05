package uk.ac.qub.eeecs.game.gameWorld.worldObjects;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
/**
 * <h1>Shop class</h1>
 * A class used for creating the world object that represents the shop. It is similar to other collidable objects, except it displays
 * the shop screen on collision
 *
 * @author  Kristina Geddis
 * @version 1.0
 */
public class Shop extends CollidableObject{
    //Private variables
    private String[] itemsList;
    protected ArrayList<PreviewItem> items = new ArrayList<PreviewItem>();

    /**
     * This constructor initialises Shop as a collidable object and with its item list
     * @param x float to set the x position of the shop
     * @param y float to set the y position of the shop
     * @param width float to set the width of the shop
     * @param height float to set the height of the shop
     * @param bitmap Bitmap to set the image for the shop
     * @param itemsList String[] to store the list of items the shop displays
     * @param gameScreen GameScreen for the collidable object
     * @author  Kristina Geddis
     */
    public Shop(float x, float y, float width, float height, Bitmap bitmap, String[] itemsList, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, gameScreen);
        this.itemsList = itemsList;
    }

    /**
     * This method takes the hashmap of all items and uses this to find this shops items.
     * It then saves these items to an arraylist.
     * @param allItems HashMap<String, PreviewItem> of all the items loaded into the game
     * @author Kristina Geddis
     */
    public void getShopItems (HashMap<String,PreviewItem> allItems){
        for(String shopItems : itemsList){
            items.add(allItems.get(shopItems));
        }
    }

    /**
     * This method sets the shop screen with items when the player collides
     * @param obj GameObject to be checked whether a player or not
     * @return boolean false (unused in this method)
     * @author Kristina Geddis
     */
    @Override
    public boolean onFirstCollision(GameObject obj) {
        if(obj instanceof Player){
                ((GameEnvironment) mGameScreen).screenManager.setShopScreen(items, ((Player) obj).getInventory());
        }
        return false;
    }
}
