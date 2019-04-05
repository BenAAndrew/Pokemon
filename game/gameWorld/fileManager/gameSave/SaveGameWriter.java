package uk.ac.qub.eeecs.game.gameWorld.fileManager.gameSave;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import uk.ac.qub.eeecs.game.fight.AttackMove;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.ExternalStorageHandler;
import uk.ac.qub.eeecs.game.gameWorld.inventory.EquippableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>Save Game State</h1>
 * Class for converting the current game state into JSON formatting
 * to be written to a game save file in ExternalStorageHandler
 *
 * @author  Ben Andrew & Matthew Breen
 * @version 1.0
 */
public class SaveGameWriter {
    ExternalStorageHandler externalStorageHandler;

    /**
     * This constructor initialise the externalStorageHandler needed
     * to write the JSON to an external file
     * @param externalStorageHandler storage handler copied locally
     */
    public SaveGameWriter(ExternalStorageHandler externalStorageHandler){
        this.externalStorageHandler = externalStorageHandler;
    }

    /**
     * This method takes a player and extracts all key properties
     * into a JSONObject then passed into a byte stream sent to a gameSave file
     * @author Ben Andrew & Matthew Breen
     * @exception JSONException Handled exception results in process being aborted
     * @param player current gameWorld player
     */
    public void writeSavedGame(Player player){
        //Matthew Breen - resets players effects and evade, armour values so they are correct before being written to file
        player.resetEffects();
        try {
            //Ben Andrew
            JSONObject whole = new JSONObject();
            whole.put("x", player.position.x);
            whole.put("y", player.position.y);
            whole.put("worldKey", player.getGameWorld());
            whole.put("money", player.getInventory().getMoney());
            whole.put("items", saveInventory(player.getInventory()));
            whole.put("weaponEquipped", getEquippedItem(player.getInventory(),true));
            whole.put("armourEquipped", getEquippedItem(player.getInventory(),false));
            //Matthew Breen
            whole.put("health", player.getHealth());
            whole.put("evade", player.getEvade());
            whole.put("armour", player.getArmour());
            whole.put("moves",saveAttackMoves(player.getAttackMoves()));
            //Ben Andrew
            InputStream stream = new ByteArrayInputStream(whole.toString().getBytes(StandardCharsets.UTF_8));
            externalStorageHandler.writeFileToStorage(stream, "gameSave.JSON");
        } catch (JSONException e) {
            GameEnvironment.log.addLog("SaveGameWriter","Game save failure; player properties couldn't be retrieved");
        }
    }

    /**
     * This method takes an inventory and iterates through all items turning them into JSONObjects
     * and saving into a JSONArray which is returned
     * @author Ben Andrew
     * @exception JSONException Handled exception results in a partially filled JSONArray with all
     * objects up until point of exception
     * @param inventory current player inventory
     * @return inventoryArray JSONArray of inventory items
     */
    private JSONArray saveInventory(Inventory inventory) {
        JSONArray inventoryArray = new JSONArray();
        try {
            inventoryArray = new JSONArray();
            for (PreviewItem p : inventory.getItems()) {
                JSONObject inventoryItem = new JSONObject();
                inventoryItem.put("name", p.getName());
                inventoryArray.put(inventoryItem);
            }
        } catch (JSONException e) {
            GameEnvironment.log.addLog("SaveGameWriter","Inventory items couldn't be retrieved. Total items saved = "+inventoryArray.length());
        }
        return inventoryArray;
    }

    /**
     * This method takes an attack moves and iterates through all items turning them into JSONObjects
     * and saving into a JSONArray which is returned
     * @author Matthew Breen
     * @exception JSONException Handled exception results in a partially filled JSONArray with all
     * objects up until point of exception
     * @param attackMoves is current player attack moves
     * @return JSONArray of player attack moves
     */
    private JSONArray saveAttackMoves(ArrayList<AttackMove> attackMoves) {
        JSONArray moveArray = new JSONArray();
        try {
            for (AttackMove attackMove : attackMoves) {
                JSONObject move = new JSONObject();
                move.put("name",attackMove.getName());
                move.put("description",attackMove.getDescription());
                move.put("selfEffect",attackMove.isSelfEffect());

                JSONObject effect = new JSONObject();
                effect.put("healthChange",attackMove.getEffect().getHealthChange());
                if(attackMove.getEffect().getNumTurns() > 0){
                    effect.put("evadeChange",attackMove.getEffect().getEvadeChange());
                    effect.put("armourChange",attackMove.getEffect().getArmourChange());
                    effect.put("numTurns",attackMove.getEffect().getNumTurns());
                    effect.put("endMessage",attackMove.getEffect().getEndMessage());
                    effect.put("turnMessage",attackMove.getEffect().getTurnMessage());
                }
                move.put("effect",effect);
                moveArray.put(move);
            }
        } catch (JSONException e) {
            GameEnvironment.log.addLog("SaveGameWriter","Game save failure; player moves couldn't be retrieved");
        }
        return moveArray;
    }

    /**
     * This method takes an inventory and boolean to whether to process weapon or armour
     * and returns the relevant equipped items name to be saved
     * @author Ben Andrew
     * @param inventory current player inventory
     * @param weapon boolean of whether to return weapon or armour
     * @return String name of the item
     */
    private String getEquippedItem(Inventory inventory, boolean weapon){
        if(weapon){
            if(inventory.getEquipped(EquippableItem.Category.WEAPON) != null)
                return inventory.getEquipped(EquippableItem.Category.WEAPON).getName();
        } else {
            if(inventory.getEquipped(EquippableItem.Category.ARMOUR) != null)
                return inventory.getEquipped(EquippableItem.Category.ARMOUR).getName();
        }
        return null;
    }

}
