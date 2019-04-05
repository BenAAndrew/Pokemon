package uk.ac.qub.eeecs.game.gameWorld.fileManager;

import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameSave.OpenSavedGame;
import uk.ac.qub.eeecs.game.gameWorld.fileManager.gameWorld.GameWorld;

/**
 * <h1>World Manager</h1>
 * Class for initialising worlds and holding
 * them to be fetched by GameEnvironment.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class WorldManager {
    private final String[] worldJsons = {"GrassWorld", "PlayerHouse", "LavaWorld"};
    private HashMap<String, GameWorld> worlds = new HashMap<String, GameWorld>();

    /**
     * This constructor declares WorldManager with all parameters needed
     * to fetch and store all worlds.
     * @author Ben Andrew
     * @param fileIO FileIO needed for GameWorld construction
     * @param assetManager AssetManager needed for GameWorld construction
     * @param gameScreen GameScreen needed for GameWorld construction
     * @param fontSize int for font size in GameWorlds
     * @param charactersPerLine int for max characters per line in GameWorlds
     * @param font Typeface for font for GameWorlds
     */
    public WorldManager(FileIO fileIO, AssetManager assetManager, GameScreen gameScreen, int fontSize, int charactersPerLine, Typeface font){
        for(String world : worldJsons)
            worlds.put(world, new GameWorld(fileIO,"txt/assets/"+world+".JSON", assetManager, gameScreen, fontSize, charactersPerLine, font));
    }

    /**
     * Returns GameWorld of given name/key
     * @author Ben Andrew
     * @param gameWorld String for GameWorld name
     * @return GameWorld of given key
     */
    public GameWorld getWorld(String gameWorld) {
        return worlds.get(gameWorld);
    }

    //testing only
    public HashMap<String, GameWorld> getWorlds() {
        return worlds;
    }

    //testing only
    public String[] getWorldJsons() {
        return worldJsons;
    }
}
