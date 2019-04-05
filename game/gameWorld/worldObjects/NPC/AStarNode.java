package uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC;

import uk.ac.qub.eeecs.gage.util.Vector2;

/**
 * <h1>A* Node</h1>
 * Holds data for a node during the A* traversal process.
 * Contains the current position (Vector2), cost to reach
 * that position, and an AStarNode of the previous node
 * so that the nodes can be backtracked to output the whole
 * route.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class AStarNode {
    private Vector2 coordinates;
    //previous node used to rebuild route when A* is complete
    private AStarNode previous;
    private int cost;

    /**
     * This constructor declares AStarNode with the previous node,
     * coordinates and cost to reach the nodes position
     * @param previous AStarNode of previous node in path
     * @param coordinates Vector2 of node position on AStarNavigation grid
     * @param cost int of cost to reach the nodes position
     */
    public AStarNode(AStarNode previous, Vector2 coordinates, int cost){
        this.previous = previous;
        this.coordinates = coordinates;
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public int getX() {
        return (int) coordinates.x;
    }

    public int getY() {
        return (int) coordinates.y;
    }

    public Vector2 getCoordinates() {
        return coordinates;
    }

    public AStarNode getPrevious() {
        return previous;
    }

    /**
     * Checks whether passed position matches this nodes position
     * (checks x and y of both)
     * @author Ben Andrew
     * @param position Vector2 to compare to this nodes position
     */
    public boolean positionEquals(Vector2 position){
        return this.coordinates.x == position.x && this.coordinates.y == position.y;
    }
}
