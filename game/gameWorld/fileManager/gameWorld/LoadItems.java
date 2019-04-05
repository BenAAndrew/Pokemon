package uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.fight.AttackMove;
import uk.ac.qub.eeecs.game.fight.Effect;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.JSONReader;
import uk.ac.qub.eeecs.game.gameWorld.inventory.ConsumableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.EquippableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Key;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;

/**
 * <h1>Load Items</h1>
 * Class to load all items into the game from JSON.
 * This includes keys, equippable items, consumable items and animated items.
 *
 * @author Ben Andrew, Shannon Turley & Matthew Breen
 * @version 1.0
 */
public class LoadItems extends JSONReader {
    private AssetManager assetManager;

    /**
     * Constructor declares LoadItems with asset manager and fileIO for the super.
     *
     * @param fileIO       FileIO to read from
     * @param assetManager Asset Manager
     */
    public LoadItems(FileIO fileIO, AssetManager assetManager) {
        super(fileIO, "txt/assets/Items.JSON");
        this.assetManager = assetManager;
    }

    /**
     * Reads all items from the items.JSON file and parses them into PreviewItem objects,
     * then returns a Hashmap of PreviewItems and their names.
     * If there's an exception, adds an error message to the logs.
     *
     * @return HashMap<String   ,       PreviewItem> of items
     * @author Shannon Turley, Ben Andrew & Matthew Breen
     */
    public HashMap<String, PreviewItem> getItems() {
        //Ben Andrew
        HashMap<String, PreviewItem> items = new HashMap<String, PreviewItem>();
        try {
            JSONArray itemJson = super.getJson().getJSONArray("items");
            for (int i = 0; i < itemJson.length(); i++) {
                JSONObject obj = itemJson.getJSONObject(i);
                String name = obj.get("name").toString();
                String type = obj.get("type").toString();
                int price = obj.getInt("price");
                String description = obj.get("description").toString();
                String image = obj.get("image").toString();
                switch (type) {
                    case "key":
                        items.put(name, new Key(description, price, assetManager.getBitmap(image), image, name, obj.getInt("id")));
                        break;
                    // Shannon Turley
                    // Adds an equippable item to the item list, trying first animated and then not animated
                    case "equippable":
                        try {
                            if (obj.has("armour")) {
                                addAnimatedEquippableArmour(items, obj, name, price, description, image);
                            } else {
                                addAnimatedEquippableWeapon(items, obj, name, price, description, image);
                            }
                        } catch (Exception e) {
                            if (obj.has("armour")) {
                                addEquippableArmour(items, obj, name, price, description, image);
                            } else {
                                addEquippableWeapon(items, obj, name, price, description, image);
                            }
                        }
                        break;
                    // Adds a consumable item to the item list, trying first animated and then not animated
                    case "consumable":
                        ConsumableItem.Category consumeCategory = obj.getString("category").equals("health")
                                ? ConsumableItem.Category.HEALTH : null;
                        int consumeAmount = obj.getInt("amount");
                        try {
                            addAnimatedConsumable(items, obj, name, price, description, image, consumeCategory, consumeAmount);
                        } catch (Exception e) {
                            items.put(name, new ConsumableItem(description, price, assetManager.getBitmap(image),
                                    image, name, consumeCategory, consumeAmount));
                        }
                        break;
                        // Otherwise the item is just a normal preview item
                    default:
                        try {
                            addAnimatedPreviewItem(items, obj, name, price, description, image);
                        } catch (Exception e) {
                            items.put(name, new PreviewItem(description, price, assetManager.getBitmap(image), image, name));
                        } finally {
                            break;
                        }
                }
            }
        } catch (JSONException e) {
            GameEnvironment.log.addLog("LOADITEMS", "No items found to load");
        }
        return items;
    }

    /**
     * Adds an animated preview item to the item list
     * @param items HashMap item list
     * @param obj JSON object for parsing
     * @param name Name of the item
     * @param price Price of the item
     * @param description Description of the item
     * @param image Image name
     * @throws JSONException if JSON file could not be read
     */
    private void addAnimatedPreviewItem(HashMap<String, PreviewItem> items, JSONObject obj, String name, int price, String description, String image) throws JSONException {
        String animation = obj.getString("animation");
        String firstAnimation = obj.getString("firstAnimation");
        items.put(name, new PreviewItem(description, price, image, name, animation, firstAnimation));
    }

