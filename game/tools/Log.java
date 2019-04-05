package uk.ac.qub.eeecs.game.tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.json.JSONException;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * <h1>Log</h1>
 * Class for logging throughout the game to diagnose
 * errors and behaviours whilst the game is running.
 * Uses default Log.d as well as an internal array
 * to output to game screen
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class Log {
    private final int MAX_LINES = 20;
    private final int LINE_SPACE = 10;
    protected final int CHARACTERS_PER_LINE = 20;
    private final int MAX_LINES_PER_LOG = 5;
    private final int TOP_PADDING = 60;
    protected final int FONT_SIZE = 40;
    protected String[][] log = new String[MAX_LINES][MAX_LINES_PER_LOG];
    private int index = 0;
    private int lineCount = 0;
    private Rect logScreen;
    protected boolean visible = false;
    protected Paint whitePaint, blackPaint;
    private float width, height;

    /**
     * Takes a tag and message to save log to log array
     * and send to android log
     * @param tag String of log tag
     * @param message String of log message
     */
    public void addLog(String tag, String message){
        if(visible) {
            android.util.Log.d(tag, message);
            String[] lines = Tools.convertStringToMultiLine(tag + ": " + message, MAX_LINES_PER_LOG, CHARACTERS_PER_LINE);
            lineCount += lines.length;
            if(lineCount > MAX_LINES){
                index = 0;
                lineCount = 0;
            }
            log[index++] = lines;
        }
    }

    /**
     * Reverses visibility (if visible sets not visible
     * and visa-versa)
     */
    public void toggleVisible(){
        this.visible = !this.visible;
    }

    /**
     * Initialises key Log properties as log has
     * no constructor and so is set up on first use.
     * Sets logScreen position, initialises log array
     * and initialises paints.
     * @author Ben Andrew
     * @param layerViewport LayerViewport for screen dimensions
     */
    protected void initialiseScreen(LayerViewport layerViewport){
        width = layerViewport.getWidth();
        height = layerViewport.getHeight();
        logScreen = new Rect((int) (width*0.8f), 0, (int) width, (int) height);
        for(int i = 0; i < MAX_LINES; i++){
            String[] emptyLines = new String[MAX_LINES_PER_LOG];
            for(int j = 0; j < MAX_LINES_PER_LOG; j++){
                emptyLines[j] = "";
            }
            log[i] = emptyLines;
        }
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(FONT_SIZE);
    }

    /**
     * Draws log screen and each line for
     * all visible logs (capped at MAX_LINES).
     * Also draw fps counter.
     * @author Ben Andrew
     * @param graphics2D IGraphics2D for draw methods
     * @param layerViewport LayerViewport for draw methods
     * @param screenViewport ScreenViewport for draw methods
     * @param fps int for outputting current fps
     */
    public void draw(IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, int fps){
        if(visible){
            if(logScreen == null){
                initialiseScreen(layerViewport);
            }
            graphics2D.drawRect(logScreen, whitePaint, layerViewport, screenViewport);
            int i = 0;
            int logIndex = 0;
            while(i < MAX_LINES){
                if(log.length > logIndex){
                    for(String line : log[logIndex]){
                        graphics2D.drawText(line, logScreen.left, logScreen.bottom - TOP_PADDING - (LINE_SPACE * i), blackPaint, layerViewport, screenViewport);
                        i++;
                    }
                    logIndex++;
                } else {
                    break;
                }
            }
            graphics2D.drawText("FPS:"+String.valueOf(fps), width/2, height*0.9f, blackPaint, layerViewport, screenViewport);
        }
    }
}
