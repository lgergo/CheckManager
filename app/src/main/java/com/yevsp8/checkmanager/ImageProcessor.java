package com.yevsp8.checkmanager;

import android.content.Context;

import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 03. 04..
 */

public class ImageProcessor {

    @Inject
    TessTwoApi tessTwoApi;
    String imagePath;

    public ImageProcessor(Context context) {
        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .build();

        component.injectImageProcessing(this);
    }

    public String startImageProcess(String imagePath) {
        this.imagePath = imagePath;

        //TODO preprocess

        return tessTwoApi.startRegognition(imagePath);
    }

   /*
   * dinamikus lokális küszöbölés
   * zajszűrés
   * csekk alakzatának felismerése
   * elforgatás
   * felismerni kívánt terület meghatározása
   * */
}
