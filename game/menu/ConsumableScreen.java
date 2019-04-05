package uk.ac.qub.eeecs.game.menu;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.inventory.ConsumableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;

/**
 * <h1>Consumable Screen</h1>
 * Class containing functionality to display and process the consumable screen during a fight
 * @author Shannon Turley
 */
public class ConsumableScreen extends GameScreen {
    private Inventory inventory;
    private Player player;
    private GameEnvironment gameEnvironment;

    private ArrayList<Pair<PushButton, PreviewItem>> itemButtons;
    private PushButton resume;

    private float layerViewportWidth;
    private float layerViewportHeight;

    private final float ITEMS_PER_ROW = 4.0f;
    private final float ROWS_PER_SCREEN = 4.0f;

    private final float ITEM_SIZE = 50.0f;

    private final float COLUMN_FACTOR = 1f / ITEMS_PER_ROW;
    private final float ROW_FACTOR = 1.0f / ROWS_PER_SCREEN;

    private boolean shouldEndTurn;

    /**
     * Constructor to create a new Consumable Screen, set the height and width and initialise the
     * array list of buttons
     * @param game Game to attach screen to
     */
    public ConsumableScreen(Game game) {
        super("ConsumableScreen", game);
        isVisible = false;
        layerViewportWidth = mDefaultLayerViewport.getWidth();
        layerViewportHeight = mDefaultLayerViewport.getHeight();
        resume = new PushButton(layerViewportWidth*0.2f,layerViewportWidth*0.05f,80,30,"resume",this);
        itemButtons = new ArrayList<>();
        shouldEndTurn = false;
    }

    /**
     * Checks whether buttons have been pressed and if so consumes item corresponding to
     * button.
     * @param elapsedTime Elapsed time information for the frame
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        resumeGame();
        resume.update(elapsedTime,mDefaultLayerViewport,mDefaultScreenViewport);

        if (!itemButtons.isEmpty()) {
            for (int i = 0; i < itemButtons.size(); i++) {
                PushButton button = itemButtons.get(i).first;
                if (button.isPushTriggered()) {
                    inventory.consume((ConsumableItem) itemButtons.get(i).second, player);
                    resetConsumableItems();

                    this.shouldEndTurn = true;
                    this.isVisible = false;
                    this.gameEnvironment.screenManager.fightScreen.setVisible(true);
                }
                button.update(elapsedTime);
            }
        }
    }

    /**
     * Draws all push buttons corresponding to consumable items to the screen.
     * @param elapsedTime Elapsed time information for the frame
     * @param graphics2D  Graphics instance used to draw the screen
     * @author Shannon Turley
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(255, 218, 86));

        for (Pair<PushButton, PreviewItem> button : itemButtons) {
            PushButton pushButton = button.first;
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            graphics2D.drawBitmap(mGame.getAssetManager().getBitmap("Tile"),
                    (int) pushButton.getBound().x, (int) pushButton.getBound().y,
                    (int) (pushButton.getWidth() * 1.5f), (int) (pushButton.getHeight() * 1.5), p,
                    mDefaultLayerViewport, mDefaultScreenViewport);
            pushButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        }
        resume.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
    }

    /**
     * Sets up data needed for the consumable screen to function, such as a player,
     * inventory and game environment.
     * @param player Player in fight
     * @param gameEnvironment Game Environment
     * @author Shannon Turley
     */
    public void setConsumableData(Player player, GameEnvironment gameEnvironment) {
        this.player = player;
        this.inventory = player.getInventory();
        this.gameEnvironment = gameEnvironment;
        setConsumableItems();
    }

    /**
     * Resets the consumable item list back to empty.
     * @author Shannon Turley
     */
    private void resetConsumableItems(){
        itemButtons = new ArrayList<>();
    }

    /**
     * Sets up consumable items in the inventory as buttons in rows and columns using a percentage of the
     * screen width and height. Adds these buttons to a Pair of itemButton and item.
     * @author Shannon Turley
     */
    protected void setConsumableItems() {
        float xFactor = COLUMN_FACTOR;
        float yFactor = 1;
        for (PreviewItem item : inventory.getItems()) {
            if (item != null && item instanceof ConsumableItem) {
                float xPos = layerViewportWidth * xFactor - ITEM_SIZE;
                float yPos = layerViewportHeight * yFactor - ITEM_SIZE;

                PushButton itemButton = new PushButton(xPos, yPos, ITEM_SIZE, ITEM_SIZE,
                        item.getImageName(), this);
                itemButtons.add(new Pair<>(itemButton, item));

                if (xFactor == 1) {
                    xFactor = COLUMN_FACTOR;
                    yFactor -= ROW_FACTOR;
                } else {
                    xFactor += COLUMN_FACTOR;
                }

            }

        }
    }

    /**
     * Resumes game if resume button is pressed
     * @author Shannon Turley
     */
    protected void resumeGame(){
        if(resume.isPushTriggered()){
            isVisible=false;
            this.gameEnvironment.screenManager.fightScreen.setVisible(true);
        }
    }

    /**
     * Sets the should end turn boolean to value passed in
     * @param shouldEndTurn boolean
     * @author Shannon Turley
     */
    public void setShouldEndTurn(boolean shouldEndTurn) {
        this.shouldEndTurn = shouldEndTurn;
    }

    /**
     * Returns whether the turn should end or not
     * @return boolean should end turn
     * @author Shannon Turley
     */
    public boolean shouldEndTurn() {
        return shouldEndTurn;
    }
}

