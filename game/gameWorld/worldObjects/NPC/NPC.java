package uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC;

import android.graphics.Bitmap;
import android.graphics.Typeface;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.DialogHandler;
import uk.ac.qub.eeecs.game.gameWorld.npcInteractions.TextDialog;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.CollidableObject;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>NPC</h1>
 * Class for creating NPC's that the player can collide with and
 * create dialog chains using dialogHandler. May also be used to
 * enable entry to other worlds (guards)
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class NPC extends CollidableObject {
    public DialogHandler dialogHandler;
    protected String newWorld;
    protected int newWorldXPosition, newWorldYPosition;


    /**
     * This constructor declares NPC with a dialogHandler and
     * world entry id and coordinates
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for width
     * @param height float for height
     * @param bitmap Bitmap for NPC
     * @param gameScreen GameScreen to draw
     * @param font TypeFace for dialog text font
     * @param fontSize int for dialog font size
     * @param charactersPerLine int for max characters
     * per line in dialogs
     * @param newWorld String name of new world to be loaded
     * @param worldX int of new world x position
     * @param worldY int of new world y position
     */
    public NPC(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, Typeface font, int fontSize, int charactersPerLine, String newWorld, int worldX, int worldY) {
        super(x, y, width, height, bitmap, gameScreen);
        this.dialogHandler = new DialogHandler(fontSize,charactersPerLine,font);
        this.newWorld = newWorld;
        this.newWorldXPosition = worldX;
        this.newWorldYPosition = worldY;
    }

    /**
     * This constructor declares NPC with a dialogHandler
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for width
     * @param height float for height
     * @param bitmap Bitmap for NPC
     * @param gameScreen GameScreen to draw
     * @param font TypeFace for dialog text font
     * @param fontSize int for dialog font size
     * @param charactersPerLine int for max characters
     * per line in dialogs
     */
    public NPC(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, Typeface font, int fontSize, int charactersPerLine) {
        super(x, y, width, height, bitmap, gameScreen);
        this.dialogHandler = new DialogHandler(fontSize,charactersPerLine,font);
    }

    /**
     * Handles collisions with objects. If colliding with
     * a player, shows current dialog.
     * @param obj GameObject used to check if collision
     * is with a player
     * @return boolean returns true (automatic and unused in
     * this state)
     */
    @Override
    public boolean onFirstCollision(GameObject obj) {
        if(obj instanceof Player)
            getCurrentDialog().setHidden(false);
        return true;
    }

    /**
     * Updates NPC and dialog handler. If world is successful
     * and world coordinates are stored it will reload the
     * world to these values
     * @param elapsedTime ElapsedTime used to update NPC & dialogHandler
     * @param player Player used to get inventory to update dialogHandler
     * @param gameEnvironment GameEnvironment used to call reloadWorld
     *  if interaction is successful & coordinates are set.
     */
    public void update(ElapsedTime elapsedTime, Player player, GameEnvironment gameEnvironment) {
        super.update(elapsedTime);
        if(dialogHandler != null)
            dialogHandler.update(elapsedTime, player.getInventory());
        if(dialogHandler.isInteractionSuccessful() && newWorld != null){
            gameEnvironment.loadWorld(newWorld, newWorldXPosition, newWorldYPosition);
        }
    }

    /**
     * Draws NPC & current Dialog
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @param layerViewport LayerViewport used in draw methods
     * @param screenViewport ScreenViewport used in draw methods
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, LayerViewport overlayViewport, ScreenViewport screenViewport){
        super.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
        getCurrentDialog().draw(elapsedTime,graphics2D,overlayViewport,screenViewport);
    }
    
    public TextDialog getCurrentDialog(){ return dialogHandler.getCurrentDialog(); }

    public boolean npcSpeechIsVisible(){
        return !getCurrentDialog().getHidden();
    }
}
