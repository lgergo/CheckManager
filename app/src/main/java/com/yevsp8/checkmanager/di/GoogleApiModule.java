package com.yevsp8.checkmanager.di;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Arrays;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module(includes = ContextModule.class)
public class GoogleApiModule {

    @Provides
    @CustomScope
    public com.google.api.services.sheets.v4.Sheets provideGoogleApi(HttpTransport httpTransport, JsonFactory jsonFactory, GoogleAccountCredential credential) {
        return new Sheets.Builder(httpTransport, jsonFactory, credential).build();
    }

    @Provides
    @CustomScope
    public HttpTransport provideHttpTransport() {
        return AndroidHttp.newCompatibleTransport();
    }

    @Provides
    @CustomScope
    public JsonFactory provideJsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    @Provides
    @CustomScope
    public GoogleAccountCredential provideCredential(Context context, /*String[] SCOPES,*/ ExponentialBackOff exponentialBackOff) {
        return GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE, SheetsScopes.DRIVE_FILE))
                .setBackOff(exponentialBackOff);
    }

    @Provides
    @CustomScope
    public ExponentialBackOff provideExponentialBackOff() {
        return new ExponentialBackOff();
    }

}
