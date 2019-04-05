package uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld;

import android.graphics.Typeface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.JSONReader;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.WorldItem;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.ChoiceDialog;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.DialogHandler;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.TextDialog;
import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.AnimatedEnemyObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.AnimatedObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.CollidableObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.EnemyObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Gateway;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC.MovingNPC;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC.NPC;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC.NPCInteractionHandler;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Shop;

/**
 * <h1>Game World Loader</h1>
 * Class for loading and extracting world properties from JSON
 * and identifying all world object types including NPCs &
 * items
 *
 * @author  Ben Andrew, Matthew Breen & Shannon Turley
 * @version 1.0
 */
public class GameWorld extends JSONReader {
    private AssetManager assetManager;
    private GameScreen gameScreen;
    private static final String[] OBJECT_TYPES = {"collidableObjects", "animatedObjects", "enemyObjects", "animatedEnemyObjects", "gateways", "npc", "shop"};
    private HashMap<String,PreviewItem> previewItems;
    private ArrayList<Quest> quests;
    private int[] worldBoundaries = new int[4];
    private int[] worldDimensions = new int[2];
    private int fontSize, charactersPerLine;
    private Typeface font;

    /**
     * This constructor declares the parent JSONReader with the saveFile
     * and copies assetmanager & gamescreen locally
     * @author Ben Andrew
     * @param fileIO Used to give JSONReader the ability to read assets
     * @param assetManager Used to allow class to access assets
     * @param gameScreen Used to initialise objects with gameScreen
     */
    public GameWorld(FileIO fileIO, String file, AssetManager assetManager, GameScreen gameScreen, int fontsize, int charactersPerLine, Typeface font) {
        super(fileIO, file);
        this.assetManager = assetManager;
        this.gameScreen = gameScreen;
        this.fontSize = fontsize;
        this.charactersPerLine = charactersPerLine;
        this.font = font;
    }

    /**
     * This method fetches the map gameObject with image,width & height as well
     * as saving world boundaries to a int array to be fetched by game later
     * @author Ben Andrew
     * @exception JSONException Handled exception results in null being returned
     * @return GameObject returns map object
     */
    public GameObject getMap() {
        GameObject map = null;
        String backgroundImage = "";
        try {
            backgroundImage = super.getJson().getString("background");
            worldDimensions[0] = super.getJson().getInt("width");
            worldDimensions[1] = super.getJson().getInt("height");
            worldBoundaries[0] = super.getJson().getInt("left");
            worldBoundaries[1] = super.getJson().getInt("right");
            worldBoundaries[2] = super.getJson().getInt("top");
            worldBoundaries[3] = super.getJson().getInt("bottom");
            map = new GameObject(worldDimensions[0] / 2.0f, worldDimensions[1] / 2.0f, worldDimensions[0], worldDimensions[1], assetManager.getBitmap(backgroundImage), gameScreen);
        } catch (JSONException e) {
            GameEnvironment.log.addLog("GameWorld","Map retrieval for "+backgroundImage+" failed");
        }
        return map;
    }

    /**
     * Getter for worldBoundaries array
     * @return int[] returns world boundaries
     */
    public int[] getWorldBoundaries() {
        return worldBoundaries;
    }

    /**
     * Getter for worldDimensions array
     * @return int[] returns world boundaries
     */
    public int[] getWorldDimensions() {
        return worldDimensions;
    }

    /**
     * Fetches quest of matching name (or null if not found)
     * @author Ben Andrew
     * @param name String matching a Quest's name
     * @return Quest returns quest of matching name
     */
    public Quest getQuest(String name){
        for (Quest q : quests) {
            if (q.getName().equalsIgnoreCase(name)) {
                return q;
            }
        }
        GameEnvironment.log.addLog("GameWorld","Quest of name "+name+" not found");
        return null;
    }

