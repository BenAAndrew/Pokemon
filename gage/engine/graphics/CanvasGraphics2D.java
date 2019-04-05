package uk.ac.qub.eeecs.gage.engine.graphics;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;


/**
 * Graphics2D class that provides basic draw functionality for a canvas
 *
 * @version 1.
 */
public class CanvasGraphics2D implements IGraphics2D {

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Canvas onto which this graphics instance will render
     */
    private Canvas mCanvas;

    /**
     * Height and width of the canvas
     */
    private int mWidth;
    private int mHeight;

    /**
     * Asset manager
     */
    private AssetManager mAssetManager;

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Constructors
    // /////////////////////////////////////////////////////////////////////////

    public CanvasGraphics2D(AssetManager assets) {
        this.mAssetManager = assets;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Set the canvas onto which this graphics instance can render
     *
     * @param canvas Canvas to draw to
     */
    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawBitmap(android.graphics
     * .Bitmap, android.graphics.Rect, android.graphics.Rect,
     * android.graphics.Paint)
     */
    @Override
    public void drawBitmap(Bitmap bitmap, Rect srcRect, Rect desRect,
                           Paint paint) {
        mCanvas.drawBitmap(bitmap, srcRect, desRect, paint);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawBitmap(android.graphics
     * .Bitmap, android.graphics.Matrix, android.graphics.Paint)
     */
    @Override
    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        mCanvas.drawBitmap(bitmap, matrix, paint);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawText(java.lang.String,
     * float, float, android.graphics.Paint)
     */
    @Override
    public void drawText(String text, float x, float y, Paint paint) {
        mCanvas.drawText(text, x, y, paint);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawRect(java.lang.float,
     * float, float, float, android.graphics.Paint)
     */
    @Override
    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        mCanvas.drawRect(left, top, right, bottom, paint);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#clear(int)
     */
    @Override
    public void clear(int color) {
        mCanvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Configuration
    // /////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D#clipRect(android.graphics.Rect)
     */
    @Override
    public void clipRect(Rect clipRegion) { mCanvas.clipRect(clipRegion); }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#getSurfaceWidth()
     */
    @Override
    public int getSurfaceWidth() {
        return mWidth;
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#getSurfaceHeight()
     */
    @Override
    public int getSurfaceHeight() {
        return mHeight;
    }

    @Override
    public void drawBitmap(Bitmap bitmap, int x, int y, int width, int height,
                           Paint paint, LayerViewport layerViewport, ScreenViewport screenViewport) {
//        Vector2 location = new Vector2();
//        ViewportHelper.convertLayerPosIntoScreen(layerViewport,x,y, screenViewport,location);
        Rect drawScreenRect = new Rect();
        Rect drawSourceRect = new Rect();
        drawScreenRect.set(x - width/2,
                y - height/2,
                x + width/2,
                y + height/2);
        BoundingBox bound = new BoundingBox(x,y,width/2,height/2);
        if (GraphicsHelper.getClippedSourceAndScreenRect(bound, bitmap,layerViewport, screenViewport, drawSourceRect, drawScreenRect)) {
            drawBitmap(bitmap, null, drawScreenRect,paint);
        }

    }

    @Override
    public void drawText(String text, float x, float y, Paint paint, LayerViewport layerViewport, ScreenViewport screenViewport){
        Vector2 location = new Vector2();
        ViewportHelper.convertLayerPosIntoScreen(layerViewport,x,y, screenViewport,location);
        drawText(text, location.x, location.y, paint);
    }

    public void drawRect(Rect rect, Paint paint, LayerViewport layerViewport, ScreenViewport screenViewport){
        float left = ViewportHelper.convertXDistanceFromLayerToScreen(rect.left, layerViewport, screenViewport);
        float right = ViewportHelper.convertXDistanceFromLayerToScreen(rect.right, layerViewport, screenViewport);
        float top = ViewportHelper.convertYDistanceFromLayerToScreen(rect.top, layerViewport, screenViewport);
        float bottom = ViewportHelper.convertYDistanceFromLayerToScreen(rect.bottom, layerViewport, screenViewport);
        mCanvas.drawRect(left, top, right, bottom, paint);
    }

    @Override
    public void drawCentredText(String text, float x, float y, Paint paint, int fontSize, LayerViewport layerViewport, ScreenViewport screenViewport){
        Vector2 location = new Vector2();
        int width = text.length() * fontSize / 2;
        ViewportHelper.convertLayerPosIntoScreen(layerViewport,x,y, screenViewport,location);
        drawText(text, location.x - width, location.y + fontSize / 2, paint);
    }
}
