package uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>A* Navigation</h1>
 * Navigation class for use in NPCs. Generates a grid structure
 * for passed maps/ objects to identify possible routes. Uses
 * A* to identify best route between two coordinates to
 * produce a step-by-step route to target.
 * Resource used; https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode
 * @author  Ben Andrew
 * @version 1.0
 */
public class AStarNavigation {
    protected boolean[][] grid;
    protected int[] mapDimensions;
    protected int[] mapBoundaries;
    protected static final int CELL_SIZE = 30;
    private static final int UPDATE_RATE = 5;
    private int lastUpdate = 0;
    private Random random = new Random();

    /**
     * This constructor declares AStarNavigation with map dimensions,
     * map boundaries, and objects for initialising map grid.
     * @author Ben Andrew
     * @param mapDimensions int[] for map dimensions (width & height)
     * @param mapBoundaries int[] for map boundaries (left, right, up & down)
     * @param objectBounds ArrayList for all object dimensions used to identify impassible areas
     */
    public AStarNavigation(int[] mapDimensions, int[] mapBoundaries, ArrayList<GameObject> objectBounds){
        this.mapDimensions = mapDimensions;
        this.mapBoundaries = new int[4];
        for(int i = 0; i < 4; i++){
            this.mapBoundaries[i] = mapBoundaries[i] / CELL_SIZE;
        }
        int width = (mapDimensions[0] / CELL_SIZE)+1;
        int height = (mapDimensions[1] / CELL_SIZE)+1;
        initialiseGrid(width,height);
        implementObjectBounds(objectBounds);
    }


    /**
     * Internal class required for Java PriorityQueue
     * to compare AStarNodes during A* processing.
     * Simply compares cost of both nodes to decide
     * which to continue exploring first.
     * @author Ben Andrew
     */
    private class NodeComparator implements Comparator<AStarNode>
    {
        public int compare(AStarNode n1, AStarNode n2)
        {
            return n1.getCost() - n2.getCost();
        }
    }

