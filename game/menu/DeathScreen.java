package uk.ac.qub.eeecs.game.menu;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.List;
import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.AnimatedObject;

/**
 * <h1>DeathScreen</h1>
 * Class for displaying when the player has died
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public class DeathScreen extends GameScreen {
    //Private variables
    private AnimatedObject background;
    private Paint gameOverText, continueText;
    private int width, height;

    /**
     * This constructor initialises DeathScreen with its animated background and text
     * @param game Game used to decalre parent game screen
     * @param font Typeface used to set the font for the text
     * @param fontSize int sets the size of the text
     * @author Kristina Geddis
     */
    public DeathScreen(Game game, Typeface font, int fontSize) {
        super("death", game);
        isVisible = false;
        gameOverText = new Paint();
        gameOverText.setColor(Color.RED);
        gameOverText.setTextSize(fontSize*2);
        gameOverText.setTypeface(font);
        continueText = new Paint();
        continueText.setColor(Color.YELLOW);
        continueText.setTextSize(fontSize*1);
        continueText.setTypeface(font);
        width = (int) mDefaultLayerViewport.getWidth();
        height = (int) mDefaultLayerViewport.getHeight();
        background = new AnimatedObject(width/2,height/2+(height*0.2f),height*2.0f,height*0.8f,this, "DeathScreen", "GameOver");
    }

    /**
     * This method closes the window if if the screen is pressed
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime needed for update methods
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        List<TouchEvent> touchEvents = mGame.getInput().getTouchEvents();
        if(touchEvents.size() > 0){
            for (TouchEvent touch:touchEvents) {
                if(touch.type == TouchEvent.TOUCH_DOWN)
                    mGame.onBackPressed();
            }
        }
        background.update(elapsedTime);
    }

    /**
     * This method draws the animated background and text
     * @param elapsedTime Elapsed time information for the frame
     * @param graphics2D  Graphics instance used to draw the screen
     * @author Kristina Geddis
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(25, 25, 25));
        background.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        graphics2D.drawCentredText("Game Over", width/2, height*0.25f, gameOverText, (int)gameOverText.getTextSize(), mDefaultLayerViewport, mDefaultScreenViewport);
        graphics2D.drawCentredText("Press anywhere to continue...", width/2, height*0.1f, continueText, (int)continueText.getTextSize(), mDefaultLayerViewport, mDefaultScreenViewport);

    }
}