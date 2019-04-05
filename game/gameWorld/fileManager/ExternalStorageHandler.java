package uk.ac.qub.eeecs.game.gameWorld.fileManager;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.qub.eeecs.game.gameWorld.GameEnvironment;

/**
 * <h1>External Storage Handler</h1>
 * Manages interaction with external device storage
 * for reading/writing game saves
 * Resource used; https://stackoverflow.com/questions/29790578/android-copy-file-from-internal-storage-to-external
 * @author  Ben Andrew
 * @version 1.0
 */
public class ExternalStorageHandler {
    //context required to get external access to device's storage
    private Context context;
    private static final int BYTE_BUFFER_SIZE = 1024;

    /**
     * This constructor declares ExternalStorageHandler with context
     * for accessing device storage
     * @author Ben Andrew
     * @param context Context needed to access devices storage
     */
    public ExternalStorageHandler(Context context){
        this.context = context;
    }

    /**
     * Copies file to devices storage if not already found.
     * @author Ben Andrew
     * @param filename String of filename to be copied
     */
    public void copyAsset(String filename) {
        AssetManager assetManager = context.getAssets();
        boolean found = searchExternalStorage(filename);
        if (!found) {
            try {
                //inputstream from file in text assets to storage
                InputStream in = assetManager.open("txt/assets/" + filename);
                writeFileToStorage(in,filename);
                in.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            if(searchExternalStorage(filename)){
                GameEnvironment.log.addLog("ESH","File: "+filename+" added to storage");
            } else {
                GameEnvironment.log.addLog("ESH","File: "+filename+" failed to copy");
            }
        }
    }

    /**
     * Fetches file from external storage
     * @param filename String of filename to be fetched
     * @author Ben Andrew
     * @return File file from storage (or null if not found)
     */
    public File getFileFromExternalStorage(String filename){
        if(searchExternalStorage(filename)){
            //fetch file from device root directory
            return new File(context.getExternalFilesDir(null).getPath(), filename);
        } else {
            GameEnvironment.log.addLog("ESH","File not found: "+filename);
        }
        return null;
    }

    /**
     * Returns whether or not the file exists in device storage
     * @author Ben Andrew
     * @param filename String of filename to be searched for
     * @return boolean to whether it was found
     */
    public boolean searchExternalStorage(String filename){
        //fetches all files names in device's storage directory
        String[] external = context.getExternalFilesDir(null).list();
        for (String file : external) {
            if (file.equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes passed input stream to file on fileName
     * is external storage
     * @param inputStream InputStream of file data
     * @param fileName String of file name
     */
    public void writeFileToStorage(InputStream inputStream, String fileName){
        File file = new File(context.getExternalFilesDir(null), fileName);
        OutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(file);
            copyFile(inputStream, outputStream);
            GameEnvironment.log.addLog("ESH","File copied to device storage");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Uses byte buffer to write input stream data to
     * output stream file
     * @author Ben Andrew
     * @param inputStream InputStream of incoming file data (reading)
     * @param outputStream OutputStream of outgoing file data (writing)
     */
    public void copyFile(InputStream inputStream, OutputStream outputStream) {
        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
        int read;
        try {
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
