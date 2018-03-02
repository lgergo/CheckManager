package com.yevsp8.checkmanager.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {

    @Inject
    protected Context context;
    @Inject
    SharedPreferences sharedPreferences;

    public void addFragmentToActivity(FragmentManager manager, Fragment fragment, int frameId, String frameTag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(frameId, fragment, frameTag);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectBaseActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_synch:

                break;
            case R.id.menu_settings:
                Intent settings = new Intent(context, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.menu_home:
                Intent home = new Intent(context, MainActivity.class);
                startActivity(home);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void saveToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    protected String getValueFromSharedPreferences(String key) {
        String value = sharedPreferences.getString(key, null);

        return value;
    }
}