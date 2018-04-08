package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.logic.ImageProcessor;
import com.yevsp8.checkmanager.logic.TessTwoApi;
import com.yevsp8.checkmanager.view.NewImageActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 03. 02..
 */

@CustomScope
@Component(modules = {TessTwoModule.class})
public interface ImageProcessingComponent {

    void injectTessTwoApi(TessTwoApi tessTwoApi);

    void injectImageProcessor(ImageProcessor imageProcessor);

    void injectNewImageActivtiy(NewImageActivity newImageActivity);
}
