package com.yevsp8.checkmanager.di;

import android.content.Context;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.yevsp8.checkmanager.ImageProcessor;
import com.yevsp8.checkmanager.TessTwoApi;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module(includes = ContextModule.class)
public class TessTwoModule {

    public final Context context;

    public TessTwoModule(Context context) {
        this.context = context;
    }

    @Provides
    @CustomScope
    ImageProcessor provideImageProcessing() {
        return new ImageProcessor(context);
    }

    @Provides
    @CustomScope
    TessTwoApi provideTessTwoApi() {
        return new TessTwoApi(context);
    }

    @Provides
    @CustomScope
    TessBaseAPI provideTessBaseApi() {
        return new TessBaseAPI();
    }
}
