package com.yevsp8.checkmanager.di;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module
public class GoogleApiModule {

    Sheets.Builder provide

    GoogleapiService(HttpTransport transport, JsonFactory jsonFactory, GoogleAccountCredential credential) {

        return new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Sheets API Create Sheet")
                .build();
    }

    @Provides
    GoogleAccountCredential provideGoogleApiCredentials() {

    }

    @Provides

    @Provides
    HttpTransport provideHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    @Provides
    JsonFactory provideJsonFactory() {
        return new JacksonFactory.getDefaultInstance();
    }

}
