package com.yevsp8.checkmanager.di;

import android.content.Context;

import com.yevsp8.checkmanager.util.Converter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @CustomScope
    Context provideContext() {
        return context;
    }

    @Provides
    @CustomScope
    Converter provideConverter() {
        return new Converter();
    }
}

