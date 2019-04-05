package uk.ac.qub.eeecs.game.gameWorld.inventory;

import java.util.ArrayList;

import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;
import uk.ac.qub.eeecs.game.gameWorld.inventory.EquippableItem.Category;

import static uk.ac.qub.eeecs.game.gameWorld.GameEnvironment.log;

/**
 * <h1>Inventory</h1>
 * Class for storing and handling players objects including
 * equippableItems and money.
 *
 * @author Shannon Turley & Ben Andrew
 * @version 1.0
 */
public class Inventory {
    private ArrayList<PreviewItem> items;
    private ArrayList<Quest> quests;
    private int money;

    private EquippableItem equippedArmour;
    private EquippableItem equippedWeapon;

    /**
     * This constructor initialises the items, money & quests
     * @author Ben Andrew
     * @param items  ArrayList of all aquired items
     * @param money  int value of current money
     * @param quests ArrayList of all assigned Quests
     */
    public Inventory(ArrayList<PreviewItem> items, int money, ArrayList<Quest> quests) {
        this.items = items;
        this.money = money;
        this.quests = quests;
    }

    public ArrayList<PreviewItem> getItems() {
        return items;
    }

    /**
     * Remove item at a specified index from the inventory list
     * @author Ben Andrew
     * @param index int of item at index to be removed
     */
    public void removeItem(int index) {
        items.remove(index);
    }

    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public void addQuest(Quest quest) {
        this.quests.add(quest);
    }

    /**
     * Removes quest of name matching given string.
     * @author Ben Andrew
     * @param quest String of the quest name to be removed
     */
    public void removeQuest(String quest) {
        for (int i = 0; i < quests.size(); i++) {
            if (quests.get(i).getName().equals(quest)) {
                quests.remove(i);
                break;
            }
        }
    }

    public void addItem(PreviewItem item) {
        items.add(item);
    }

    public int getMoney() {
        return money;
    }

    /**
     * Verifies if player has enough money for a transaction (if
     * taking money) and carries out the transaction. Returns
     * true or false depending on whether it was successful
     * @author Ben Andrew
     * @param amount int of money change (positive is adding money
     * negative is a withdrawal)
     * @return boolean to whether transaction is successful
     */
    public boolean transaction(int amount) {
        if (amount >= 0) {
            money += amount;
            return true;
        } else if (money + amount >= 0) {
            money += amount;
            return true;
        }
        return false;
    }

    /**
     * Equip an item - either armour or weapon.
     * If there was an item already equipped,
     * adds this item back into the normal item array list
     *
     * @author Shannon Turley
     * @param item Equippable Item to be equipped
     */
    public void equip(EquippableItem item) {
        switch (item.getCategory()) {
            case ARMOUR:
                if (equippedArmour != null) {
                    items.add(equippedArmour);
                }
                setEquipped(item, Category.ARMOUR);
                break;
            case WEAPON:
                if (equippedWeapon != null) {
                    items.add(equippedWeapon);
                }
                setEquipped(item, Category.WEAPON);
                break;
        }
    }

    /**
     * Unequip an item - either armour or weapon.
     * Adds this item back into the normal item list and sets nothing equipped for this
     * category of item
     * @author Shannon Turley
     * @param item equippable item to be unequipped
     */
    public void unequip(EquippableItem item) {
        switch (item.getCategory()) {
            case ARMOUR:
                items.add(item);
                setEquipped(null, Category.ARMOUR);
                break;
            case WEAPON:
                items.add(item);
                setEquipped(null, Category.WEAPON);
                break;
        }
    }

    /**
     * Consumes an item and gives its effect to the player,
     * then removes item from the inventory
     * @author Shannon Turley
     * @param item consumable item to be consumed
     * @param player player to consume this item
     */
    public void consume(ConsumableItem item, Player player) {
        switch (item.getCategory()) {
            case HEALTH:
                player.heal(item.getAmount());
                break;
        }
        items.remove(item);
    }

    /**
     * Set which item is equipped and its category
     * @author Shannon Turley
     * @param equippedItem equippable item to be set to equipped
     * @param category category of item
     */
    public void setEquipped(EquippableItem equippedItem, Category category) {
        switch (category) {
            case ARMOUR:
                items.remove(equippedItem);
                equippedArmour = equippedItem;
                break;
            case WEAPON:
                items.remove(equippedItem);
                equippedWeapon = equippedItem;
                break;
            default:
                log.addLog("INVENTORY ERROR",
                        "Tried to set equipped item of invalid type:" + category);
        }
    }

    /**
     * Get currently equipped item of a certain category
     * @author Shannon Turley
     * @param category category of item
     */
    public EquippableItem getEquipped(Category category) {
        switch (category) {
            case ARMOUR:
                return equippedArmour;
            case WEAPON:
                return equippedWeapon;
            default:
                log.addLog("INVENTORY ERROR",
                        "Tried to get equipped item of invalid type:" + category);
                return null;
        }
    }
}