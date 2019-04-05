package uk.ac.qub.eeecs.game.gameWorld.worldObjects.NPC;

import android.graphics.Typeface;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.Animation;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.CollisionDetector;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Animated;
import uk.ac.qub.eeecs.game.gameWorld.worldObjects.Player;
import uk.ac.qub.eeecs.game.tools.Tools;

/**
 * <h1>Moving NPC</h1>
 * Class extending NPC to include movement
 * and animation. Allows NPCs to navigate the map with use
 * AStarNavigation.
 *
 * @author  Ben Andrew
 * @version 1.0
 */
public class MovingNPC extends NPC implements Animated {
    protected AnimationManager animationManager;
    private static final String[] DIRECTIONS = new String[]{"Right","Down","Left","Up"};
    //direction represents corresponding index of DIRECTIONS array
    //0 = Right, 1 = Down, 2 = Left, 3 = Up
    protected int currentDirection = 0;
    protected boolean playerSeekMode;
    private boolean targetReached = true;
    protected Vector2[] navigationPath;
    protected static final int PATH_FOLLOWING_TARGET_VARIANCE = 5;
    protected static final int MINIMUM_PLAYER_PROXIMITY = 100;
    protected int pathStep = 0;
    private int movementSpeed;

    /**
     * This constructor declares Moving NPC with text
     * settings for dialogs and animation details. Also states
     * whether NPC is currently seeking player.
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for width
     * @param height float for height
     * @param gameScreen GameScreen to draw
     * @param font TypeFace for dialog text font
     * @param fontSize int for dialog font size
     * @param charactersPerLine int for max characters
     * per line in dialogs
     * @param animationJson String of animation JSON
     * name
     * @param playerSeekMode boolean of whether NPC is
     * seeking player
     */
    public MovingNPC(float x, float y, float width, float height, GameScreen gameScreen, Typeface font, int fontSize,
                     int charactersPerLine, String animationJson, boolean playerSeekMode, int movementSpeed) {
        super(x, y, width, height, null, gameScreen, font, fontSize, charactersPerLine);
        this.animationManager = new AnimationManager(this);
        animationManager.addAnimation("txt/animation/"+animationJson+".JSON");
        this.playerSeekMode = playerSeekMode;
        this.movementSpeed = movementSpeed;
    }

    /**
     * This constructor declares Moving NPC with animation details.
     * Also states whether NPC is currently seeking player.
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for width
     * @param height float for height
     * @param gameScreen GameScreen to draw
     * @param animationJson String of animation JSON
     * name
     * @param playerSeekMode boolean of whether NPC is
     * seeking player
     */
    public MovingNPC(float x, float y, float width, float height, GameScreen gameScreen, String animationJson, boolean playerSeekMode, int movementSpeed) {
        super(x, y, width, height, null, gameScreen, null, 0, 0);
        this.animationManager = new AnimationManager(this);
        animationManager.addAnimation("txt/animation/"+animationJson+".JSON");
        this.playerSeekMode = playerSeekMode;
        this.movementSpeed = movementSpeed;
    }

    /**
     * This constructor declares Moving without animation for
     * testing purposes.
     * @author Ben Andrew
     * @param x float for x position
     * @param y float for y position
     * @param width float for width
     * @param height float for height
     * @param gameScreen GameScreen to draw
     * @param playerSeekMode boolean of whether NPC is
     * seeking player
     */
    public MovingNPC(float x, float y, float width, float height, GameScreen gameScreen, boolean playerSeekMode, int movementSpeed){
        super(x, y, width, height, null, gameScreen, null, 0, 0);
        this.playerSeekMode = playerSeekMode;
        this.movementSpeed = movementSpeed;
    }

    /**
     * Draws Moving NPC if on screen using animationManager.
     * Also draws current dialog (if one exists)
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime for animation/dialog draw methods
     * @param graphics2D IGraphics2D for animation/dialog draw methods
     * @param layerViewport LayerViewport for animation draw method
     * @param overlayViewport LayerViewport for dialog draw method
     * @param screenViewport ScreenViewport for animation/dialog draw methods
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, LayerViewport overlayViewport, ScreenViewport screenViewport){
        if(Tools.checkOnScreen(this.mBound,layerViewport)) {
            animationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
            if(getCurrentDialog() != null)
                getCurrentDialog().draw(elapsedTime, graphics2D, overlayViewport, screenViewport);
        }
    }

    /**
     * Updates Moving NPC animation and movement (if
     * not processing a dialog)
     * @author Ben Andrew
     * @param elapsedTime ElapsedTime for NPC & animation update methods
     * @param player Player for updating NPC dialoghandler (and NPC movement)
     * @param gameEnvironment GameEnvironment for updating NPC (not used
     * in moving NPC)
     */
    @Override
    public void update(ElapsedTime elapsedTime, Player player, GameEnvironment gameEnvironment){
        super.update(elapsedTime,player,gameEnvironment);
        animationManager.play(elapsedTime);
        animationManager.update(elapsedTime);
        if(getCurrentDialog().getHidden()){
            movementDecision(player);
            setDirection(currentDirection);
        }
    }

    /**
     * Overrides NPC onFirstCollision to also set
     * NPC facing direction towards player, turn off player
     * seeking and resolve collision when colliding with a player
     * @author Ben Andrew
     * @param obj GameObject to check whether its a player
     */
    @Override
    public boolean onFirstCollision(GameObject obj) {
        if(obj instanceof Player){
            if(getCurrentDialog() != null)
                getCurrentDialog().setHidden(false);
            setIdleDirection((Player) obj);
            invertDirection();
            playerSeekMode = false;
            //needed to avoid jamming player in inescapable positions
            CollisionDetector.determineAndResolveCollision(this, obj);
        }
        return true;
    }