    /**
     * Fills grid with all empty (false) area.
     * @author Ben Andrew
     * @param width int for grid width in cells
     * @param height int for grid height in cells
     */
    protected void initialiseGrid(int width, int height){
        grid = new boolean[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                grid[i][j] = false;
            }
        }
    }

    /**
     * Uses object bounds to identify impassable areas
     * of the map grid and set them to blocked (true).
     * @author Ben Andrew
     * @param objectBounds ArrayList of GameObjects to get object bounds
     */
    protected void implementObjectBounds(ArrayList<GameObject> objectBounds){
        for(GameObject g : objectBounds){
            //ensure not to mark moving NPCs as impassible areas
            if(!(g instanceof MovingNPC)){
                BoundingBox b = g.getBound();
                int rightCell = (int) Math.ceil((int) b.getRight() / CELL_SIZE);
                int bottomCell = (int) Math.ceil((int) b.getBottom() / CELL_SIZE);
                int leftCell = (int) Math.floor((int) b.getLeft() / CELL_SIZE);
                int topCell = (int) Math.floor((int) b.getTop() / CELL_SIZE);
                for(int i = leftCell; i <= rightCell; i++){
                    for(int j = bottomCell; j <= topCell; j++){
                        grid[i][j] = true;
                    }
                }
            }
        }
        declareOutOfMapBounds();
    }

    /**
     * Marks areas of maps that npcs cant navigate areas (out of bounds)
     * as impassible (true).
     * @author Ben Andrew
     */
    private void declareOutOfMapBounds(){
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++) {
                if (i <= mapBoundaries[0] || i >= mapBoundaries[1] || j >= mapBoundaries[2] || j <= mapBoundaries[3]) {
                    grid[i][j] = true;
                }
            }
        }
    }

    /**
     * Creates 2D int array the same size as the map
     * grid with all values set at maximum integer value.
     * This is used in A* to track best path through the map
     * and update cells to their lowest cost.
     * @author Ben Andrew
     * @return int[][] grid of costs all set as maxint
     */
    protected int[][] initialiseCosts() {
        int[][] costs = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                costs[i][j] = Integer.MAX_VALUE;
            }
        }
        return costs;
    }

    /**
     * Returns a priorityQueue designd for comparing
     * AStarNodes and containing the starting node.
     * @author Ben Andrew
     * @param startingPosition Vector2 of starting cell position
     * @return PriorityQueue containing starting node
     */
    @TargetApi(Build.VERSION_CODES.N)
    private PriorityQueue<AStarNode> initialiseNodeQueue(Vector2 startingPosition){
        PriorityQueue<AStarNode> nodesToVisit = new PriorityQueue<AStarNode>(new NodeComparator());
        nodesToVisit.add(new AStarNode(null, startingPosition, 0));
        return nodesToVisit;
    }

    /**
     * Returns whether a cell can be passed through by checking it
     * is in the map and not obstructed.
     * @author Ben Andrew
     * @param cell Vector2 of cell position
     * @return boolean to whether cell can be reached
     */
    protected boolean withinBounds(Vector2 cell){
        boolean onMap = cell.x >= 0 && cell.x < grid.length && cell.y >= 0 && cell.y < grid[0].length;
        if(onMap && !grid[(int) cell.x][(int) cell.y]){
            return true;
        }
        return false;
    }

    /**
     * Converts passed vector in map pixel format in cell format
     * @author Ben Andrew
     * @param coordinates Vector2 of map position (pixels)
     * @return Vector2 of cell position in grid
     */
    protected Vector2 convertToCell(Vector2 coordinates){
        return new Vector2((int) ((coordinates.x / CELL_SIZE)), (int) ((coordinates.y / CELL_SIZE)));
    }

    /**
     * Converts passed vector array in cell format
     * into map pixel format
     * @author Ben Andrew
     * @param coordinates Vector2[] of grid positions
     * @return Vector2[] of positions in map format (pixels)
     */
    protected Vector2[] convertToPixels(Vector2[] coordinates){
        for(int i = 0; i < coordinates.length; i++){
            coordinates[i] = new Vector2((coordinates[i].x * CELL_SIZE) + CELL_SIZE /2, (coordinates[i].y * CELL_SIZE) + CELL_SIZE /2);
        }
        return coordinates;
    }

    /**
     * Iterates through each previousNode to build an
     * ArrayList of nodes for a path. Then converts this path
     * into a Vector array for NPC to follow to target
     * @author Ben Andrew
     * @param destinationNode AStarNode of final node at destination
     * @return Vector2[] of each step in order and in cell format
     */
    protected Vector2[] getSteps(AStarNode destinationNode){
        ArrayList<AStarNode> pathNodes = new ArrayList<AStarNode>();
        AStarNode currentNode = destinationNode.getPrevious();
        while(currentNode != null){
            pathNodes.add(currentNode);
            currentNode = currentNode.getPrevious();
        }
        Collections.reverse(pathNodes);
        pathNodes.add(destinationNode);
        return getPositionsFromNodes(pathNodes);
    }

    /**
     * Fetches coordinates for each node to produce a Vector[]
     * of positions for all nodes in a path
     * @author Ben Andrew
     * @param nodes ArrayList of nodes in path
     * @return Vector2[] of all coordinates found in nodes
     */
    protected Vector2[] getPositionsFromNodes(ArrayList<AStarNode> nodes){
        Vector2[] steps = new Vector2[nodes.size()];
        for(int i = 0; i < nodes.size(); i++){
            steps[i] = nodes.get(i).getCoordinates();
        }
        return steps;
    }

    /**
     * Uses A* traversal algorithm to find a possible
     * path through the map between two positions
     * @author Ben Andrew
     * @param startingPos Vector2 of starting coordinates
     * @param targetPos Vector2 of destination coordinates
     * @return Vector2[] of each step in path between start
     * and target. May return null if route cannot be found
     */
    public Vector2[] getPath(Vector2 startingPos, Vector2 targetPos){
        startingPos = convertToCell(startingPos);
        targetPos = convertToCell(targetPos);
        AStarNode destNode = calculatePath(startingPos,targetPos);
        if(destNode != null){
            Vector2[] steps = getSteps(destNode);
            return convertToPixels(steps);
        } else {
            GameEnvironment.log.addLog("AStarNavigation","Path cannot be found to: "+targetPos.x+","+targetPos.y);
            return null;
        }
    }

    /**
     * Creates array of surrounding cells for AStar to
     * check and explore if suitable
     * @author Ben Andrew
     * @param x int of current cells x position
     * @param y int of current cells y position
     * @return Vector2[] of four cells
     * surrounding cell at the x,y position
     */
    protected Vector2[] getCellNeighbours(int x, int y){
        Vector2[] neighbours = new Vector2[4];
        //left
        neighbours[0] = new Vector2(x-1, y);
        //top
        neighbours[1] = new Vector2(x, y+1);
        //right
        neighbours[2] = new Vector2(x+1, y);
        //bottom
        neighbours[3] = new Vector2(x, y-1);
        return neighbours;
    }

    /**
     * Implements A* searching to find most efficient
     * route between starting a target cell.
     * @author Ben Andrew
     * @param startingPos Vector2 of starting cell position
     * @param targetPos Vector2 of target cell position
     * @return AStarNode of destination node which also contains all
     * previous nodes produced to reach the target. If route cannot be found
     * null is returned
     */
    protected AStarNode calculatePath(Vector2 startingPos, Vector2 targetPos){
        int[][] costs = initialiseCosts();
        PriorityQueue<AStarNode> nodesToVisit = initialiseNodeQueue(startingPos);
        while(!nodesToVisit.isEmpty()){
            AStarNode current = nodesToVisit.remove();
            if(current.positionEquals(targetPos)) {
                return current;
            }
            Vector2[] neighbours = getCellNeighbours(current.getX(), current.getY());
            for(int i = 0; i < 4; i++){
                if(withinBounds(neighbours[i])){
                    int newCost = costs[current.getX()][current.getY()]+1;
                    if(newCost < costs[(int) neighbours[i].x][(int) neighbours[i].y]){
                        costs[(int) neighbours[i].x][(int) neighbours[i].y] = newCost;
                        AStarNode newNode = new AStarNode(current, neighbours[i], newCost + Tools.manhattanDistance(neighbours[i],targetPos));
                        nodesToVisit.add(newNode);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Assigns MovingNPCs paths depending on their goal (random position
     * or player). Will only update route if current journey is complete or
     * seeking the player.
     * @author Ben Andrew
     * @param mObjects ArrayList of all objects to extract moving NPCs from
     * @param mPlayer Player for assigning players position as a target
     */
    public void manageNPCNavigation(ArrayList<GameObject> mObjects, Player mPlayer){
        for(GameObject g : mObjects){
            if(g instanceof MovingNPC) {
                if(((MovingNPC) g).isPlayerSeekMode()){
                    ((MovingNPC) g).setPath(getPath(g.position, mPlayer.position), true);
                } else if (((MovingNPC) g).isTargetReached()){
                    Vector2 randomTarget = randomCellTarget();
                    while(!withinBounds(convertToCell(randomTarget))){
                        randomTarget = randomCellTarget();
                    }
                    Vector2[] steps = getPath(g.position, randomTarget);
                    ((MovingNPC) g).setPath(steps, false);
                }
            }
        }
    }

    /**
     * Produces a random vector position within confines
     * of the map
     * @author Ben Andrew
     * @return Vector2 random position within map dimensions
     */
    protected Vector2 randomCellTarget(){
        Vector2 randomPositon = new Vector2();
        randomPositon.x = random.nextInt(mapDimensions[0]);
        randomPositon.y = random.nextInt(mapDimensions[1]);
        return randomPositon;
    }

    /**
     * Updates AStarNavigation every UPDATE_RATE seconds
     * by checking and assigning paths to all moving NPC's
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime to decided whether another update is required
     * @param gameObjects ArrayList of gameObjects to pass to manageNPCNavigation
     * @param mPlayer Player to pass to manageNPCNavigation
     */
    public void update(ElapsedTime elapsedTime, ArrayList<GameObject> gameObjects, Player mPlayer){
        if(elapsedTime.totalTime - UPDATE_RATE > lastUpdate){
            manageNPCNavigation(gameObjects,mPlayer);
            lastUpdate = (int) elapsedTime.totalTime;
            GameEnvironment.log.addLog("AStarNavigation","Path updated");
        }
    }
}