    /**
     * Adds an animated consumable item to the item list
     * @param items HashMap item list
     * @param obj JSON object for parsing
     * @param name Name of the item
     * @param price Price of the item
     * @param description Description of the item
     * @param image Image name
     * @param consumeCategory Category of consumable
     * @param consumeAmount Amount consumable increases a stat by
     * @throws JSONException if JSON file could not be read
     */
    private void addAnimatedConsumable(HashMap<String, PreviewItem> items, JSONObject obj, String name, int price, String description, String image, ConsumableItem.Category consumeCategory, int consumeAmount) throws JSONException {
        String animation = obj.getString("animation");
        String firstAnimation = obj.getString("firstAnimation");
        items.put(name, new ConsumableItem(description, price, image, name, animation,
                firstAnimation, consumeCategory, consumeAmount));
    }

    /**
     * Adds an equippable weapon to the item list
     * @Author Matthew Breen & Shannon Turley
     * @param items HashMap item list
     * @param obj JSON object for parsing
     * @param name Name of the item
     * @param price Price of the item
     * @param description Description of the item
     * @param image Image name
     * @throws JSONException if JSON file could not be read
     */
    private void addEquippableWeapon(HashMap<String, PreviewItem> items, JSONObject obj, String name, int price, String description, String image) throws JSONException {
        JSONObject move = obj.getJSONObject("weaponMove");
        JSONObject effect = move.getJSONObject("effect");
        Effect moveEffect;
        if (effect.has("numTurns")) {
            moveEffect = new Effect(effect.getInt("evadeChange"), effect.getInt("armourChange"), effect.getInt("healthChange"), effect.getInt("numTurns"), effect.getString("endMessage"), effect.getString("turnMessage"));
        } else {
            moveEffect = new Effect(effect.getInt("healthChange"));
        }
        AttackMove weaponMove = new AttackMove(move.getString("name"), move.getString("description"), moveEffect, move.getBoolean("selfEffect"));
        items.put(name, new EquippableItem(description, price, assetManager.getBitmap(image), image, name, weaponMove));
    }

    /**
     * Adds an equippable armour item to the item list
     * @param items HashMap item list
     * @param obj JSON object for parsing
     * @param name Name of the item
     * @param price Price of the item
     * @param description Description of the item
     * @param image Image name
     * @throws JSONException if JSON file could not be read
     */
    private void addEquippableArmour(HashMap<String, PreviewItem> items, JSONObject obj, String name, int price, String description, String image) throws JSONException {
        int amount = obj.getInt("armour");
        items.put(name, new EquippableItem(description, price, assetManager.getBitmap(image), image, name, amount));
    }

    /**
     * Adds an animated equippable weapon to the item list
     * @Author Matthew Breen & Shannon Turley
     * @param items HashMap item list
     * @param obj JSON object for parsing
     * @param name Name of the item
     * @param price Price of the item
     * @param description Description of the item
     * @param image Image name
     * @throws JSONException if JSON file could not be read
     */
    private void addAnimatedEquippableWeapon(HashMap<String, PreviewItem> items, JSONObject obj, String name, int price, String description, String image) throws JSONException {
        JSONObject move = obj.getJSONObject("weaponMove");
        JSONObject effect = move.getJSONObject("effect");
        Effect moveEffect;
        if (effect.has("numTurns")) {
            moveEffect = new Effect(effect.getInt("evadeChange"), effect.getInt("armourChange"), effect.getInt("healthChange"), effect.getInt("numTurns"), effect.getString("endMessage"), effect.getString("turnMessage"));
        } else {
            moveEffect = new Effect(effect.getInt("healthChange"));
        }
        AttackMove weaponMove = new AttackMove(move.getString("name"), move.getString("description"), moveEffect, move.getBoolean("selfEffect"));
        String animation = obj.getString("animation");
        String firstAnimation = obj.getString("firstAnimation");
        items.put(name, new EquippableItem(description, price, image, name, animation, firstAnimation, weaponMove));
    }

    /**
     * Adds an animated equippable armour item to the item list
     * @param items HashMap item list
     * @param obj JSON object for parsing
     * @param name Name of the item
     * @param price Price of the item
     * @param description Description of the item
     * @param image Image name
     * @throws JSONException if JSON file could not be read
     */
    private void addAnimatedEquippableArmour(HashMap<String, PreviewItem> items, JSONObject obj, String name, int price, String description, String image) throws JSONException {
        int amount = obj.getInt("armour");
        String animation = obj.getString("animation");
        String firstAnimation = obj.getString("firstAnimation");
        items.put(name, new EquippableItem(description, price, image, name, animation, firstAnimation, amount));
    }
}
