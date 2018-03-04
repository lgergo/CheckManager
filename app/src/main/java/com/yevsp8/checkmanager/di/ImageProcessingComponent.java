package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.ImageProcessor;
import com.yevsp8.checkmanager.TessTwoApi;
import com.yevsp8.checkmanager.view.RecognisedCheckActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 03. 02..
 */

@CustomScope
@Component(modules = {TessTwoModule.class, DatabaseModule.class})  //database for saving result
public interface ImageProcessingComponent {

    void injectTessTwoApi(TessTwoApi tessTwoApi);

    void injectRecognisedCheckActivity(RecognisedCheckActivity recognisedCheckActivity);

    void injectImageProcessing(ImageProcessor imageProcessor);
}
