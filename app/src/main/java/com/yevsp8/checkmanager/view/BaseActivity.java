package com.yevsp8.checkmanager.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Constants;

import java.io.File;


public abstract class BaseActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    void replaceFragmentToActivity(FragmentManager manager, Fragment fragment, int frameId) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    void removeFragmentFromActivity(FragmentManager manager, Fragment fragment) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
        sharedPref = getSharedPreferences(Constants.SharedPreferencesName, MODE_PRIVATE);
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
            case R.id.menu_images:
                File storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (storage != null) {
                    for (File c : storage.listFiles()) {
                        c.delete();
                    }
                    Toast t = Toast.makeText(getApplicationContext(), R.string.successful_delete, Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
            case R.id.menu_help:
                Intent help = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(help);
                break;
            case R.id.menu_settings:
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.menu_home:
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void saveToSharedPreferences(int key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(key), value);
        editor.apply();
    }

    String getValueFromSharedPreferences(int key, int defaultValue) {
        return sharedPref.getString(getString(key), getString(defaultValue));
    }
}
