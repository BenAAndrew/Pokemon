package uk.ac.qub.eeecs.game.gameWorld.quest;
// Where each individual Mission is setup, and called upon

import java.util.ArrayList;
import java.lang.String;

/*
 * <h1>Quest</h1>
 * User Defined Data Type, that stores two different Strings for each index in a storage facility
 *
 * @author Daniel Bell :)
 * @version 1.0
 */
public class Quest {
    /**
     * Properties
     */
    private String name, description;
    // complete - Flag
    protected boolean complete = false;

    // ********************************************************************************************

    /**
     * Constructor - Creates a Quest
     *
     * Parameters to be passed through Constructor, in order to establish an instance of Quest:-
     * @param name
     * @param description
     * @author Daniel Bell :)
     */
    public Quest(String name, String description){
        this.name = name;
        this.description = description;
    }

    // ********************************************************************************************

    /**
     * Accessor(s) & Mutator(s) - in-line functions
     * @author Daniel Bell :)
     */
    public String getName(){ return name; }

    public String getDescription() { return description; }

    public boolean getComplete(){ return complete; } // For testing purposes

    public void setComplete(){ complete = true; }
}