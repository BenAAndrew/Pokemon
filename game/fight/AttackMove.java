package uk.ac.qub.eeecs.game.fight;

/**
 * <h1>Attack Move</h1>
 * Class for storing information about an attack move used by fightable objects
 *
 * @author  Matthew Breen
 * @version 1.0
 */
public class AttackMove {
    private String name;
    private String description;
    private Effect effect;
    private boolean selfEffect;

    /**
     * This constructor declares Attackmove with the name, description, it's effect and whether or not it is self directed.
     * @author Matthew Breen
     * @param name String is the name of the attack
     * @param description String is a description of what the attack does
     * @param effect Effect is the effect caused by the attack
     * @param selfEffect Boolean is whether or not it is self directed
     */
    public AttackMove(String name, String description, Effect effect, boolean selfEffect){
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.selfEffect = selfEffect;
    }

    /**
     * This method returns the name
     * @author Matthew Breen
     * @return String is the name of the attack
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the description
     * @author Matthew Breen
     * @return String is a description of what the attack does
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method returns the effect
     * @author Matthew Breen
     * @return Effect is the effect caused by the attack
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * This method returns the selfEffect
     * @author Matthew Breen
     * @return Boolean is whether or not it is self directed
     */
    public boolean isSelfEffect() {
        return selfEffect;
    }
}
