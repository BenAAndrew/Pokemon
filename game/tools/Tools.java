package uk.ac.qub.eeecs.game.tools;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.LayerViewport;

/**
* <h1>Tools</h1>
* Class for centralising general purpose methods used in two or more methods.
* Any method which is used in two or more methods with similar implementations
* should be moved here for maintenance purposes.
*
* @author  Ben Andrew
* @version 1.0
*/
public class Tools {

    /**
     * Converts a passed String into a String[]
     * of lines where each line is less than characterPerLine
     * in length. String will maintain its order but may lose
     * words if individual word is longer than characterPerLine
     * in length, or message is longer than what can fit into
     * line division method (limited by lines).
     * @author Ben Andrew
     * @param message String for message to be split into lines
     * @param lines int for max number of lines (set to -1 if no limit)
     * @param charactersPerLine int for max characters per lines
     * @return String[] of message divided into lines
     */
    public static String[] convertStringToMultiLine(String message, int lines, int charactersPerLine){
        ArrayList<String> linesArrayList = new ArrayList<String>();
        String[] words = message.split(" ");
        String currentLine = "";
        for(String word : words){
            if(currentLine.length()+word.length()+1 < charactersPerLine){
                if(currentLine.isEmpty()) {
                    currentLine = word;
                }
                else {
                    currentLine += " " + word;
                }
            } else if(word.length() < charactersPerLine){
                if(lines != -1 && linesArrayList.size() == lines-1){
                    break;
                }
                linesArrayList.add(currentLine);
                currentLine = word;
            }
        }
        linesArrayList.add(currentLine);
        String[] linesArray = new String[linesArrayList.size()];
        for(int i = 0; i < linesArray.length; i++){
            linesArray[i] = linesArrayList.get(i);
        }
        return linesArray;
    }


    /**
     * Manhattan distance calculation for measuring distance between
     * two vector positions
     * @author Ben Andrew
     * @param a Vector2 of a position
     * @param b Vector2 of b position
     * @return int of distance from a to b
     */
    public static int manhattanDistance(Vector2 a, Vector2 b){
        return (int)(Math.abs(a.x-b.x) + Math.abs(a.y-b.y));
    }

    /**
     * returns whether a given bounding box is currently on screen
     * @author Ben Andrew
     * @return int of distance from a to b
     */
    public static boolean checkOnScreen(BoundingBox object, LayerViewport screen){
        return  object.x-object.getWidth()/2 < screen.getRight() && object.x+object.getWidth()/2 > screen.getLeft() &&
                object.y+object.getHeight()/2 > screen.getBottom() && object.y-object.getHeight()/2 < screen.getTop();
    }
}
