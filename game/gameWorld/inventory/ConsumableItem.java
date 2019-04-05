package uk.ac.qub.eeecs.game.gameWorld.inventory;

import android.graphics.Bitmap;

/**
 * <h1>ConsumableItem</h1>
 * Class that contains information about preview items which can be consumed
 *
 * @author Shannon Turley
 * @version 1.0
 */
public class ConsumableItem extends PreviewItem {

    public enum Category{HEALTH}

    private Category category;
    private float amount;

    /**
     * Constructor for consumable item which doesn't have animation
     *
     * @param description String description of the item
     * @param price       int price of item in coins
     * @param image       Bitmap of item image to be displayed
     * @param imageName   String name of the image
     * @param name        String name of the item
     * @param category    Category item category
     * @param amount      float amount item will increase its stat by
     */
    public ConsumableItem(String description, int price, Bitmap image, String imageName,
                          String name, Category category, float amount) {
        super(description, price, image, imageName, name);
        this.category = category;
        this.amount = amount;
    }

    /**
     * Constructor for equippable item which has animation
     *
     * @param description   String description of the item
     * @param price         int price of item in coins
     * @param imageName     String name of the image
     * @param name          String name of the item
     * @param animationJson String name of animation JSON
     * @param animationName String name of animation to start on
     * @param category      Category item category
     * @param amount        float amount item will increase its stat by
     */
    public ConsumableItem(String description, int price, String imageName, String name,
                          String animationJson, String animationName, Category category, float amount) {
        super(description, price, imageName, name, animationJson, animationName);
        this.category = category;
        this.amount = amount;
    }

    /**
     * Returns item category
     * @return Category item category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns stat increase amount
     * @return float stat increase amount
     */
    public float getAmount() {
        return amount;
    }
}