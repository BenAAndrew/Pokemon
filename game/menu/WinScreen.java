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
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.fight.Enemy;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>WinScreen</h1>
 * Class for displaying the rewards after winning fight
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public class WinScreen extends GameScreen {
    private int width, height;
    private Paint paint;
    private Paint paintTitle;
    private Player player;
    private Enemy enemy;
    private String text;
    private int fontSize;

    /**
     * This constructor declares Win Screen with the following parameters
     * @author Matthew Breen
     * @param game Game for the game creating this screen
     * @param player Player for player information in fight
     * @param font Typeface for the font you want this class to use
     * @param fontSize int for the font size
     */
    public WinScreen(Game game, Player player, Typeface font, int fontSize) {
        super("win", game);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
        this.fontSize = fontSize;
        paintTitle = new Paint();
        paintTitle.setColor(Color.BLACK);
        paintTitle.setTextSize(fontSize+40);
        paintTitle.setTypeface(font);
        width = (int)mDefaultLayerViewport.getWidth();
        height = (int)mDefaultLayerViewport.getHeight();
        this.player = player;
        isVisible = false;
    }

    /**
     * This method sets up the win screen and displays it
     * @author Matthew Breen
     * @param enemy Enemy the enemy that was defeated
     */
    public void display(Enemy enemy){
        player.resetEffects();
        this.enemy = enemy;
        text = "Reward:";
        for(PreviewItem item  : enemy.getRewardItems()) {
            player.getInventory().addItem(item);
            text+="\n"+item.getName();
        }
        if(enemy.getRewardMoney() > 0) {
            player.getInventory().transaction(enemy.getRewardMoney());
            text += "\nGold: " + enemy.getRewardMoney();
        }
        isVisible = true;
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
                    this.setVisible(false);
            }
        }
    }

    /**
     * This method draws the background and text of win screen
     * @author Matthew Breen
     * @param elapsedTime ElapsedTime used for draw
     * @param graphics2D IGraphics2D used for draw
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(186, 244, 144));
        graphics2D.drawCentredText("You beat "+enemy.getName(),width*0.5f,height*0.6f,paintTitle,fontSize+40,mDefaultLayerViewport,mDefaultScreenViewport);
        int count  = 0;
        for(String line:text.split("\n")) {
            graphics2D.drawCentredText(line, width * 0.5f, height * 0.4f - count*20, paint, fontSize, mDefaultLayerViewport, mDefaultScreenViewport);
            count++;
        }
    }
}