package com.yevsp8.checkmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;

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
    private String language = "en";
    private String imagePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/ocr.png";
    private String textResult;
    private String TAG = "Tesseract error";
    private String imageToRecognisePath;

    public TessTwoApi(Context context) {
        this.context = context;

        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .build();

        component.injectTessTwoApi(this);
    }

    public String startRegognition(String imagePath) {
        imageToRecognisePath = imagePath;
        prepareTessData();
        startOCR();
        return getDataFromImage();
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
                if (!(new File(pathToDataFile)).exists()) {
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

    public String getDataFromImage() {
        return textResult;
    }

    private void startOCR() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 6;
            Bitmap bitmap = BitmapFactory.decodeFile(imageToRecognisePath, options);
            textResult = this.getText(bitmap);
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
            retStr = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }
}
