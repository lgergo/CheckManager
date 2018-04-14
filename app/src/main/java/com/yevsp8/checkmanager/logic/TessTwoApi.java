package com.yevsp8.checkmanager.logic;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 02. 18..
 */

public class TessTwoApi {

    private final String TESS_DATA;
    private final String language;
    private final String requiredTraineddata;
    private final String tessError_load;
    private final String tessError_getUtf;
    @Inject
    TessBaseAPI tessBaseAPI;
    @Inject
    Context context;
    private String textResult;

    public TessTwoApi(Context context) {
        this.context = context;

        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .tessTwoModule(new TessTwoModule(context))
                .build();

        component.injectTessTwoApi(this);

        TESS_DATA = context.getString(R.string.tesseract_tessdata);
        language = context.getString(R.string.tesseract_lang);
        requiredTraineddata = language + context.getString(R.string.tesseract_dottraineddata);
        String TAG = context.getString(R.string.tesseract_errorTag);
        tessError_load = context.getString(R.string.tesseract_error_loadTesseract);
        tessError_getUtf = context.getString(R.string.tesseract_eroor_getUTFText);

    }

    void initialize() throws IOException {
        prepareTessData();
    }

    String startRecognition(Bitmap bitmap, String whiteList) throws RuntimeException {
        startOCR(bitmap, whiteList);

        return textResult;
    }

    private void prepareTessData() throws IOException {
        File dir = context.getExternalFilesDir(TESS_DATA);
        if (dir != null) {
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new IOException();
                }
            }
            String fileList[] = context.getAssets().list("");
            for (String fileName : fileList) {
                String pathToDataFile = dir + "/" + fileName;
                if (fileName.equals(requiredTraineddata) && !(new File(pathToDataFile)).exists()) {
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
        } else {
            throw new IOException();
        }
    }

    private void startOCR(Bitmap bitmap, String whiteList) throws RuntimeException {
        textResult = this.getText(bitmap, whiteList);
    }

    private String getText(Bitmap bitmap, String whiteList) {
        String retStr;
        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            retStr = tessError_load;
            return retStr;
        }
        String dataPath = context.getExternalFilesDir("/").getPath() + "/";
        tessBaseAPI.init(dataPath, language);
        if (whiteList != null) {
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
        }
        tessBaseAPI.setImage(bitmap);
        try {
            retStr = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            retStr = tessError_getUtf;
            return retStr;
        }
        tessBaseAPI.end();

        return retStr;
    }
}