    /**
     * Sets NPC idle position so that NPC animation
     * stops and they face the player
     * @author Ben Andrew
     * @param player Player to get players position
     * for setting direction
     */
    protected void setIdleDirection(Player player){
        if(Math.abs(player.position.y-super.position.y) > Math.abs(player.position.x-super.position.x)){
            if(player.position.y > super.position.y){
                animationManager.setCurrentAnimation("IdleUp");
            } else {
                animationManager.setCurrentAnimation("IdleDown");
            }
        } else {
            if(player.position.x > super.position.x){
                animationManager.setCurrentAnimation("IdleRight");
            } else {
                animationManager.setCurrentAnimation("IdleLeft");
            }
        }
    }

    /**
     * Sets Moving NPC direction and matches animation
     * accordingly
     * @author Ben Andrew
     * @param direction int for direction
     */
    protected void setDirection(int direction){
        this.currentDirection = direction;
        if(currentDirection >= 0)
            animationManager.setCurrentAnimation(DIRECTIONS[currentDirection]);
    }

    /**
     * Inverts direction (i.e. if right, now left)
     * @author Ben Andrew
     */
    protected void invertDirection(){
        currentDirection = currentDirection+2 % 4;
    }

    /**
     * Decides whether the NPC should be moving
     * or waiting on another path assignment from
     * AStarNavigation
     * @author Ben Andrew
     * @param player Player required for pathFollowing
     */
    private void movementDecision(Player player){
        if(navigationPath != null && !targetReached){
            pathFollowing(player);
        } else {
            standStill(player);
        }
    }

    /**
     * Updates Moving NPC position
     * depending on current direction
     * @author Ben Andrew
     */
    protected void move(){
        switch (currentDirection){
            case 0: super.position.x+=movementSpeed; break;
            case 1: super.position.y-=movementSpeed; break;
            case 2: super.position.x-=movementSpeed; break;
            case 3: super.position.y+=movementSpeed; break;
            default: break;
        }
    }

    /**
     * Sets NPC path and whether they are seeking the player.
     * Also resets pathStep and targetReached to track steps
     * during the journey through the path
     * @author Ben Andrew
     * @param navigationPath Vector2[] of all steps in path
     * @param playerSeek boolean the whether the NPC is seeking
     * player
     */
    public void setPath(Vector2[] navigationPath, boolean playerSeek){
        this.navigationPath = navigationPath;
        this.playerSeekMode = playerSeek;
        this.targetReached = false;
        this.pathStep = 0;
    }

    /**
     * Evaluates whether Moving NPC has reached its current
     * path step, and sets the next step if reached, or calls
     * goToLocation to continue moving towards current target.
     * @author Ben Andrew
     * @param player Player needed for goToLocation
     */
    protected void pathFollowing(Player player){
        try {
            Vector2 currentTarget = navigationPath[pathStep];
            int xDifference = Math.abs((int) position.x - (int) currentTarget.x);
            int yDifference = Math.abs((int) position.y - (int) currentTarget.y);
            if (xDifference < PATH_FOLLOWING_TARGET_VARIANCE && yDifference < PATH_FOLLOWING_TARGET_VARIANCE) {
                pathStep++;
                if (pathStep == navigationPath.length - 1) {
                    targetReached = true;
                }
            } else {
                goToLocation(currentTarget, xDifference, yDifference, player);
            }
        } catch (ArrayIndexOutOfBoundsException e){
            GameEnvironment.log.addLog("MovingNPC","Path failure");
            this.targetReached = true;
        }
    }

    /**
     * Moves NPC towards current target by moving in the direction
     * (horizontal/vertical) that the NPC is currently furthest from.
     * Also if not seeking the player, stops the NPC if they are too close to
     * the player so that they do not collide.
     * @author Ben Andrew
     * @param target Vector2 fof current target (step in path)
     * @param xDifference int of xDifference compared to target
     * @param yDifference int of yDifferencce compared to target
     * @param player Player needed to check whether NPC is too
     * close (only if not seeking the player)
     */
    private void goToLocation(Vector2 target, int xDifference, int yDifference, Player player){
        if(playerTooClose(player) && !playerSeekMode){
            standStill(player);
        } else {
            if(xDifference >= yDifference){
                if(target.x > position.x){
                    setDirection(0);
                } else {
                    setDirection(2);
                }
            } else {
                if(target.y > position.y){
                    setDirection(3);
                } else {
                    setDirection(1);
                }
            }
            move();
        }
    }

    /**
     * Sets the NPC to an idle position based on the players
     * current location (facing the player) and stops movement
     * (invalid movement direction turns off movement)
     * @author Ben Andrew
     * @param player Player needed to set idle direction
     * facing towards player
     */
    private void standStill(Player player){
        setIdleDirection(player);
        setDirection(-1);
    }

    /**
     * Uses manhattan distance to check if player is too close
     * (distance < MINIMUM_PLAYER_PROXIMITY)
     * @author Ben Andrew
     * @param player Player needed for player position
     * in distance measuring
     */
    protected boolean playerTooClose(Player player) {
        return Tools.manhattanDistance(player.position, position) < MINIMUM_PLAYER_PROXIMITY;
    }

    @Override
    public Animation getCurrentAnimation() {
        return animationManager.getCurrentAnimation();
    }

    @Override
    public int getCurrentFrame() {
        return animationManager.getCurrentAnimation().getCurrentFrame();
    }

    public boolean isPlayerSeekMode() {
        return playerSeekMode;
    }

    public boolean isTargetReached() {
        return targetReached;
    }
}
