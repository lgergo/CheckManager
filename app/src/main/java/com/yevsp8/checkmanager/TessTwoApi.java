package com.yevsp8.checkmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 02. 18..
 */

public class TessTwoApi {

    public static final String TESS_DATA = "/tessdata";
    @Inject
    TessBaseAPI tessBaseAPI;
    @Inject
    Context context;
    private String language = "eng";
    private String imagePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/ocr.png";
    private String textResult;
    private String TAG = "Tesseract error";

    public TessTwoApi(Context context) {
        this.context = context;

        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .tessTwoModule(new TessTwoModule(context))
                .build();

        component.injectTessTwoApi(this);

    }

    public String startRecognition(Bitmap bitmap) {

        prepareTessData();
        startOCR(bitmap);
        return textResult;
    }

    public String startRecognitionWithByteArray(byte[] imagedata, int width, int height, int bytes_per_pixel, int bytes_per_line) {

        prepareTessData();
        startOCRWithByteArray(imagedata, width, height, bytes_per_pixel, bytes_per_line);
        return textResult;
    }

    private void prepareTessData() {
        try {
            File dir = context.getExternalFilesDir(TESS_DATA);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    //TODO logika ne toastoljon
                    Toast.makeText(context.getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }

            String fileList[] = context.getAssets().list("");
            for (String fileName : fileList) {
                String pathToDataFile = dir + "/" + fileName;
                if ((fileName.equals("eng.traineddata") || fileName.equals("hu.traineddata")) && !(new File(pathToDataFile)).exists()) {
                    InputStream in = context.getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startOCR(Bitmap bitmap) {
        try {
            textResult = this.getText(bitmap);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void startOCRWithByteArray(byte[] imagedata, int width, int height, int bytes_per_pixel, int bytes_per_line) {
        try {
            textResult = this.getTextWithByteArray(imagedata, width, height, bytes_per_pixel, bytes_per_line);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private String getText(Bitmap bitmap) {
        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        String dataPath = context.getExternalFilesDir("/").getPath() + "/";
        tessBaseAPI.init(dataPath, language);
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try {
            Log.e("--------------", "STARTING RECOGNITION");
            retStr = tessBaseAPI.getUTF8Text();
            Log.e("--------------", "RECOGNITION ENDED");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    private String getTextWithByteArray(byte[] imagedata, int width, int height, int bytes_per_pixel, int bytes_per_line) {
        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        String dataPath = context.getExternalFilesDir("/").getPath() + "/";
        tessBaseAPI.init(dataPath, language);
        tessBaseAPI.setImage(imagedata, width, height, bytes_per_pixel, bytes_per_line);
        String retStr = "No result";
        try {
            Log.e("--------------", "STARTING RECOGNITION");
            retStr = tessBaseAPI.getUTF8Text();
            Log.e("--------------", "RECOGNITION ENDED");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }
}
