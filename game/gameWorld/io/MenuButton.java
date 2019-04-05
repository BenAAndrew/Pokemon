package uk.ac.qub.eeecs.game.gameWorld.io;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.menu.InventoryScreen;
import uk.ac.qub.eeecs.game.menu.PauseScreen;
import uk.ac.qub.eeecs.game.menu.QuestScreen;
/**
 * <h1>Menu Button</h1>
 * A class used for creating a pushbutton with the button type to identify its action when clicked.
 * Each button type produces a different screen when clicked.
 *
 * @author  Kristina Geddis
 * @version 1.0
 */
public class MenuButton extends PushButton {

    //Enum for menu button type. Used in constructor and when a button is clicked to identify
    //which action should occur
    public enum MenuButtonType{
        INVENTORY, PAUSE, QUEST
    };

    protected MenuButtonType menuButtonType;

    /**
     * This constructor initialises parent push button and declares menu button type
     * @param buttonX float used to set the x position of the button
     * @param buttonY float used to set the y position of the button
     * @param buttonWidth float used to set the width of the button
     * @param buttonHeight float used to set the height of the button
     * @param gameScreen GameScreen used for pushbutton
     * @param menuButtonType MenuButtonType used to identify button type
     * @param bitmap String of bitmap name used in pushbutton
     * @author Kristina Geddis
     */
    public MenuButton(float buttonX, float buttonY, float buttonWidth, float buttonHeight, GameScreen gameScreen,
                      MenuButtonType menuButtonType, String bitmap) {
        super(buttonX, buttonY, buttonWidth, buttonHeight, bitmap, gameScreen);
        this.menuButtonType = menuButtonType;
    }

    /**
     * This method updates the button and opens up the corresponding screen for the button type when pushed.
     * @param elapsedTime ElapsedTime used for update method
     * @param layerViewport LayerViewport used for update method
     * @param screenViewport ScreenViewport used for update method
     * @param inventoryScreen InventoryScreen used for setting visible if of inventory type and pushed
     * @param pauseScreen PauseScreen used for setting visible if of pause type and pushed
     * @param questScreen QuestScreen used for setting visible if of quest type and pushed
     * @author Kristina Geddis
     */
    public void update(ElapsedTime elapsedTime, LayerViewport layerViewport, ScreenViewport screenViewport,
                       InventoryScreen inventoryScreen, PauseScreen pauseScreen, QuestScreen questScreen){
        super.update(elapsedTime,layerViewport,screenViewport);
        if(super.isPushTriggered()){
           handleButtonEvent(inventoryScreen,pauseScreen,questScreen);
        }
    }

    /**
     * This method is used to determine which screen to show when a button is pressed. This depends on the menu button type.
     * @param inventoryScreen InventoryScreen to be set visible if an inventory button
     * @param pauseScreen PauseScreen to be set visible if a pause button
     * @param questScreen QuestScreen to be set visible if a quest button
     * @author Kristina Geddis
     */
    public void handleButtonEvent(InventoryScreen inventoryScreen, PauseScreen pauseScreen, QuestScreen questScreen){
        switch (menuButtonType){
            case INVENTORY: inventoryScreen.setVisible(true); break;
            case PAUSE: pauseScreen.setVisible(true); break;
            case QUEST: questScreen.setVisible(true); break;
            default: GameEnvironment.log.addLog("MenuButton", "Invalid menu button type");
        }
    }
}
