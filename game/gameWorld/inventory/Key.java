package uk.ac.qub.eeecs.game.gameWorld.inventory;

import android.graphics.Bitmap;

/**
 * <h1>Key</h1>
 * Simple previewItem the holds an id to be verified
 * for quest completion and access to certain areas
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class Key extends PreviewItem {
    private int keyId;

    /**
     * This constructor initialises the super class
     * PreviewItem and assigns the local keyId
     * @author Ben Andrew
     * @param description String items description
     * @param price int item price (not used for keys)
     * @param image Bitmap for item
     * @param imageName String image name
     * @param name String item name
     * @param keyId int for key uniqueId
     */
    public Key(String description, int price, Bitmap image, String imageName, String name, int keyId) {
        super(description, price, image, imageName, name);
        this.keyId = keyId;
    }

    /**
     * Returns whether the passes value matches
     * this key's id
     * @author Ben Andrew
     * @param id int searched key
     * @return boolean whether id matches keyId
     */
    public boolean correctKey(int id){
        return keyId == id;
    }
}
