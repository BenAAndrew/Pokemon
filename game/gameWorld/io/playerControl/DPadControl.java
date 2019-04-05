package uk.ac.qub.eeecs.game.gameWorld.io.playerControl;

import java.util.HashMap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * <h1>DPadControl</h1>
 * Class for handling dpad interactions and buttons
 *
 * @author Shannon Turley
 * @version 1.0
 */
public class DPadControl {

    private HashMap<String, PushButton> controls;
    private float dPadCenterX, dPadCenterY, buttonWidth, buttonHeight;
    private final float SPACING_OFFSET = 36.0f;

    /**
     * Initialises a DPad object
     * @param dPadCenterX float x center of the dpad
     * @param dPadCenterY float y center of the dpad
     * @param buttonWidth float width of the directional buttons
     * @param buttonHeight float height of the directional buttons
     * @param gameScreen GameScreen the DPad will be rendered to
     * @param assetManager Asset manager for loading button images
     */
    public DPadControl(float dPadCenterX, float dPadCenterY, float buttonWidth,
                            float buttonHeight, GameScreen gameScreen, AssetManager assetManager){
        controls = new HashMap<>();
        assetManager.loadAssets("txt/assets/DPadAssets.JSON");
        this.dPadCenterX = dPadCenterX;
        this.dPadCenterY = dPadCenterY;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        createButtons(gameScreen);
    }

    protected DPadControl(){
        controls = new HashMap<>();
    }

    /**
     * Checks if moving left or right depending on button pressed
     * @return int x direction
     */
    public int getXDirection(){
        if(controls.get("Right").isPushed()) return 1;
        else if(controls.get("Left").isPushed()) return -1;
        else return 0;
    }

    /**
     * Checks if moving up or down depending on button pressed
     * @return int y direction
     */
    public int getYDirection(){
        if(controls.get("Up").isPushed()) return 1;
        else if(controls.get("Down").isPushed()) return -1;
        else return 0;
    }

    /**
     * Updates the dpad keys
     * @param elapsedTime time elapsed since last update
     * @param layerViewport layer viewport
     * @param screenViewport screen viewport
     */
    public void update(ElapsedTime elapsedTime, LayerViewport layerViewport, ScreenViewport screenViewport){
        for (String key : controls.keySet())
            controls.get(key).update(elapsedTime, layerViewport, screenViewport);
    }

    /**
     * Draws the dpad keys
     * @param elapsedTime time elapsed since last update
     * @param graphics2D graphics2D to draw to
     * @param layerViewport layer viewport
     * @param screenViewport screen viewport
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport){
        for (String key : controls.keySet())
            controls.get(key).draw(elapsedTime, graphics2D, layerViewport, screenViewport);
    }

    /**
     * Creates all buttons on the game screen when initialising for the first time
     * @param gameScreen GameScreen buttons will be on
     */
    private void createButtons(GameScreen gameScreen) {
        controls.put("Right", new PushButton(dPadCenterX + SPACING_OFFSET, dPadCenterY, buttonWidth, buttonHeight,
                "DPadRight", "DPadRightPressed", gameScreen));
        controls.put("Left",new PushButton(dPadCenterX - SPACING_OFFSET, dPadCenterY, buttonWidth, buttonHeight,
                "DPadLeft", "DPadLeftPressed", gameScreen));
        controls.put("Up",new PushButton(dPadCenterX, dPadCenterY + SPACING_OFFSET, buttonWidth, buttonHeight,
                "DPadUp", "DPadUpPressed", gameScreen));
        controls.put("Down",new PushButton(dPadCenterX, dPadCenterY - SPACING_OFFSET, buttonWidth, buttonHeight,
                "DPadDown", "DPadDownPressed", gameScreen));
    }
}