    /**
     * This method takes a JSONObject and extracts all dialog data from it including
     * message, buttons, interactions & success/failure where applicable. Handles returning both
     * TextDialog and ChoiceDialog (child class)
     * @author Ben Andrew
     * @exception JSONException Handled exception results in null being returned
     * @param dialog JSONObject containing all dialog information
     * @return TextDialog returns complete textdialog
     */
    public TextDialog getDialog(JSONObject dialog){
        String message = "";
        try {
            message = dialog.getString("message");
            if(dialog.has("interaction")){
                String[] btn = dialog.getString("buttons").split(",");
                ArrayList<String> buttons = new ArrayList<String>();
                for (String k : btn)
                    buttons.add(k);
                String interaction = dialog.getString("interaction");
                NPCInteractionHandler npcInteractionHandler = null;
                if(dialog.has("keyId")){
                    npcInteractionHandler = new NPCInteractionHandler(dialog.getInt("keyId"), previewItems.get(dialog.getString("reward")), interaction.equalsIgnoreCase("take"), dialog.getString("quest"));
                } else {
                    npcInteractionHandler = new NPCInteractionHandler(getQuest(dialog.getString("quest")), previewItems.get(dialog.getString("reward")));
                }
                String successMessage = dialog.getString("success");
                String failureMessage = dialog.getString("failure");
                boolean deletePreviousMessageSequence = false;
                if(dialog.has("deleteSequence")){
                    deletePreviousMessageSequence = dialog.getBoolean("deleteSequence");
                }
                return new ChoiceDialog(gameScreen, message, font, fontSize, charactersPerLine, buttons, npcInteractionHandler, successMessage, failureMessage, deletePreviousMessageSequence);
            } else {
                return new TextDialog(gameScreen, message, font, fontSize, charactersPerLine);
            }
        } catch (JSONException e) {
            GameEnvironment.log.addLog("GameWorld","Dialog retrieval for '"+message+"' failed");
        }
        return null;
    }

    /**
     * This method takes a JSONObject of NPC and extracts the dialogs array from it.
     * It then uses getDialog to create a dialog for each JSONObject and adds these to
     * dialoghandler
     * @author Ben Andrew
     * @exception JSONException Handled exception results in dialoghandler in the
     * current state being returned
     * @param dialogHandler DialogHandler to add TextDialogs to
     * @param obj JSONObject to extract dialog details from
     * @return DialogHandler returns complete dialoghandler containing all dialogs
     */
    public DialogHandler getNPCDialogs(DialogHandler dialogHandler, JSONObject obj) {
        try {
            JSONArray dialogs = obj.getJSONArray("dialogs");
            for (int j = 0; j < dialogs.length(); j++) {
                TextDialog dialog = getDialog(dialogs.getJSONObject(j));
                if(dialog instanceof ChoiceDialog)
                    dialogHandler.add((ChoiceDialog) dialog);
                else
                    dialogHandler.add(dialog);
            }
        } catch (JSONException e) {
            GameEnvironment.log.addLog("GameWorld","NPC dialogs retrieval failed");
        }
        return dialogHandler;
    }

