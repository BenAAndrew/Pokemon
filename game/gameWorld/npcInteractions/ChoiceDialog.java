package uk.ac.qub.eeecs.game.gameWorld.npcInteractions;

import android.graphics.Typeface;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC.NPCInteractionHandler;

/**
 * <h1>Choice Dialog</h1>
 * Extends TextDialog to include TextButtons for
 * the user to interact with. Used in shops,
 * and communicating with NPCs to accept & complete
 * quests.
 *
 * @author  Ben Andrew & Matthew Breen
 * @version 1.0
 */
public class ChoiceDialog extends TextDialog {
    private ArrayList<TextButton> buttons = new ArrayList<TextButton>();
    private NPCInteractionHandler interactionHandler;
    protected TextDialog successMessage;
    protected TextDialog failureMessage;
    protected boolean deleteMessagesBefore = false;
    private String choice;

    //constructor for testing only
    public ChoiceDialog(GameScreen gameScreen){
        super(gameScreen);
    }

    /**
     * This constructor declares ChoiceDialog with a textDialog and buttons
     * @author Ben Andrew
     * @param gameScreen GameScreen for placing TextDialog & TextButtons
     * @param message String for TextDialog text
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     * @param options ArrayList of string to convert into buttons
     */
    public ChoiceDialog(GameScreen gameScreen, String message, Typeface font, int fontSize, int charactersPerLine, ArrayList<String> options,int y, int x){
        super(gameScreen,message,font,fontSize,charactersPerLine/2,y);
        int width = (int)gameScreen.getDefaultLayerViewport().getWidth();
        buttons = new ArrayList<>();
        int count = 0;
        for(int i = 0; i < options.size(); i++){
            buttons.add(new TextButton(x+((width-100-x)/(options.size()-1))*count, y, 90,40,gameScreen,options.get(i),fontSize,font));
            count++;
        }
        choice ="";
    }

    /**
     * This constructor declares ChoiceDialog default buttons
     * and interaction for buying items. Takes previewItem being sold.
     * @author Ben Andrew
     * @param gameScreen GameScreen for placing TextDialog & TextButtons
     * @param message String for TextDialog text
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     * @param item PreviewItem to be sold (given to player)
     */
    public ChoiceDialog(GameScreen gameScreen, String message, Typeface font, int fontSize, int charactersPerLine, PreviewItem item){
        super(gameScreen,message,font,fontSize,charactersPerLine/2,true);
        buttons.add(new TextButton(320, 200, 80,40,gameScreen,"yes",fontSize,font));
        buttons.add(new TextButton(420, 200, 80,40,gameScreen,"no",fontSize,font));
        this.interactionHandler = new NPCInteractionHandler(NPCInteractionHandler.NPCInteractionType.BUY, item);
        this.failureMessage = new TextDialog(gameScreen,"You cannot afford this yet",font,fontSize,charactersPerLine,true);
    }

    /**
     * This constructor declares ChoiceDialog with buttons,
     * and interaction handler, success/failure message and boolean to whether
     * to delete previous dialogs on success.
     * @author Ben Andrew
     * @param gameScreen GameScreen for placing TextDialog & TextButtons
     * @param message String for TextDialog text
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     * @param options ArrayList of string to convert into buttons
     * @param interactionHandler NPCInteractionHandler to manage player to NPC
     * interaction
     * @param successMessage String of message shown on interaction success
     * @param failureMessage String of message show on interaction failure
     * @param deleteMessagesBefore boolean to whether to delete previous dialogs on
     * interaction success
     */
    public ChoiceDialog(GameScreen gameScreen, String message, Typeface font, int fontSize, int charactersPerLine, ArrayList<String> options,
                        NPCInteractionHandler interactionHandler, String successMessage, String failureMessage, boolean deleteMessagesBefore){
        super(gameScreen,message,font,fontSize,charactersPerLine);
        for(int i = 0; i < options.size(); i++){
            buttons.add(new TextButton(320+(i*100), 60, 80,40,gameScreen,options.get(i),fontSize,font));
        }
        this.interactionHandler = interactionHandler;
        this.successMessage = new TextDialog(gameScreen,successMessage,font,fontSize,charactersPerLine);
        this.failureMessage = new TextDialog(gameScreen,failureMessage,font,fontSize,charactersPerLine);
        this.deleteMessagesBefore = deleteMessagesBefore;
    }

    /**
     * Overwrites TextDialog reset to also reset
     * success and failure message
     */
    @Override
    public void resetDialog(){
        super.resetDialog();
        if(successMessage != null)
            successMessage.resetDialog();
        failureMessage.resetDialog();
    }

    @Override
    protected void updateTriggerActions(TouchEvent touchEvent, Vector2 touchLocation) { }

    public DialogState getState(){
        return super.state;
    }

    public TextDialog getFailureMessage() {
        return failureMessage;
    }

    public TextDialog getSuccessMessage() {
        return successMessage;
    }

    /**
     * Updates TextDialog as well as all buttons,
     * and resets dialog or uses interaction handler
     * depending on which button is pressed. Updates
     * DialogState accordingly
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used to update TextDialog/TextButton
     * @param inventory Inventory for use in interaction handler
     * if needed
     */
    public void update(ElapsedTime elapsedTime, Inventory inventory){
        if(super.completedMessage){
            super.update(elapsedTime);
            for(TextButton b : buttons){
                b.update(elapsedTime);
                if (b.isPressed()) {
                    if(b.getText().equalsIgnoreCase("yes")){
                        if(interactionHandler.checkInteraction(inventory)){
                            super.state = DialogState.CHOICE_ACCEPTED;
                        } else {
                            super.state = DialogState.CHOICE_REJECTED;
                        }
                    } else {
                        super.state = DialogState.CHOICE_DECLNED;
                    }
                    super.pressed = true;
                }
            }
        }
    }

    /**
     * Updates TextDialog as well as all buttons and sets
     * choice to the button pressed
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used to update TextDialog/TextButton
     */
    @Override
    public void update(ElapsedTime elapsedTime){
        if(super.completedMessage){
            super.update(elapsedTime);
            for(TextButton b : buttons){
                b.update(elapsedTime);
                if (b.isPressed()) {
                    choice = b.getText();
                    super.pressed = true;
                }
            }
        }
    }

    /**
     * Draws parent TextDialog as well as all buttons
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @param layerViewport LayerViewport used in draw methods
     * @param screenViewport ScreenViewport used in draw methods
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport){
        if(super.state != DialogState.HIDDEN){
            super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
            for(TextButton b : buttons)
                b.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
        }
    }

    @Override
    protected void updateTouchActions(Vector2 touchLocation) { }

    @Override
    protected void updateDefaultActions() { }

    public boolean deleteMessagesBefore() {
        return deleteMessagesBefore;
    }

    public String getChoice(){
        return choice;
    }
}