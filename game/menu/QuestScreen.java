package uk.ac.qub.eeecs.game.menu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;
import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>Quest Screen</h1>
 * A screen class for user to read Active Quests that are either loaded in once user is commited to a quest.
 * This screen also allows user to find out more about their Active Quest
 *
 * @author Daniel Bell :) & Ben Andrew
 * @version 1.0
 */
public class QuestScreen extends GameScreen {
    /**
     * Properties
     */
    // resume - navigation button displayed/ used on-screen
    protected PushButton resume; // protected - for testing
    // quests - list of individual Quests/ mission names to be loaded
    private ArrayList<Quest> quests;
    // width & height - used in Constructing PushButton navigate
    private int width, height;
    private Paint paint;
    // visible - Flag;

    //Quests properties
    private int charactersPerLine;
    private Bitmap quest;
    private final float QUEST_BANNER_START_Y = 0.85f;
    private final float QUEST_TEXT_START_Y = 0.88f;
    private final float QUEST_BANNER_HEIGHT = 0.3f;
    private final float TEXT_X_POSITION = 0.12f;
    private final float LINE_SPACING = 0.09f;

    // ********************************************************************************************

    //Default constructor for testing
    public QuestScreen(Game game){
        super("QuestScreen", game);
    }

    /**
     * Constructor - Creates a GameScreen
     *
     * Parameters to be passed through Constructor, in order to establish an instance of QuestScreen :-
     * @param game                      instance of Game used by QuestScreen
     * @param font                      specified Typeface, for all text to be on-screen
     * @param fontSize                  specified fontSize, for all text to be on-screen
     * @param charactersPerLine         Upper-Bound of characters of text allowed on each Line of Text
     * @author                          Daniel Bell :) & Ben Andrew
     */
    public QuestScreen(Game game, Typeface font, int fontSize, int charactersPerLine) {
        super("QuestScreen", game);
        this.charactersPerLine = charactersPerLine;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);
        //Ben Andrew
        width = (int)mDefaultLayerViewport.getWidth();
        height = (int)mDefaultLayerViewport.getHeight();
        resume = new PushButton(width*0.5f,height*0.2f,150,50,"resume","resumeSelected",this);
        quest = mGame.getAssetManager().getBitmap("Quest");
        isVisible = false;
    }

    // ********************************************************************************************

    /**
     * Class Method(s)
     */

    /**
     * This method resumes to the main Game Screen when the resume button is pushed
     * @author Daniel Bell :)
     */
    public void resumeGame(){
        if(resume.isPushed()){
            isVisible=false;
        }
    }

    /**
     * This method updates; items if inventory changes, money, resume button, scrollEvent, overlay and animation
     * @param elapsedTime       ElapsedTime used for update methods
     * @param quests            List of Quests in ArrayList of user-Defined Data Type Questss
     * @author                  Daniel Bell :)
     */
    public void update(ElapsedTime elapsedTime, ArrayList<Quest> quests) {
        this.quests = quests;
        resumeGame();
        resume.update(elapsedTime,mDefaultLayerViewport,mDefaultScreenViewport);
    }

    @Override
    public void update(ElapsedTime elapsedTime) { }

    /**
     * draws screen background, button and lines
     * of text with background for each quest
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime required for draw methods
     * @param graphics2D IGraphics2D required for draw methods
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(255, 149, 79)); // Can change the colour later
        resume.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);

        if(quests != null){
            for(int i = 0; i < quests.size(); i++){
                String[] lines = Tools.convertStringToMultiLine(quests.get(i).getDescription(), 2, charactersPerLine);
                graphics2D.drawBitmap(quest, width/2, (int) (height*QUEST_BANNER_START_Y)-((int)(height*QUEST_BANNER_HEIGHT)*i), width, (int) (height*QUEST_BANNER_HEIGHT), paint, mDefaultLayerViewport, mDefaultScreenViewport);
                graphics2D.drawText(lines[0], width*TEXT_X_POSITION, (int) (height*QUEST_TEXT_START_Y)-((int)(height*QUEST_BANNER_HEIGHT)*i), paint, mDefaultLayerViewport, mDefaultScreenViewport);
                graphics2D.drawText(lines[1], width*TEXT_X_POSITION, (int) (height*QUEST_TEXT_START_Y)-((int)(height*QUEST_BANNER_HEIGHT)*i)-(height*LINE_SPACING), paint, mDefaultLayerViewport, mDefaultScreenViewport);
            }
        }
    }

}