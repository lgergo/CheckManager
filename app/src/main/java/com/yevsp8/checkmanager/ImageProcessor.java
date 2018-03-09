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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

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

    public Bitmap preProcessing(Bitmap rawBitmap) {
        Log.e("Preprocess", "PREPROCESSING STARTED");

        bitmap = rawBitmap;

        src = new Mat(rawBitmap.getHeight(), rawBitmap.getWidth(), CvType.CV_8UC1);
        dest = new Mat(rawBitmap.getHeight(), rawBitmap.getWidth(), CvType.CV_8UC1);

        Utils.bitmapToMat(rawBitmap, src);
        Imgproc.cvtColor(src, dest, Imgproc.COLOR_BGR2GRAY);
        src = dest;
        //Imgproc.medianBlur(src,dest,7);
        Photo.fastNlMeansDenoising(src, dest);
        src = dest;
        //adaptiveThreshold(src,dest);
        //canny();
        Utils.matToBitmap(dest, bitmap);

        Log.e("Preprocess", "PREPROCESSING ENDED");

        return bitmap;
    }

    public String recognition(Bitmap rawBitmap) {

        //TODO külön szálon fusson
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

    private void adaptiveThreshold(Mat source, Mat destination) {
        Imgproc.adaptiveThreshold(source, destination, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
    }

    private void canny() {
        int lowThreshold = 3;
        int kernelSize = 3;
        Imgproc.Canny(src, dest, lowThreshold, lowThreshold * 2, kernelSize, false);
    }

   /*
   * dinamikus lokális küszöbölés
   * zajszűrés
   * csekk alakzatának felismerése
   * elforgatás
   * felismerni kívánt terület meghatározása
   * */
}
