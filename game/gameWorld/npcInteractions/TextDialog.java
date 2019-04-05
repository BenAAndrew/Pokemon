package uk.ac.qub.eeecs.game.gameWorld.npcInteractions;

import android.graphics.Paint;
import android.graphics.Typeface;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.Button;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>Text Dialog</h1>
 * Class for creating Dialogs which output messages
 * and can be pressed. Used for NPC communication with player
 * in the world, in fights and in shops. Also extended in ChoiceDialog
 * to enable buttons for options.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class TextDialog extends Button {
    public enum DialogState{
        VISIBLE, HIDDEN, CHOICE_ACCEPTED, CHOICE_REJECTED, CHOICE_DECLNED
    }
    /*DialogState is used to track and identify the current dialogs state.
    In TextDialog only visible/hidden are used but ChoiceDialogs uses all others
    to monitor user choices
     */
    private final int Y_TEXT_OFFSET = 5;
    //hidden by default
    protected DialogState state = DialogState.HIDDEN;
    protected int textCounter = 0;
    protected boolean completedMessage = false;
    protected String[] message;
    protected int messageLength;
    private Paint paint = new Paint();
    protected int charactersPerLine;
    protected boolean pressed = false;
    protected final int MAX_LINES = 2;
    //Used for text positioning
    protected Vector2 linePosition = new Vector2(50,65);
    /*shopDialog changes the position of the dialog, by default
    is at the bottom of the screen, but in shop is higher.*/
    protected boolean shopDialog = false;

    //constructor for testing only
    public TextDialog(GameScreen gameScreen){
        super(240,200,470,80,null,true,gameScreen);
    }

    /**
     * Constructor for testing purposes
     * @author  Ben Andrew
     */
    public TextDialog(GameScreen gameScreen, String message, int charactersPerLine){
        super(0,0,0,0,null,true,gameScreen);
        this.message = Tools.convertStringToMultiLine(message, MAX_LINES, charactersPerLine);
        for(String line : this.message)
            messageLength += line.length();
        this.charactersPerLine = charactersPerLine;
    }

    /**
     * This constructor declares TextDialog with a message,
     * font information & a boolean indicating dialog is used in a shop
     * (changes y position)
     * @author Ben Andrew
     * @param gameScreen GameScreen for declaring parent Button
     * @param message String for dialog text
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     * @param shopDialog boolean for whether dialog is used in a shop
     */
    public TextDialog(GameScreen gameScreen, String message, Typeface font, int fontSize, int charactersPerLine, boolean shopDialog){
        super(240,200,470,80,"Dialog",true,gameScreen);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
        this.charactersPerLine = charactersPerLine;
        this.message = Tools.convertStringToMultiLine(message, MAX_LINES, charactersPerLine);
        for(String line : this.message)
            messageLength += line.length();
        this.shopDialog = shopDialog;
        if(shopDialog)
            linePosition.y = 205;
    }

    /**
     * This constructor declares TextDialog with a message &
     * font information
     * @author Ben Andrew
     * @param gameScreen GameScreen for declaring parent Button
     * @param message String for dialog text
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     */
    public TextDialog(GameScreen gameScreen, String message, Typeface font, int fontSize, int charactersPerLine){
        super(240,60,470,80,"Dialog",true,gameScreen);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
        this.charactersPerLine = charactersPerLine;
        this.message = Tools.convertStringToMultiLine(message, MAX_LINES, charactersPerLine);
        for(String line : this.message)
            messageLength += line.length();
    }

    /**
     * This constructor declares TextDialog with a message,
     * font information & a custom y position
     * @author Matthew Breen
     * @param gameScreen GameScreen for declaring parent Button
     * @param message String for dialog text
     * @param font TypeFace for text font
     * @param fontSize int for font size
     * @param charactersPerLine int for max characters per line
     * @param y int for dialog y position
     */
    public TextDialog(GameScreen gameScreen, String message, Typeface font, int fontSize, int charactersPerLine, int y){
        super(240,y,470,80,"Dialog",true,gameScreen);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
        this.charactersPerLine = charactersPerLine;
        this.message = Tools.convertStringToMultiLine(message, MAX_LINES, charactersPerLine);
        for(String line : this.message)
            messageLength += line.length();
        state = DialogState.VISIBLE;
        linePosition.y =y+Y_TEXT_OFFSET;
    }

    /**
     * Checks whether dialog has been pressed to play next message
     * (pressed can only be set when the message is fully outputted)
     * @author Ben Andrew
     * @return boolean dialog pressed
     */
    public boolean playNext(){
        return pressed;
    }

    /**
     * Sets pressed if message has been fully outputted (called
     * on touch event in parent class Button)
     * @author Ben Andrew
     * @param touchEvent TouchEvent used in parent class Button
     * @param touchLocation Vector2 used in parent class Button
     */
    @Override
    protected void updateTriggerActions(TouchEvent touchEvent, Vector2 touchLocation) {
        if(completedMessage){
            pressed = true;
        }
    }

    /**
     * Gets whether dialog is currently hidden
     * @author Ben Andrew
     * @retun boolean to whether state matches hidden
     */
    public boolean getHidden(){
        return state == DialogState.HIDDEN;
    }

    /**
     * Resets dialog buy hiding it
     * and resetting textCounter as well
     * as completed/ pressed flags
     * @author Ben Andrew
     */
    public void resetDialog(){
        this.state = DialogState.HIDDEN;
        textCounter = 0;
        completedMessage = false;
        pressed = false;
    }

    /**
     * Sets dialog state depending on passed boolean.
     * If true sets state to hidden, otherwise visible
     * @author Ben Andrew
     * @param hidden boolean on whether to hide dialog
     */
    public void setHidden(boolean hidden){
        if(!hidden)
            this.state = DialogState.VISIBLE;
        else
            this.state = DialogState.HIDDEN;
    }

    /**
     * Returns message up to a set number of characters (textCounter).
     * This is used to animate text by updating text counter each frame,
     * which in turns add another character to the outputted lines.
     * @author Ben Andrew
     * @return String[] message up to the set number of characters
     */
    protected String[] currentLines(){
        String[] lines = new String[message.length];
        int charactersUsed = 0;
        for(int i = 0; i < message.length; i++){
            if(textCounter-charactersUsed > 0) {
                //uses Math.min to ensure substring doesn't exceed line length (resulting in error)
                lines[i] = message[i].substring(0, Math.min(message[i].length(), textCounter - charactersUsed));
                //uses counter to record how many characters have been used by each line so far
                charactersUsed += message[i].length();
            } else{
                //empty if we've used all out characters on the previous lines
                lines[i] = "";
            }
        }
        return lines;
    }

    /**
     * Increments textCounter if less than messageLength,
     * otherwise flags message as completed to go to next dialog
     * on click
     * @author Ben Andrew
     */
    protected void updateTextCounter(){
        if(textCounter < messageLength) {
            textCounter++;
        }else{
            completedMessage = true;
        }
    }

    /**
     * Draws the parent Button as well as all
     * currently visible lines. Text counter is updated here
     * and will also modify how much of the message is output
     * each frame in currentLines()
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @param layerViewport LayerViewport used in draw methods
     * @param screenViewport ScreenViewport used in draw methods
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport){
        if(state != DialogState.HIDDEN){
            super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
            updateTextCounter();
            String[] lines = currentLines();
            for(int i = 0; i < lines.length; i++){
                if(lines[i] != "")
                    graphics2D.drawText(lines[i], linePosition.x, linePosition.y-(20*i), paint, layerViewport, screenViewport);
            }
        }
    }

    //Unused in TextDialog
    @Override
    protected void updateTouchActions(Vector2 touchLocation) { }

    //Unused in TextDialog
    @Override
    protected void updateDefaultActions() { }
}