package uk.ac.qub.eeecs.game.fight;

import java.util.ArrayList;

/**
 * <h1>Fightable</h1>
 * Interface for objects that are involved in the fight system
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public interface Fightable {

    /**
     * This method returns the name of the object
     * @author Matthew Breen
     * @return String is the name of the object for text in fight system
     */
    String getName();

    /**
     * This method returns whether an attack would hit the object or not
     * @author Matthew Breen
     * @return Boolean is whether it hit or not
     */
    boolean isHit();

    /**
     * This method adds an effect to the object
     * @author Matthew Breen
     * @param effect Effect is the effect of an attack move
     */
    void addEffect(Effect effect);

    /**
     * This method processes the current effects of the object and returns messages if necessary
     * @author Matthew Breen
     * @return ArrayList<String> is the messages returned
     */
    ArrayList<String> processEffects();

    /**
     * This method adds health passed to the users health and if it is over max health health is set to max health
     * @author Matthew Breen
     * @param health float is the amount you want to heal
     */
    void heal(float health);

    /**
     * This method returns the current health of the object
     * @author Matthew Breen
     * @return float is the current health of the object
     */
    float getHealth();

    /**
     * This method returns the max health of the object
     * @author Matthew Breen
     * @return float is the max health of the object
     */
    float getMaxHealth();

    /**
     * This method returns the current evade of the object
     * @author Matthew Breen
     * @return float is the current evade of the object
     */
    float getEvade();
}
