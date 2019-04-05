package uk.ac.qub.eeecs.game.menu;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.gameWorld.inventory.ConsumableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.EquippableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.DraggableItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;
/**
 * <h1>Inventory Screen</h1>
 * A screen class for user interactions to use the items that are either picked up or bought.
 * This screen also allows user to consume/equip specific items and also find out more about a certain item
 *
 * @author  Kristina Geddis
 * @version 1.0
 */

public class InventoryScreen extends GameScreen{
    //private variables
    protected PushButton resume;
    private int width, height;
    private Paint paint;
    private GameObject background;

    //Arraylist pair used for holding draggable items with their preview items for description
    protected ArrayList<Pair<DraggableItem, PreviewItem>> items = new ArrayList<Pair<DraggableItem, PreviewItem>>();
    //Single pair used to hold draggable item and equippable item for currently equipped armour
    protected Pair<DraggableItem, EquippableItem> equippedArmour;
    //Single pair used to hold draggable item and equippable item for currently equipped weapon
    protected Pair<DraggableItem, EquippableItem> equippedWeapon;
    //InventoryOverlay for outputting item details when long pressed
    protected InventoryOverlay overlay;
    private int totalItems = 0;
    private int totalQuests = 0;

    private int money = 0;
    private Vector2 moneyPosition;

    //Sets the starting position of x and y for items to be drawn
    private static final float STARTING_X = 0.40f;
    private static final float STARTING_Y = 0.68f;
    //The difference in x and y calculated for items to be drawn on a different row
    private static final float X_DIFFERENCE = 0.2f;
    private static final float Y_DIFFERENCE = -0.30f;

    private static final int TILE_WIDTH_AND_HEIGHT = 100;
    private static final int ITEM_WIDTH = 65;
    private static final int ITEM_HEIGHT = 45;
    private static final int EQUIPPED_ARMOUR_WIDTH = 75;
    private static final int EQUIPPED_WEAPON_WIDTH = 40;

    private static final float SCROLL_SENSITIVITY = 0.3f;
    //Holds the current y change in regards to window scrolling
    private int currentVerticalDifference = 0;

    private static final int ITEMS_PER_ROW = 3;
    private static final int VISIBLE_ROWS = 2;

    //Creates a dynamic tile arraylist to add backgrounds behind each item
    protected ArrayList<GameObject> tileBackground = new ArrayList<GameObject>();
    private Bitmap tile;
    //Index of the last dragged item to check this item first and make sure its drawn over other items
    //when dragged across the screen
    private int lastDraggedIndex = 0;

    //Creates an area for consumable/equippable items to be dragged into
    protected HashMap<String, GameObject> snappingMap = new HashMap<String, GameObject>();
    //boolean flag for whether or not to update the inventory and items
    protected boolean update = false;

    //boolean flag of whether a consumed action has occured to play the animation
    protected boolean consumedAction = false;
    protected AnimationManager animationManager;
    protected String animationJSON = "ConsumeMapAnimation";
    private boolean visible = false;

    //Constructor for testing purposes
    public InventoryScreen(Game game){
        super("InventoryScreen", game);
    }

    /**
     * This constructor initialises InventoryScreen with its animation, snappingMaps and buttons
     * @param game Game for current game
     * @param background Bitmap sets background image
     * @param infoWindow Bitmap sets a bitmap for infoWindow
     * @param font Typeface sets font for text
     * @author  Kristina Geddis
     */
    public InventoryScreen(Game game, Bitmap background, Bitmap infoWindow, Typeface font, int fontSize) {
        super("InventoryScreen", game);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(fontSize);
        paint.setTypeface(font);

        width = (int)mDefaultLayerViewport.getWidth();
        height = (int)mDefaultLayerViewport.getHeight();

        moneyPosition = new Vector2(width*0.8f, height*0.9f);
        this.background = new GameObject(width/2,height/2,height*1.5f,height,background,this);
        this.overlay = new InventoryOverlay(width/2,height/2,220,280,infoWindow, font, fontSize, this);
        resume = new PushButton(width*0.2f,height*0.05f,80,30,"resume",this);

        /**
         * These variables of type float are based on the whole characters dimensions and the snapping map uses these
         * dimensions to calculate positions and width and height for the snappingMap areas.
         */
        float characterX = width*0.2f;
        float characterY = height/2;
        float characterWidth = 133;
        float characterHeight = 240;

        /**
         * SnappingMap is a Hashmap which contains the GameObject areas which are used for the user to drag items into.
         * If the dragged item is an equippable item it will be equipped and move to the set area.
         * If the dragged item is a consumable item it will then play an animation and delete the item consumed.
         */
        snappingMap.put("Consume",new GameObject(characterX, characterY,characterWidth,characterHeight, null, this));
        snappingMap.put("Armour",new GameObject(characterX, characterY+(characterHeight*0.45f),characterWidth*0.53f,characterHeight*0.16f,mGame.getAssetManager().getBitmap("HatMap"),this));
        snappingMap.put("Weapon",new GameObject(characterX-(characterWidth*0.4f), characterY-(characterHeight*0.04f),characterWidth*0.2f,characterHeight*0.136f,mGame.getAssetManager().getBitmap("EquipMap"),this));

        /**
         * Uses the animationManager to play an animation from a JSON when an item is consumed
         */
        animationManager = new AnimationManager(snappingMap.get("Consume"));
        animationManager.addAnimation("txt/animation/"+animationJSON+".JSON");

        tile = mGame.getAssetManager().getBitmap("Tile");
    }

