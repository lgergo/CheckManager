package com.yevsp8.checkmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 03. 04..
 */

public class ImageProcessor {

    @Inject
    TessTwoApi tessTwoApi;
    String imagePath;
    Bitmap bitmap;
//    Mat src;
//    Mat dest;

    public ImageProcessor(Context context) {
        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .tessTwoModule(new TessTwoModule(context))
                .build();
        component.injectImageProcessor(this);

    }

    public String startImageProcess(String imagePath) {
        this.imagePath = imagePath;
        loadImage();
        //src = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);

        //TODO preprocess
        // adaptiveThreshold();

        return tessTwoApi.startRecognition(bitmap);
    }

    private void loadImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        //options.inSampleSize = 4;  //higher is smaller image
        bitmap = BitmapFactory.decodeFile(imagePath, options);
    }

//    private void adaptiveThreshold() {
//        Imgproc.adaptiveThreshold(src, dest, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 10, 2);
//    }

   /*
   * dinamikus lokális küszöbölés
   * zajszűrés
   * csekk alakzatának felismerése
   * elforgatás
   * felismerni kívánt terület meghatározása
   * */
}
