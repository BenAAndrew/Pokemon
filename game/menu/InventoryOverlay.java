package uk.ac.qub.eeecs.game.menu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.tools.Tools;
/**
 * <h1>Inventory Overlay </h1>
 * A collective of bitmaps and text used to create an information window, overlaying another screen
 *
 * @author  Kristina Geddis
 * @version 1.0
 */

public class InventoryOverlay {
    //Private variables
    protected boolean isVisible = false;

    protected String title;
    protected String[] description;
    protected Bitmap overlay, previewImage;

    private PushButton returnButton;

    private int x,y,width,height;

    private static final int BUTTON_WIDTH_AND_HEIGHT = 25;
    protected static final int CHARACTERS_PER_LINE = 20;

    private Paint descriptionPaint, titlePaint;

    //Constructor for testing purposes
    public InventoryOverlay(){

    }

    /**
     * This constructor initialises InventoryOverlay with its positioning and return button
     * @param x Int to set the x position of the bitmap
     * @param y Int to set the y position of the bitmap
     * @param width Int to set the width of the bitmap
     * @param height Int to set the height of the bitmap
     * @param imageOverlay Bitmap to declare what bitmap is being used
     * @param font Typeface sets font for text
     * @param screen GameScreen for declaring pushbutton
     * @author  Kristina Geddis
     */
    public InventoryOverlay(int x, int y, int width, int height, Bitmap imageOverlay, Typeface font, int fontSize, GameScreen screen){
        this.overlay = imageOverlay;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.returnButton = new PushButton(x+(width*0.4f),y+(height*0.4f),BUTTON_WIDTH_AND_HEIGHT,BUTTON_WIDTH_AND_HEIGHT,"CloseButton",screen);

        this.descriptionPaint = new Paint();
        descriptionPaint.setColor(Color.BLACK);
        descriptionPaint.setTextSize(fontSize);
        descriptionPaint.setTypeface(font);

        this.titlePaint = new Paint();
        titlePaint.setColor(Color.YELLOW);
        titlePaint.setTextSize(fontSize*1);
        titlePaint.setTypeface(font);
        titlePaint.setUnderlineText(true);
    }

    /**
     * This method sets the items information such as name, description and image and sets
     * the overlay to visible
     * @param item PreviewItem used for getting item details
     * @author  Kristina Geddis
     */
    public void showItem(PreviewItem item) {
        title = item.getName();
        description = Tools.convertStringToMultiLine(item.getDescription(),-1,CHARACTERS_PER_LINE);
        previewImage = item.getImage();
        isVisible = true;
    }

    /**
     * This method updates the return button so when pressed it returns the user back to the main game
     * @param elapsedTime ElapsedTime used for update methods
     * @author  Kristina Geddis
     */
    public void update(ElapsedTime elapsedTime){
        returnButton.update(elapsedTime);
        if(returnButton.isPushTriggered()){
            isVisible = false;
        }
    }

    /**
     * This method draws;
     * -Buttons
     * -The overlay window and its preview image
     * -Text for describing the item in the overlay
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @param layerViewport LayerViewport used in draw methods
     * @param screenViewport ScreenViewport used in draw methods
     * @author  Kristina Geddis
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        if(isVisible){
            graphics2D.drawBitmap(overlay,x,y,width,height,descriptionPaint,layerViewport,screenViewport);
            graphics2D.drawBitmap(previewImage, (int) ((int) x+(width*0.3f)), (int) ((int) y+(height*0.2f)), 65,75,descriptionPaint,layerViewport,screenViewport);
            returnButton.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
            graphics2D.drawText(title,x-(width*0.35f), y+(height*0.35f),titlePaint,layerViewport, screenViewport);
            for(int i = 0; i < description.length; i++){
                graphics2D.drawText(description[i],x-(width*0.4f), y-(height*0.01f)-(15*i),descriptionPaint, layerViewport, screenViewport);
            }
        }
    }

    /**
     * This method determines whether the overlay screen is visible
     * @return isVisible which sets a boolean flag
     * @author Kristina Geddis
     */
    public boolean isVisible() {
        return isVisible;
    }
}
