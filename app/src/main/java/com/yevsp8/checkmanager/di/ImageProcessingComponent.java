package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.ImageProcessor;
import com.yevsp8.checkmanager.TessTwoApi;
import com.yevsp8.checkmanager.view.RecognisedCheckActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 03. 02..
 */

@CustomScope
@Component(modules = {TessTwoModule.class})
public interface ImageProcessingComponent {

    void injectTessTwoApi(TessTwoApi tessTwoApi);

    void injectRecognisedCheckActivity(RecognisedCheckActivity recognisedCheckActivity);

    void injectImageProcessor(ImageProcessor imageProcessor);
}
