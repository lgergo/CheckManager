package com.yevsp8.checkmanager.di;

import android.content.Context;

import com.googlecode.tesseract.android.TessBaseAPI;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module(includes = ContextModule.class)
public class TessTwoModule {

    @Provides
    TessBaseAPI provideTessBaseApi(Context context) {
        return new TessBaseAPI();
    }
}
