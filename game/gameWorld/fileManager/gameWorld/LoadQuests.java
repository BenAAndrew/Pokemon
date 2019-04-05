package uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.JSONReader;
import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;

/**
 * <h1>Load Quests</h1>
 * Class for loading all quests and details from JSON
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class LoadQuests extends JSONReader {
    protected static final String QUESTS_JSON = "txt/assets/Quests.JSON";

    /**
     * This constructor declares the parent JSONReader with the Json
     * name held in QUESTS_JSON
     * @author Ben Andrew
     * @param fileIO Used to in parent JSONReader
     */
    public LoadQuests(FileIO fileIO){
        super(fileIO, QUESTS_JSON);
    }

    /**
     * Loads all quests properties from JSON and returns as Quest objects in an ArrayList
     * @author Ben Andrew
     * @exception JSONException Handled exception results in partial quests ArrayList being returned
     * @return ArrayList<Quest> all Quests
     */
    public ArrayList<Quest> getQuests(){
        ArrayList<Quest> quests = new ArrayList<Quest>();
        try {
            JSONArray itemJson = super.getJson().getJSONArray("items");
            for(int i = 0; i < itemJson.length(); i++) {
                JSONObject obj = itemJson.getJSONObject(i);
                String name = obj.getString("name");
                String description = obj.getString("description");
                quests.add(new Quest(name,description));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quests;
    }
}
