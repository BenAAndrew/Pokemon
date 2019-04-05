package uk.ac.qub.eeecs.game.gameWorld.npcInteractions;

import android.graphics.Typeface;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;

/**
 * <h1>Dialog Handler</h1>
 * Manages Dialogs for NPC interactions.
 * Makes decisions on which dialog to show and
 * when to increment/reset dialogs.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class DialogHandler {
    protected ArrayList<TextDialog> dialogs = new ArrayList<TextDialog>();
    protected TextDialog currentDialog;
    protected int fontSize, charactersPerLine;
    private Typeface font;
    protected int dialogIndex = 0;
    protected boolean resetFlag = false;
    //interactionSuccessful corresponds to ChoiceDialogs check and is needed for NPC to check events
    protected boolean interactionSuccessful = false;

    /**
     * This constructor declares DialogHandler with font size,
     * characters per line & font
     * @author Ben Andrew
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     */
    public DialogHandler(int fontSize, int charactersPerLine, Typeface font) {
        this.fontSize = fontSize;
        this.charactersPerLine = charactersPerLine;
        this.font = font;
    }

    /**
     * Adds ChoiceDialog to DialogHandler and
     * adds a clearing index if previous messages
     * should be deleted if this dialog is successful
     * @author Ben Andrew
     * @param choiceDialog ChoiceDialog to be added
     */
    public void add(ChoiceDialog choiceDialog) {
        dialogs.add(choiceDialog);
    }

    /**
     * Adds choiceDialog to DialogHandler
     * with default font characteristics from dialogHandler.
     * @author Ben Andrew
     * @param gameScreen GameScreen for ChoiceDialog
     * @param message String for ChoiceDialog message
     * @param item PreviewItem for ChoiceDialog interaction
     */
    public void add(GameScreen gameScreen, String message, PreviewItem item) {
        dialogs.add(new ChoiceDialog(gameScreen, message, font, fontSize, charactersPerLine, item));
    }

    /**
     * Adds TextDialog to DialogHandler
     * @author Ben Andrew
     * @param textDialog TextDialog to be added
     */
    public void add(TextDialog textDialog){
        dialogs.add(textDialog);
    }

    /**
     * Adds TextDialog to DialogHandler
     * with default font characteristics from dialogHandler.
     * @author Ben Andrew
     * @param gameScreen GameScreen used to declare TextDialog
     * @param message String for TextDialog message
     */
    public void add(GameScreen gameScreen, String message) {
        dialogs.add(new TextDialog(gameScreen, message, font, fontSize, charactersPerLine));
    }

    /**
     * Adds TextDialog to DialogHandler
     * with default font characteristics from dialogHandler but custom y position.
     * @author Matthew Breen
     * @param gameScreen GameScreen used to declare TextDialog
     * @param message String for TextDialog message
     * @param y int y position of text dialogue
     */
    public void add(GameScreen gameScreen, String message, int y) {
        dialogs.add(new TextDialog(gameScreen, message, font, fontSize, charactersPerLine, y));
    }

    /**
     * Adds ChoiceDialog to DialogHandler
     * with default font characteristics from dialogHandler and
     * boolean for whether the dialog is a shopDialog.
     * @author Ben Andrew
     * @param gameScreen GameScreen used to declare ChoiceDialog
     * @param message String for ChoiceDialog message
     * @param shopDialog boolean for whether dialog is a shop dialog
     */
    public void add(GameScreen gameScreen, String message, boolean shopDialog) {
        dialogs.add(new TextDialog(gameScreen, message, font, fontSize, charactersPerLine, shopDialog));
    }

    public TextDialog getCurrentDialog() {
        if(currentDialog == null && !dialogs.isEmpty()){
            currentDialog = dialogs.get(0);
        }
        return currentDialog;
    }

    /**
     * Deletes all dialogs up to and including current dialog.
     * Resets dialogIndex and interactionSuccessful flag.
     * @author Ben Andrew
     */
    protected void removePreviousDialogs(){
        for (int i = 0; i <= dialogIndex; i++) {
            dialogs.remove(0);
        }
        dialogIndex = 0;
        //reset interactionSuccessful for the next use of ChoiceDialog
        interactionSuccessful = false;
    }

    /**
     * Hides the dialog and resets all dialogs.
     * Goes back to first dialog to be shown as the opening
     * dialog next time DialogHandler is used.
     * @author Ben Andrew
     */
    protected void restartDialogChain(){
        getCurrentDialog().setHidden(true);
        for (TextDialog d : dialogs) {
            d.resetDialog();
        }
        dialogIndex = 0;
        currentDialog = dialogs.get(dialogIndex);
    }

    /**
     * Deletes all dialogs and resets dialog index
     * @author Ben Andrew
     */
    public void clear() {
        dialogs = new ArrayList<TextDialog>();
        currentDialog = null;
        dialogIndex = 0;
    }

    /**
     * Decides which resetting process should be used.
     * Will either clear all messages for shop, delete previous
     * messages if a successful ChoiceDialog and a clear flag has been
     * set, or reset the dialogs and restart.
     * @author Ben Andrew
     */
    protected void resetDialogHandler() {
        if (currentDialog.shopDialog) {
            clear();
        } else {
            restartDialogChain();
        }
        resetFlag = false;
    }


    public boolean isInteractionSuccessful() {
        return interactionSuccessful;
    }

    public void setInteractionSuccessful(boolean interactionSuccessful) {
        this.interactionSuccessful = interactionSuccessful;
    }

    /**
     * Sets interaction success flag and displays success dialog
     * if one exists. Also deletes previous dialogs if flag is set
     * @author Ben Andrew
     */
    protected void choiceDialogSuccess(){
        interactionSuccessful = true;
        ChoiceDialog choiceDialog = ((ChoiceDialog) getCurrentDialog());
        TextDialog successDialog = choiceDialog.getSuccessMessage();
        if (successDialog != null) {
            currentDialog.setHidden(true);
            currentDialog = successDialog;
            currentDialog.setHidden(false);
        }
        if(choiceDialog.deleteMessagesBefore())
            removePreviousDialogs();
    }

    /**
     * Displays failure dialog
     * @author Ben Andrew
     */
    protected void choiceDialogRejected(){
        currentDialog.setHidden(true);
        currentDialog = ((ChoiceDialog) getCurrentDialog()).getFailureMessage();
        currentDialog.setHidden(false);
    }

    /**
     * Gets the current dialog state and matches to corresponding action
     * such as showing success/ failure dialog.
     * @author Ben Andrew
     */
    public void handleChoiceDialog() {
        TextDialog.DialogState state = ((ChoiceDialog) getCurrentDialog()).getState();
        switch (state){
            case CHOICE_ACCEPTED: choiceDialogSuccess(); resetFlag = true; break;
            case CHOICE_REJECTED: choiceDialogRejected(); resetFlag = true; break;
            case CHOICE_DECLNED: resetFlag = true; break;
        }
    }

    /**
     * Updates current dialog whether a ChoiceDialog or TextDialog. Also uses handleChoiceDialog
     * to check for ChoiceDialog events
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in both dialog types update methods
     * @param inventory Inventory used in ChoiceDialog update
     */
    private void updateCurrentDialog(ElapsedTime elapsedTime, Inventory inventory){
        if (getCurrentDialog() instanceof ChoiceDialog) {
            ((ChoiceDialog) getCurrentDialog()).update(elapsedTime, inventory);
            handleChoiceDialog();
        } else {
            getCurrentDialog().update(elapsedTime);
        }
    }

    /**
     * Updates dialogHandler by updating current dialog as well
     * as evaluating whether to reset, increment or hide the current dialog
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in both dialog types update methods
     * @param inventory Inventory used in ChoiceDialog update
     */
    public void update(ElapsedTime elapsedTime, Inventory inventory) {
        if (getCurrentDialog() != null) {
            updateCurrentDialog(elapsedTime,inventory);
            //checks that dialog is ready to move on as well as reset flag before resetting
            if (resetFlag && getCurrentDialog().playNext()) {
                resetDialogHandler();
            } else if (dialogIndex < dialogs.size() - 1 && getCurrentDialog().playNext()) {
                dialogIndex++;
                currentDialog.setHidden(true);
                currentDialog = dialogs.get(dialogIndex);
                currentDialog.setHidden(false);
            /*last else if means dialogIndex has reached end of dialogs and so doesn't increment
             but instead hides and resets the current dialog */
            } else if (getCurrentDialog().playNext()) {
                getCurrentDialog().setHidden(true);
                getCurrentDialog().resetDialog();
            }
        }
    }

    /**
     * Updates dialogHandler by updating current dialog as well
     * as evaluating whether to reset, increment or hide the current dialog.
     * Simplified for use in fight screen.
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in both dialog types update methods
     */
    public void update(ElapsedTime elapsedTime){
        if(getCurrentDialog() != null) {
            getCurrentDialog().update(elapsedTime);
            if (dialogIndex < dialogs.size() - 1 && getCurrentDialog().playNext()) {
                dialogIndex++;
                currentDialog.setHidden(true);
                currentDialog = dialogs.get(dialogIndex);
                currentDialog.setHidden(false);
            } else if (getCurrentDialog().playNext()) {
                getCurrentDialog().setHidden(true);
            }
        }
    }

    public ArrayList<TextDialog> getDialogs() {
        return dialogs;
    }
}