    /**
     * This is the main method of GameWorld. Used to fetch all world objects from JSON.
     * Handles all World Object types (except player).
     * @author Ben Andrew, Matthew Breen & Kristina Geddis
     * @exception JSONException Handled exception results in current object type retrieval
     * being halted and incrementing to next object type
     * @param allItems Hashmap of all PreviewItems with names (fetched previously)
     * @param quests ArrayList of all quest objects
     * @return ArrayList\<GameObject> returns ArrayList containing all world objects except player
     */
    public ArrayList<GameObject> getGameObjects(HashMap<String,PreviewItem> allItems, ArrayList<Quest> quests) {
        //Ben Andrew
        this.previewItems = allItems;
        this.quests = quests;
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        for (String elementType : OBJECT_TYPES) {
            try {
                JSONArray objectJsons = super.getJson().getJSONArray(elementType);
                for (int i = 0; i < objectJsons.length(); i++) {
                    JSONObject obj = objectJsons.getJSONObject(i);
                    float x = Float.valueOf(obj.getString("x"));
                    float y = Float.valueOf(obj.getString("y"));
                    float width = Float.valueOf(obj.getString("width"));
                    float height = Float.valueOf(obj.getString("height"));
                    switch (elementType) {
                        case "animatedObjects":
                            objects.add(new AnimatedObject(x, y, width, height, gameScreen, obj.getString("animation"), obj.getString("animationName")));
                            break;
                        case "collidableObjects":
                            try {
                                float collisionHeightModifier = Float.valueOf(obj.getString("collisionHeightModifier"));
                                objects.add(new CollidableObject(x, y, width, height, collisionHeightModifier, assetManager.getBitmap(obj.get("bitmap").toString()), gameScreen));
                            } catch (Exception e) {
                                objects.add(new CollidableObject(x, y, width, height, assetManager.getBitmap(obj.get("bitmap").toString()), gameScreen));
                            }
                            break;
                        case "npc":
                            NPC npc = null;
                            if(obj.has("worldkey")){
                                npc = new NPC(x, y, width, height, assetManager.getBitmap(obj.get("bitmap").toString()), gameScreen, font, fontSize, charactersPerLine, obj.getString("worldkey"), obj.getInt("playerX"), obj.getInt("playerY"));
                            } else if(obj.has("animation")) {
                                if(obj.has("dialogs")){
                                    npc = new MovingNPC(x, y, width, height, gameScreen, font, fontSize, charactersPerLine, obj.getString("animation"), obj.getBoolean("seekPlayer"), obj.getInt("movementSpeed"));
                                } else {
                                    npc = new MovingNPC(x, y, width, height, gameScreen, obj.getString("animation"), obj.getBoolean("seekPlayer"), obj.getInt("movementSpeed"));
                                }
                            } else{
                                npc = new NPC(x, y, width, height, assetManager.getBitmap(obj.get("bitmap").toString()), gameScreen, font, fontSize, charactersPerLine);
                            }
                            npc.dialogHandler = getNPCDialogs(npc.dialogHandler, obj);
                            objects.add(npc);
                            break;
                        //Matthew Breen
                        case "enemyObjects":
                            ArrayList<String> list = new ArrayList<>();
                            JSONArray jsonArray = obj.getJSONArray("enemies");
                            if (jsonArray != null) {
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    list.add(jsonArray.get(j).toString());
                                }
                            }
                            objects.add(new EnemyObject(x, y, width, height, assetManager.getBitmap(obj.getString("bitmap")), gameScreen, Integer.valueOf(obj.get("chance").toString()), list, obj.getBoolean("solid")));
                            break;
                        case "animatedEnemyObjects":
                            list = new ArrayList<>();
                            jsonArray = obj.getJSONArray("enemies");
                            if (jsonArray != null) {
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    list.add(jsonArray.get(j).toString());
                                }
                            }
                            objects.add(new AnimatedEnemyObject(x, y, width, height, gameScreen, Integer.valueOf(obj.get("chance").toString()), list, obj.getString("animation"), obj.getString("animationName"),obj.getBoolean("solid")));
                            break;
                        //Ben Andrew
                        case "gateways":
                            objects.add(new Gateway(x, y, width, height, assetManager.getBitmap(obj.getString("bitmap")), gameScreen, obj.getString("worldkey"), obj.getInt("playerX"), obj.getInt("playerY")));
                            break;
                        //Kristina Geddis
                        case "shop":
                            JSONArray itemsList = obj.getJSONArray("items");
                            String[] items = new String[itemsList.length()];
                            for (int l = 0; l < itemsList.length(); l++) {
                                items[l] = itemsList.getString(l);
                            }
                            objects.add(new Shop(x, y, width, height, assetManager.getBitmap(obj.get("bitmap").toString()), items, gameScreen));
                            break;
                    }
                }
            } catch (JSONException e) {
                System.out.println("No " + elementType + " found");
            }
        }
        objects.addAll(getWorldItems());
        return objects;
    }

    /**
     * Fetches all world items from JSON and matches to PreviewItems.
     * @author Ben Andrew
     * @exception JSONException Handled exception results in current
     * fetching being halted and current ArrayList being returned
     * @return ArrayList\<WorldItem> returns ArrayList containing all WorldItems
     */
    public ArrayList<WorldItem> getWorldItems() {
        ArrayList<WorldItem> worldItems = new ArrayList<WorldItem>();
        try {
            JSONArray objectJsons = super.getJson().getJSONArray("items");
            for (int i = 0; i < objectJsons.length(); i++) {
                JSONObject obj = objectJsons.getJSONObject(i);
                float x = Float.valueOf(obj.getString("x"));
                float y = Float.valueOf(obj.getString("y"));
                float width = Float.valueOf(obj.getString("width"));
                float height = Float.valueOf(obj.getString("height"));
                String name = obj.getString("name");
                worldItems.add(new WorldItem(x,y,width,height,gameScreen,previewItems.get(name)));
            }
        } catch (JSONException e) {
            GameEnvironment.log.addLog("GameWorld","Item fetching failed. Total items = "+worldItems.size());
        }
        return worldItems;
    }
}