    public boolean isVisible() {
        return visible || overlay.isVisible();
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    /**
     * This method resumes to the main game screen when the resume button is pushed
     * @author  Kristina Geddis
     */
    public void resumeGame(){
        if(resume.isPushTriggered()){
            visible=false;
        }
    }

    /**
     * This method goes through all draggable items and checking if they are either dragged or pressed
     * and links to corresponding actions (handles dragEvent or shows the overlay)
     * @param elapsedTime ElapsedTime used for updating draggableItem and dragEvents
     * @param player Player used for dragEvent
     * @author  Kristina Geddis
     */
    private void checkItemsDragged(ElapsedTime elapsedTime, Player player){
        for(int i = 0; i < items.size(); i++) {
            DraggableItem draggableItem = items.get(i).first;
            draggableItem.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
            if(draggableItem.isDragged()) {
                dragEvent(i, player, elapsedTime);
                lastDraggedIndex = i;
                break;
            }else if (draggableItem.isTapped()) {
                overlay.showItem(items.get(i).second);
            }
        }
    }

    /**
     * This method checks if the last dragged item is still being dragged and applies dragEvent to it if this is the case.
     * Otherwise it will check all other draggable items. If no items are found to be dragged it will check scrolling.
     * @param elapsedTime ElapsedTime used for updating draggableItem and dragEvents and checkItemsDragged
     * @param player used for dragEvent and checkItemsDragged
     * @author  Kristina Geddis
     */
    public void scrollEvent(ElapsedTime elapsedTime, Player player){
        if(!items.isEmpty()){
            DraggableItem lastDraggedItem = items.get(lastDraggedIndex).first;
            lastDraggedItem.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);
            if(lastDraggedItem.isDragged()){
                dragEvent(lastDraggedIndex, player, elapsedTime);
            }else{
                int previouslyDraggedItem = lastDraggedIndex;
                checkItemsDragged(elapsedTime, player);

                if(previouslyDraggedItem == lastDraggedIndex){
                    checkScroll();
                }
            }
        }
    }

    /**
     * This method equips the item if equippable and of Armour type.
     * Also resets lastDraggedIndex to indicate this item is not being dragged anymore and sets an update flag so that
     * this item will now be drawn on the player.
     * @param inventory Inventory used to equip the item
     * @param item PreviewItem to be equipped
     * @author  Kristina Geddis
     */
    protected void equipArmour(Inventory inventory, PreviewItem item){
        if (inventory.getEquipped(EquippableItem.Category.ARMOUR) == null || !inventory.getEquipped(EquippableItem.Category.ARMOUR).equals(item)){
            if(item instanceof EquippableItem &&
                    ((EquippableItem) item).getCategory().equals(EquippableItem.Category.ARMOUR)) {
                inventory.equip((EquippableItem) item);
                lastDraggedIndex = 0;
                update = true;
            }
        }
    }

