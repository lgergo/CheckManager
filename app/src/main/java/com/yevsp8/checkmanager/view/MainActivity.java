package com.yevsp8.checkmanager.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yevsp8.checkmanager.R;

public class MainActivity extends BaseActivity {

    private CheckListFragment fragment;
    private TextView latestSyncTextView;
    private FloatingActionButton newImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        fragment = new CheckListFragment();
        replaceFragmentToActivity(manager, fragment, R.id.checklist_fragmentcontainer);

        latestSyncTextView = findViewById(R.id.latest_synch);
        String lastSync = getValueFromSharedPreferences(R.string.last_sync_value, R.string.last_sync_default);
        latestSyncTextView.setText("Legutoljára szinkronizálva: " + lastSync);

        newImageButton = findViewById(R.id.newImage_button);
        newImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewImageActivity.class);
                startActivity(intent);
            }
        });

        String val = getValueFromSharedPreferences(R.string.first_start_value, R.string.first_start_default);
        if (val.equals("1")) {
            showFirstStartAlertDialog();
        }
    }

    private void showFirstStartAlertDialog() {
        saveToSharedPreferences(R.string.first_start_value, "0");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CheckManager");
        builder.setMessage("Hogy ki tudd használni a Google Sheets szinkronizációt, kérlek add meg a dokumentum azonosítóját.");
        builder.setNegativeButton("Most nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast t = Toast.makeText(getApplicationContext(), "Később is lehetőséged lesz megadni a Beállítások menüpont alatt.", Toast.LENGTH_SHORT);
                t.show();
            }
        });
        builder.setPositiveButton("Megadom", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
