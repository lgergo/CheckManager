package com.yevsp8.checkmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.inject.Inject;


/**
 * Created by Gergo on 2018. 03. 04..
 */

public class ImageProcessor {

    @Inject
    TessTwoApi tessTwoApi;
    String imagePath;
    Bitmap bitmap;
    Mat src;
    Mat dest;

    public ImageProcessor(Context context) {
        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .tessTwoModule(new TessTwoModule(context))
                .build();
        component.injectImageProcessor(this);

        try {
            OpenCVLoader.initDebug();
        } catch (Exception ex) {
            Log.e("e", ex.getMessage());
        }
    }

    public String startImageProcess(String imagePath) {
        this.imagePath = imagePath;

        loadImage();

        src = new Mat();
        //src=new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);

        //Imgproc.cvtColor(src, dest, Imgproc.COLOR_BayerBG2GRAY);
        //adaptiveThreshold();

        Utils.bitmapToMat(bitmap, src);
        Utils.matToBitmap(src, bitmap);
        return tessTwoApi.startRecognition(bitmap);
        //return tessTwoApi.startRecognitionWithByteArray(Mat.,src.width(),src.height(),src.channels(),(int)src.step1());
    }

    private void loadImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        //TODO for demo data only
        if (imagePath.contains("JPEG_")) {
            options.inSampleSize = 8;  //higher is smaller image
        }
        bitmap = BitmapFactory.decodeFile(imagePath, options);
    }

    private void adaptiveThreshold() {
        Imgproc.adaptiveThreshold(src, dest, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 5, 5);
    }


   /*
   * dinamikus lokális küszöbölés
   * zajszűrés
   * csekk alakzatának felismerése
   * elforgatás
   * felismerni kívánt terület meghatározása
   * */
}
