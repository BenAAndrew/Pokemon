package uk.ac.qub.eeecs.game.gameWorld.fileManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;

/**
 * <h1>JSON Reader</h1>
 * Json reader class to handle and extract JSON data from files found in assets
 * and externally in device storage via ExternalStorageHandler
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class JSONReader {
    private boolean loaded;
    private JSONObject json;

    /**
     * This constructor initialises the json using
     * openJson and saves whether it was successful in loaded
     * @author  Ben Andrew
     * @param fileIO for accessing assets
     * @param fileName String filename
     */
    public JSONReader(FileIO fileIO, String fileName){
        loaded = openJSON(fileIO,fileName);
    }

    /**
     * This constructor initialises the json using
     * externalStorageHandler to access files in device storage
     * and saves whether it was successful in loaded
     * @author  Ben Andrew
     * @param externalStorageHandler for accessing device files
     * @param fileName String filename
     */
    public JSONReader(ExternalStorageHandler externalStorageHandler, String fileName){
        loaded = openJSON(externalStorageHandler,fileName);
    }

    /**
     * This method attempts to open the JSON file in assets and initialise
     * it as a JSONObject json. Returns true or false depending on whether it
     * was successful
     * @author  Ben Andrew
     * @exception IOException handled exception results in returning false
     * @exception JSONException handled exception results in returning false
     * @param fileIO for accessing asset's files
     * @param fileName String filename
     * @return boolean depending on whether it could be loaded
     */
    public boolean openJSON(FileIO fileIO, String fileName) {
        String file = "";
        try {
            file = fileIO.loadJSON(fileName);
        } catch (IOException e) {
            GameEnvironment.log.addLog("JSONReader","Couldn't read "+fileName+" from assets");
        }
        try {
            if(file.length() > 0)
                json = new JSONObject(file);
            return true;
        } catch (JSONException e) {
            GameEnvironment.log.addLog("JSONReader","Couldn't read "+fileName+" from assets");
        }
        return false;
    }

    /**
     * This method attempts to open the JSON file in storage and initialise
     * it as a JSONObject json. Returns true or false depending on whether it
     * was successful
     * @author  Ben Andrew
     * @exception FileNotFoundException handled exception results in returning false
     * @exception IOException handled exception results in returning false
     * @exception JSONException handled exception results in returning false
     * @param externalStorageHandler for accessing device files
     * @param fileName String filename
     * @return boolean depending on whether it could be loaded
     */
    public boolean openJSON(ExternalStorageHandler externalStorageHandler, String fileName){
        File file = externalStorageHandler.getFileFromExternalStorage(fileName);
        try {
            FileReader in = new FileReader(file);
            BufferedReader br = new BufferedReader(in);
            String all;
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                all = sb.toString();
            } finally {
                br.close();
            }
            json = new JSONObject(all);
            return true;
        } catch (FileNotFoundException e) {
            GameEnvironment.log.addLog("JSONReader","File "+fileName+" not found in device storage");
        } catch (IOException e){
            GameEnvironment.log.addLog("JSONReader","File "+fileName+" failed to read from storage");
        } catch (JSONException e){
            GameEnvironment.log.addLog("JSONReader","File "+fileName+" could not be converted to JSON");
        }
        return false;
    }

    /**
     * Getter for JSONObject
     * @author  Ben Andrew
     * @return JSONObject of loaded file
     */
    public JSONObject getJson(){
        return json;
    }

    /**
     * Getter for whether load was successful
     * @author  Ben Andrew
     * @return boolean of whether file was loaded successfully
     */
    public boolean isLoaded(){
        return loaded;
    }
}
