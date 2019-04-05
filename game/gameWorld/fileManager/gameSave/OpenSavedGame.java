package uk.ac.qub.eeecs.game.gameWorld.fileManager.gameSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.fight.AttackMove;
import uk.ac.qub.eeecs.game.fight.Effect;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.ExternalStorageHandler;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.JSONReader;
import uk.ac.qub.eeecs.game.gameWorld.inventory.EquippableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>Open Saved Game JSON's</h1>
 * Class for loading gameSave JSON's either from device storage (an existing save)
 * or default from assets (new game save). It extracts data from this JSON to get Player,
 * Inventory & current GameWorld data.
 *
 * @author  Ben Andrew & Matthew Breen
 * @version 1.0
 */
public class OpenSavedGame extends JSONReader {
    /**
     * This constructor declares the parent JSONReader with the default
     * gameSave.JSON found in assets (used for new game saves).
     * @author Ben Andrew
     * @param fileIO Used to give JSONReader the ability to read assets
     */
    public OpenSavedGame(FileIO fileIO) {
        super(fileIO, "txt/assets/gameSave.JSON");
    }

    /**
     * This constructor declares the parent JSONReader with a gameSave.JSON
     * found in the devices main storage directory (existing game save).
     * @author Ben Andrew
     * @param externalStorageHandler Used to give JSONReader the ability to read assets
     */
    public OpenSavedGame(ExternalStorageHandler externalStorageHandler) {
        super(externalStorageHandler, "gameSave.JSON");
    }

    /**
     * This method fetches all player properties from the JSON (except
     * gameScreen which is required to be passed to the method).
     * @author Ben Andrew
     * @exception JSONException Handled exception results in null being returned
     * @param gameScreen Used to construct player
     * @return Player returns player with all properties loaded from JSON
     */
    public Player getPlayer(GameScreen gameScreen) {
        Player player = null;
        try {
            int x = super.getJson().getInt("x");
            int y = super.getJson().getInt("y");
            float health = super.getJson().getInt("health");
            float evade = super.getJson().getInt("evade");
            float armour = super.getJson().getInt("armour");
            player = new Player(x, y, gameScreen, getWorld(),health,evade,armour);
        } catch (JSONException e) {
            GameEnvironment.log.addLog("OpenSavedGame","Player fetch failed");
        }
        return player;
    }

    /**
     * This method fetches the money value from the JSON
     * @author Ben Andrew
     * @exception JSONException Handled exception results in 0 being returned
     * @return int returns integer value of player money
     */
    public int getMoney() {
        int money = 0;
        try {
            money = super.getJson().getInt("money");
        } catch (JSONException e) {
            GameEnvironment.log.addLog("OpenSavedGame","Money fetch failed");
        }
        return money;
    }

    /**
     * This method fetches the player inventory by initialising inventory with money
     * and then iterates through items in JSON to add them to the inventory
     * @author Ben Andrew & Matthew Breen
     * @exception JSONException Handled exception results in an inventory with
     * however many items loaded correctly up until the error being returned
     * @param allItems Used to get items by matching string name to that
     *                 found in this arraylist
     * @return Inventory returns inventory with all contents loaded from JSON
     */
    public Inventory getInventory(HashMap<String, PreviewItem> allItems) {
        Inventory inventory = new Inventory(new ArrayList<PreviewItem>(), getMoney(), new ArrayList<Quest>());
        try {
            JSONArray items = getJson().getJSONArray("items");
            for(int i = 0; i < items.length(); i++){
                String item = items.getJSONObject(i).getString("name");
                inventory.addItem(allItems.get(item));
            }
            //Matthew Breen
            String weapon = getJson().getString("weaponEquipped");
            inventory.setEquipped((EquippableItem) allItems.get(weapon), EquippableItem.Category.WEAPON);
            String armour = getJson().getString("armourEquipped");
            inventory.setEquipped((EquippableItem) allItems.get(armour), EquippableItem.Category.ARMOUR);
        } catch (JSONException e) {
            GameEnvironment.log.addLog("OpenSavedGame","Inventory fetch failed");
        }
        return inventory;
    }

    /**
     * This method fetches the world number (not entire world) which
     * is later matched to the correct game world for the player to load into
     * @author Ben Andrew
     * @exception JSONException Handled exception results in o being returned
     * @return int returns integer value of worldKey
     */
    public String getWorld() {
        try {
            return super.getJson().getString("worldKey");
        } catch (JSONException e) {
            GameEnvironment.log.addLog("OpenSavedGame","Worldkey fetch failed");
        }
        return null;
    }

    /**
     * This method fetches the attack moves for the player from JSON
     * @author Matthew Breen
     * @exception JSONException Handled exception results in a empty list being returned
     * @return ArrayList<AttackMove> is the players attack moves
     */
    public ArrayList<AttackMove> getAttackMoves(){
        ArrayList<AttackMove> attackMoves = new ArrayList<>();
        try {
            JSONArray moves = getJson().getJSONArray("moves");
            for (int i = 0; i < moves.length(); i++) {
                JSONObject move = moves.getJSONObject(i);
                JSONObject effect = move.getJSONObject("effect");
                Effect moveEffect;
                if (effect.has("numTurns")) {
                    moveEffect = new Effect(effect.getInt("evadeChange"), effect.getInt("armourChange"), effect.getInt("healthChange"), effect.getInt("numTurns"), effect.getString("endMessage"), effect.getString("turnMessage"));
                } else {
                    moveEffect = new Effect(effect.getInt("healthChange"));
                }
                attackMoves.add(new AttackMove(move.getString("name"), move.getString("description"), moveEffect, move.getBoolean("selfEffect")));
            }
        }catch (JSONException e) {
            GameEnvironment.log.addLog("OpenSavedGame","Attack moves fetch failed");
        }
        return attackMoves;
    }
}
