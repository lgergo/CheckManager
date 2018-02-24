package com.yevsp8.checkmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

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
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.menu_home:
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void saveToSharedPreferences(String key, String value) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    protected String getValueFromSharedPreferences(String key) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);

        return value;
    }
}
