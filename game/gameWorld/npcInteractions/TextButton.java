package uk.ac.qub.eeecs.game.gameWorld.npcInteractions;

import android.graphics.Paint;
import android.graphics.Typeface;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * <h1>Text Button</h1>
 * Class for creating PushButtons with text overlaid onto them.
 * Uses a default background so that buttons with different text
 * can be created dynamically on different screens
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class TextButton {
    private static final String BUTTON_BACKGROUND = "ButtonBackground";
    private PushButton button;
    private float x,y;
    private String text;
    private int fontsize;
    private Paint paint = new Paint();

    /**
     * This constructor declares TextButton with a PushButton, text,
     * font data & paint.
     * @author Ben Andrew
     * @param x float for button x position
     * @param y float for button y position
     * @param width float for button width
     * @param height float for button height
     * @param gameScreen GameScreen for declaring PushButton
     * @param message String for button text
     * @param fontSize int for font size
     * @param font TypeFace for text font
     */
    public TextButton(float x, float y, float width, float height, GameScreen gameScreen, String message, int fontSize, Typeface font) {
        this.button = new PushButton(x, y, width, height, BUTTON_BACKGROUND, gameScreen);
        this.x = x;
        this.y = y;
        this.text = message;
        this.fontsize = fontSize;
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
    }

    /**
     * Draws PushButton and text
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @param layerViewport LayerViewport used in draw methods
     * @param screenViewport ScreenViewport used in draw methods
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        button.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
        graphics2D.drawCentredText(text,x,y,paint,fontsize,layerViewport,screenViewport);
    }

    /**
     * Updates the PushButton for checking whether it is pushed
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime used in PushButton update
     */
    public void update(ElapsedTime elapsedTime){
        button.update(elapsedTime);
    }

    /**
     * Returns whether PushButton has been pressed (uses
     * isPushedTriggered rather than isPushed to only get first push
     * rather than continuous push)
     * @author Ben Andrew
     */
    public boolean isPressed(){
        return button.isPushTriggered();
    }

    public String getText() {
        return text;
    }
}

