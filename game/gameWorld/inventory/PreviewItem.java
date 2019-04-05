package uk.ac.qub.eeecs.game.gameWorld.inventory;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.world.GameObject;

/**
 * <h1>Preview Item</h1>
 * Class for storing Item information. Used for previewing items
 * such as in shops & inventory, and also passed to WorldItem
 * to store its properties inside any object the player may aquire.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class PreviewItem {
    private String name;
    private Bitmap image;
    private String imageName;
    private String description;
    private int price;

    //animated only
    private String animationJson;
    private String animationName;

    //item asset loading
    private static final String ITEM_ASSETS_FILE = "txt/assets/ItemImages.JSON";

    /**
     * This constructor initialises a simple PreviewItem
     * @author Ben Andrew
     * @param description String items description
     * @param price int item price
     * @param image Bitmap for item
     * @param imageName String image name
     * @param name String item name
     */
    public PreviewItem(String description, int price, Bitmap image, String imageName, String name){
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.imageName = imageName;
    }

    /**
     * This constructor initialises an animated PreviewItem
     * @author Ben Andrew
     * @param description String items description
     * @param price int item price
     * @param imageName String image name (required for inventory screen)
     * @param name String item name
     * @param animationJson String animation JSON name
     * @param animationName String starting animation name
     */
    public PreviewItem(String description, int price, String imageName, String name, String animationJson, String animationName){
        this.name = name;
        this.description = description;
        this.price = price;
        this.animationJson = animationJson;
        this.animationName = animationName;
        this.imageName = imageName;
    }

    /**
     * Loads ItemImages JSON file into AssetManager
     * @author Ben Andrew
     * @param assetManager Assetmanager to load item assets
     */
    public static void loadItemAssets(AssetManager assetManager) {
        assetManager.loadAssets(ITEM_ASSETS_FILE);
    }

    public Bitmap getImage(){
        return image;
    }

    public String getDescription(){
        return description;
    }

    public int getPrice(){ return price; }

    public String getName() { return name; }

    public String getAnimationJson() {
        return animationJson;
    }

    public String getAnimationName() {
        return animationName;
    }

    public String getImageName() {
        return imageName;
    }
}