    /**
     * This method equips the item if equippable and of Weapon type.
     * Also resets lastDraggedIndex to indicate this item is not being dragged anymore and sets an update flag so that
     * this item will now be drawn on the player.
     * @param inventory Inventory used to equip the item
     * @param item PreviewItem to be equipped
     * @author  Kristina Geddis
     */
    protected void equipWeapon(Inventory inventory, PreviewItem item){
        if(inventory.getEquipped(EquippableItem.Category.WEAPON) == null || !inventory.getEquipped(EquippableItem.Category.WEAPON).equals(item)){
            if(item instanceof EquippableItem &&
                    ((EquippableItem) item).getCategory().equals(EquippableItem.Category.WEAPON)) {
                inventory.equip((EquippableItem) item);
                lastDraggedIndex = 0;
                update = true;
            }
        }
    }

    /**
     * This method consumes the item if consumable and of Consume type.
     * Also resets lastDraggedIndex to indicate this item is not being dragged anymore and sets an update flag so that
     * this item will now be drawn on the player.
     * @param elapsedTime ElapsedTime used to play animation
     * @param player Player used to update the player HP
     * @param item PreviewItem to be consumed
     * @author  Kristina Geddis
     */
    protected void consumeItem(ElapsedTime elapsedTime, Player player, PreviewItem item){
        if(item instanceof ConsumableItem &&
                ((ConsumableItem)item).getCategory().equals(ConsumableItem.Category.HEALTH)){
            animationManager.play("Consumed", elapsedTime);
            player.getInventory().consume((ConsumableItem) item, player);
            consumedAction = true;
            lastDraggedIndex = 0;
            update = true;
        }
    }

    /**
     * This method goes through all areas that items can be dragged to and depending on the item type (Armour,
     * Weapon or Consume) it will decide what to do with those items when they are dragged over the snappingMap area
     * i.e. if a weapon is dragged over weapon area it will equip the weapon
     * @param itemIndex int used to get the item index
     * @param player Player to get inventory
     * @param elapsedTime ElapsedTime used for consumeItem animation
     * @author  Kristina Geddis
     */
    public void dragEvent(int itemIndex, Player player, ElapsedTime elapsedTime){
        Inventory inventory = player.getInventory();
        for (String area : snappingMap.keySet()) {
            if (items.get(itemIndex).first.overlaps(snappingMap.get(area))) {
                PreviewItem item = items.get(itemIndex).second;
                switch (area){
                    case "Armour":
                        equipArmour(inventory, item);
                        break;
                    case "Weapon":
                        equipWeapon(inventory, item);
                        break;
                    case "Consume":
                        consumeItem(elapsedTime,player,item);
                }
            }
        }
    }

    /**
     * This method is used to calculate the scroll change and if within the scroll limit, moves all items and
     * tiles accordingly.
     * @param scroll TouchEvent used to calculate scroll distance
     * @author  Kristina Geddis
     */
    private void scrollObjects(TouchEvent scroll){
        int verticalPosition = (int) (scroll.dy * SCROLL_SENSITIVITY);
        int scrollLimit = (items.size() / ITEMS_PER_ROW)* TILE_WIDTH_AND_HEIGHT;
        if (currentVerticalDifference + verticalPosition < scrollLimit &&
                currentVerticalDifference + verticalPosition > 0) {
            currentVerticalDifference += verticalPosition;
            for (int i = 0; i < items.size(); i++) {
                items.get(i).first.addToYDifference(verticalPosition);
            }
            for (GameObject tile : tileBackground) {
                tile.position.y += verticalPosition;
            }
        }
    }

    /**
     * This method checks that a scroll has been made and it passes the event to scrollObjects()
     * @author  Kristina Geddis
     */
    public void checkScroll(){
        if(items.size()> VISIBLE_ROWS * ITEMS_PER_ROW) {
            List<TouchEvent> touchEvents = mGame.getInput().getTouchEvents();
            for (TouchEvent touchScroll : touchEvents) {
                if (touchScroll.type == TouchEvent.TOUCH_SCROLL) {
                    scrollObjects(touchScroll);
                }
            }
        }
    }

