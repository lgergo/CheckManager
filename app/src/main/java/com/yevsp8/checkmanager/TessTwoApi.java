package com.yevsp8.checkmanager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Gergo on 2018. 02. 18..
 */

public class TessTwoApi {

    private static TessTwoApi tessTwoApi;
    private Bitmap image;
    private TessBaseAPI tesseract;
    private String dataPath = "";
    private String language = "en";
    private Context context;

    private TessTwoApi(Context context) {
        this.context = context;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ocr);

        dataPath = context.getFilesDir() + "/tesseract/";

        checkFile(new File(dataPath + "tessdata/"));
        tesseract = new TessBaseAPI();
        tesseract.init(dataPath, language);
    }

    public static TessTwoApi getInstance(Context context) {
        if (tessTwoApi == null)
            tessTwoApi = new TessTwoApi(context);
        return tessTwoApi;
    }

    //TODO miert kell ??
    //file eszkozre masolasa
    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = dataPath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = context.getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = dataPath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }
}
