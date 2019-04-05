package uk.ac.qub.eeecs.game.gameWorld.inventory;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.game.fight.AttackMove;

/**
 * <h1>EquippableItem</h1>
 * Class that contains information about preview items which can be equipped by the player
 *
 * @author Shannon Turley
 * @version 1.0
 */
public class EquippableItem extends PreviewItem {

    public enum Category {ARMOUR, WEAPON}

    private Category category;
    private float armour;
    private AttackMove weaponMove;

    /**
     * Constructor for armour equippable item which doesn't have animation
     *
     * @param description String description of the item
     * @param price       int price of item in coins
     * @param image       Bitmap of item image to be displayed
     * @param imageName   String name of the image
     * @param name        String name of the item
     * @param armour      float amount item will increase armour by
     */
    public EquippableItem(String description, int price, Bitmap image, String imageName,
                          String name, float armour) {
        super(description, price, image, imageName, name);
        this.category = Category.ARMOUR;
        this.armour = armour;
    }

    /**
     * Constructor for armour equippable item which has animation
     *
     * @param description   String description of the item
     * @param price         int price of item in coins
     * @param imageName     String name of the image
     * @param name          String name of the item
     * @param animationJson String name of animation JSON
     * @param animationName String name of animation to start on
     * @param armour        float amount item will increase armour by
     */
    public EquippableItem(String description, int price, String imageName, String name,
                          String animationJson, String animationName, float armour) {
        super(description, price, imageName, name, animationJson, animationName);
        this.category = Category.ARMOUR;
        this.armour = armour;
    }

    /**
     * Constructor for weapon equippable item which doesn't have animation
     *
     * @param description String description of the item
     * @param price       int price of item in coins
     * @param image       Bitmap of item image to be displayed
     * @param imageName   String name of the image
     * @param name        String name of the item
     * @param move          AttackMove of the weapon
     */
    public EquippableItem(String description, int price, Bitmap image, String imageName,
                          String name, AttackMove move) {
        super(description, price, image, imageName, name);
        this.category = Category.WEAPON;
        this.weaponMove = move;
    }

    /**
     * Constructor for weapon equippable item which has animation
     *
     * @param description   String description of the item
     * @param price         int price of item in coins
     * @param imageName     String name of the image
     * @param name          String name of the item
     * @param animationJson String name of animation JSON
     * @param animationName String name of animation to start on
     * @param move          AttackMove of the weapon
     */
    public EquippableItem(String description, int price, String imageName, String name,
                          String animationJson, String animationName, AttackMove move) {
        super(description, price, imageName, name, animationJson, animationName);
        this.category = Category.ARMOUR;
        this.weaponMove = move;
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
    public float getArmour() {
        return armour;
    }

    public AttackMove getWeaponMove() {
        return weaponMove;
    }
}
