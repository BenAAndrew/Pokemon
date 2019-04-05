package uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC;

import java.util.ArrayList;

import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Inventory;
import uk.ac.qub.eeecs.game.gameWorld.inventory.Key;
import uk.ac.qub.eeecs.game.gameWorld.inventory.PreviewItem;
import uk.ac.qub.eeecs.game.gameWorld.inventory.WorldItem;
import uk.ac.qub.eeecs.game.gameWorld.quest.Quest;

/**
 * <h1>NPC Interaction Handler</h1>
 * Class for managing NPC's interactions including selling items,
 * assigning quests & checking/taking/giving items.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class NPCInteractionHandler {
    public enum NPCInteractionType {
        GIVE, TAKE, BUY, QUEST, CHECK
    }
    private NPCInteractionType action;
    private PreviewItem item;

    //key used for take check
    private int keyId;
    //quest used for quest assignment
    private Quest quest;
    //questName used to remove quest when completed
    private String questName;

    /**
     * This constructor declares NPC interaction handler
     * with an action & item. Suitable for Give/Buy interaction types
     * @author Ben Andrew
     * @param action NPCInteractionType for interaction type
     * @param item PreviewItem for checking against interaction
     */
    public NPCInteractionHandler(NPCInteractionType action, PreviewItem item){
        if(action != NPCInteractionType.GIVE && action != NPCInteractionType.BUY)
            GameEnvironment.log.addLog("NPCInteractionHandler","Invalid Interaction constructor assignment");
        this.action = action;
        this.item = item;
    }

    /**
     * This constructor declares NPC interaction handler
     * with a keyId and either sets up a quest item take or
     * item check. Suitable for Take/Check interaction types
     * @author Ben Andrew
     * @param keyId int for key id (used in check)
     * @param rewardItem PreviewItem for rewarding player if successful
     * @param take boolean for whether to take or just check for the key
     * @param quest String for quest name (if assigning a quest)
     */
    public NPCInteractionHandler(int keyId, PreviewItem rewardItem, boolean take, String quest){
        if(action != NPCInteractionType.TAKE && action != NPCInteractionType.CHECK)
            GameEnvironment.log.addLog("NPCInteractionHandler","Invalid Interaction constructor assignment");
        if(take){
            this.action = NPCInteractionType.TAKE;
            this.questName = quest;
        } else {
            this.action = NPCInteractionType.CHECK;
        }
        this.keyId = keyId;
        this.item = rewardItem;
    }

    /**
     * This constructor declares NPC interaction handler
     * with an Quest interaction, quest object and reward item. Suitable
     * for Quest interaction type
     * @author Ben Andrew
     * @param quest Quest to be used in check
     * @param rewardItem PreviewItem for rewarding player if successful
     */
    public NPCInteractionHandler(Quest quest, PreviewItem rewardItem){
        this.action = NPCInteractionType.QUEST;
        this.quest = quest;
        this.item = rewardItem;
    }

    /**
     * Returns whether the interaction (for any interaction type)
     * was successful
     * @author Ben Andrew
     * @param inventory Inventory used to check if the player has an item
     * and remove/add an item depending on interaction type
     */
    public boolean checkInteraction(Inventory inventory){
        ArrayList<PreviewItem> items = inventory.getItems();
        switch(action){
            case GIVE: inventory.addItem(item); return true;
            case CHECK:
                for(int i = 0; i < items.size(); i++) {
                    if(items.get(i) instanceof Key){
                        if(((Key) items.get(i)).correctKey(keyId)){
                            return true;
                        }
                    }
                }
                break;
            case TAKE:
                for(int i = 0; i < items.size(); i++) {
                    if(items.get(i) instanceof Key){
                        if(((Key) items.get(i)).correctKey(keyId)){
                            inventory.removeItem(i);
                            if(item != null)
                                inventory.addItem(item);
                            inventory.removeQuest(questName);
                            return true;
                        }
                    }
                }
                break;
            case BUY:
                if(inventory.transaction(-item.getPrice())){
                    inventory.addItem(item);
                    return true;
                }
                break;
            case QUEST:
                inventory.addQuest(quest);
                return true;
        }
        return false;
    }
}
