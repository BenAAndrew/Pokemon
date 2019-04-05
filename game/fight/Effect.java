package uk.ac.qub.eeecs.game.fight;

/**
 * <h1>Effect</h1>
 * Class for storing information about an effect of an attack move
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public class Effect {
    private float evadeChange;
    private float armourChange;
    private float healthChange;
    private int numTurns;
    private String endMessage;
    private String turnMessage;


    /**
     * This constructor declares effect with the evade change, armour change, health change, number
     * of turns it lasts for, the message that displays when it wheres off and the every turn message.
     * @author Matthew Breen
     * @param evadeChange float is the change to targets evade each turn
     * @param armourChange float is the change to targets armour each turn
     * @param healthChange float is the change to targets health each turn
     * @param numTurns int is how many turns the effect lasts for
     * @param endMessage String is the message displayed when the effect wears off
     * @param turnMessage String is the message displayed every turn while it is active
     */
    public Effect(float evadeChange, float armourChange, float healthChange, int numTurns, String endMessage, String turnMessage) {
        this.evadeChange = evadeChange;
        this.armourChange = armourChange;
        this.healthChange = healthChange;
        this.numTurns = numTurns;
        this.endMessage = endMessage;
        this.turnMessage = turnMessage;
    }


    /**
     * This constructor declares effect with health change. This effect is immediate
     * and does take place over multiple turns.
     * @author Matthew Breen
     * @param healthChange float is the change to targets health immediately
     */
    public Effect(float healthChange) {
        this.healthChange = healthChange;
        this.numTurns = 0;
    }

    /**
     * This method returns evadeChange
     * @author Matthew Breen
     * @return float is the change to targets evade each turn
     */
    public float getEvadeChange() {
        return evadeChange;
    }

    /**
     * This method returns armourChange
     * @author Matthew Breen
     * @return float is the change to targets armour each turn
     */
    public float getArmourChange() {
        return armourChange;
    }

    /**
     * This method returns healthChange
     * @author Matthew Breen
     * @return float is the change to targets health each turn or immediately depending on number of turns
     */
    public float getHealthChange() {
        return healthChange;
    }

    /**
     * This method returns numTurns
     * @author Matthew Breen
     * @return int is the number of turn remaining that the effect is active
     */
    public int getNumTurns() {
        return numTurns;
    }

    /**
     * This method returns endMessage
     * @author Matthew Breen
     * @return String is the message displayed when the effect wears off
     */
    public String getEndMessage() {
        return endMessage;
    }

    /**
     * This method returns turnMessage
     * @author Matthew Breen
     * @return String is the message displayed every turn while it is active
     */
    public String getTurnMessage() {
        return turnMessage;
    }

    /**
     * This method is called each turn and reduces the remaining number of turns by 1
     * @author Matthew Breen
     */
    public void processTurn(){
        numTurns--;
    }

    /**
     * This method returns a copy of the effect
     * @author Matthew Breen
     * @return Effect is the copy of the effect
     */
    public Effect copy(){
        if(numTurns > 0) {
            return new Effect(this.getEvadeChange(), this.getArmourChange(), this.getHealthChange(), this.getNumTurns(), this.getEndMessage(), this.getTurnMessage());
        }
        else{
            return new Effect(this.getHealthChange());
        }
    }

}