    /**
     * This method goes through all inventory items and creates draggable items with images
     * and sorts these into rows and columns.
     * Also assigns the equipped Weapon and Armour to assigned positions.
     * @param inventory Inventory used get items
     * @author  Kristina Geddis
     */
    protected void addItemsToScreen(Inventory inventory){
        items = new ArrayList<Pair<DraggableItem, PreviewItem>>();
        tileBackground = new ArrayList<GameObject>();

        for(int i = 0; i < inventory.getItems().size(); i++){
            PreviewItem previewItem = inventory.getItems().get(i);
            DraggableItem draggableItem = new DraggableItem(width*(STARTING_X + (i%ITEMS_PER_ROW)*X_DIFFERENCE), height*(STARTING_Y + (Y_DIFFERENCE*(int)(i/ITEMS_PER_ROW))), ITEM_WIDTH, ITEM_HEIGHT, inventory.getItems().get(i).getImage(), this);
            tileBackground.add(new GameObject(draggableItem.position.x, draggableItem.position.y, TILE_WIDTH_AND_HEIGHT,TILE_WIDTH_AND_HEIGHT,tile,this));
            items.add(new Pair<DraggableItem, PreviewItem>(draggableItem, previewItem));
        }

        if(inventory.getEquipped(EquippableItem.Category.WEAPON) != null){
            DraggableItem draggableItem = new DraggableItem(snappingMap.get("Weapon").position.x, snappingMap.get("Weapon").position.y, EQUIPPED_WEAPON_WIDTH,ITEM_HEIGHT,inventory.getEquipped(EquippableItem.Category.WEAPON).getImage(), this);
            equippedWeapon = new Pair<DraggableItem, EquippableItem>(draggableItem, inventory.getEquipped(EquippableItem.Category.WEAPON));
        }
        if(inventory.getEquipped(EquippableItem.Category.ARMOUR) != null){
            DraggableItem draggableItem = new DraggableItem(snappingMap.get("Armour").position.x, snappingMap.get("Armour").position.y, EQUIPPED_ARMOUR_WIDTH,ITEM_HEIGHT,inventory.getEquipped(EquippableItem.Category.ARMOUR).getImage(), this);
            equippedArmour = new Pair<DraggableItem, EquippableItem>(draggableItem, inventory.getEquipped(EquippableItem.Category.ARMOUR));
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime){
    }

    /**
     * This method updates; items if inventory changes, money, resume button, scrollEvent, overlay and animation
     * @param elapsedTime ElapsedTime used for update methods
     * @param player Player used to fetch inventory for updating items
     * @author  Kristina Geddis
     */
    public void update(ElapsedTime elapsedTime, Player player) {
        /**
         * If statement to check that the number of items has changed or the update flag has been set
         * (when an item is equipped/consumed)
         * Quests also need to be checked as completing a quest may give the player another item
         */
        if(totalItems != player.getInventory().getItems().size() || totalQuests != player.getInventory().getQuests().size() || update){
            addItemsToScreen(player.getInventory());
            totalItems = player.getInventory().getItems().size();
            totalQuests = player.getInventory().getQuests().size();
            update = false;
        }

        money = player.getInventory().getMoney();

        resumeGame();
        resume.update(elapsedTime,mDefaultLayerViewport,mDefaultScreenViewport);

        scrollEvent(elapsedTime, player);

        if(overlay.isVisible()){
            overlay.update(elapsedTime);
        }

        animationManager.update(elapsedTime);
    }

    /**
     * This method draws;
     * -Buttons
     * -Tiles
     * -Overlay: if an item is long pressed it will pop up with an information window
     * -Areas for the items to be dragged to and if the item is a consumable the area will play an animation
     * -It also draws the draggable item last so that when the current item that is being dragged
     * is dragged over other items it will draw over them
     * -Equipped and unequipped items
     * @param elapsedTime ElapsedTime used in draw methods
     * @param graphics2D IGraphics2D used in draw methods
     * @author  Kristina Geddis
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.rgb(255, 218, 86));
        background.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        resume.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        for(GameObject tile : tileBackground){
            tile.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        }
        for(String area : snappingMap.keySet())
            if(area.equals("Consume"))
                animationManager.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
            else
                snappingMap.get(area).draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        graphics2D.drawText("money: "+String.valueOf(money),moneyPosition.x, moneyPosition.y, paint, mDefaultLayerViewport, mDefaultScreenViewport);

        if(items.size() != 0){
            for(int i = 0; i < items.size(); i++){
                if(i != lastDraggedIndex)
                    items.get(i).first.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
            }
            items.get(lastDraggedIndex).first.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        }
        if(equippedArmour != null)
            equippedArmour.first.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        if(equippedWeapon != null)
            equippedWeapon.first.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
        overlay.draw(elapsedTime,graphics2D,mDefaultLayerViewport,mDefaultScreenViewport);
    }
}


