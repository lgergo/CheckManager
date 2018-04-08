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
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.logic.CustomNotificationManager;
import com.yevsp8.checkmanager.util.Constants;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    CustomNotificationManager notManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectMainActivity(this);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        CheckListFragment fragment = new CheckListFragment();
        replaceFragmentToActivity(manager, fragment, R.id.checklist_fragmentcontainer);

        TextView latestSyncTextView = findViewById(R.id.latest_synch);
        String lastSync = getValueFromSharedPreferences(R.string.last_sync_value, R.string.last_sync_default);
        latestSyncTextView.setText(getString(R.string.main_latestSync, lastSync));

        FloatingActionButton newImageButton = findViewById(R.id.newImage_button);
        newImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewImageActivity.class);
                startActivity(intent);
            }
        });

        String firstStart = getValueFromSharedPreferences(R.string.first_start_value, R.string.first_start_default);
        if (firstStart.equals(Constants.FirstStart_Default)) {
            showFirstStartAlertDialog();
        }
    }

    private void showFirstStartAlertDialog() {
        saveToSharedPreferences(R.string.first_start_value, Constants.FirstStart_Value);
        int notInterval = Integer.parseInt(getValueFromSharedPreferences(R.string.notification_interval_value, R.string.notification_interval_default));
        notManager.createNotification(this, notInterval);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.firstStartMessage_title);
        builder.setMessage(R.string.firstStartMessage_message);
        builder.setNegativeButton(R.string.button_notNow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast t = Toast.makeText(getApplicationContext(), R.string.firstStartMessage_toast, Toast.LENGTH_SHORT);
                t.show();
            }
        });
        builder.setPositiveButton(R.string.button_fill, new DialogInterface.OnClickListener() {
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
