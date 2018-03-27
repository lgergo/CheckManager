package com.yevsp8.checkmanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yevsp8.checkmanager.CustomNotificationManager;
import com.yevsp8.checkmanager.GoogleApiActivity;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Enums;

import javax.inject.Inject;


public class SettingsActivity extends BaseActivity {

    @Inject
    CustomNotificationManager notManager;
    private TextView textview_sheetId;
    private EditText edittext_sheetId;
    private Button button_save;
    private Button button_test;
    private SeekBar seekBar;
    private TextView seekBarValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectSettingsActivity(this);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

//        FragmentManager manager = getSupportFragmentManager();
//        Fragment fragment = new NotificationListFragment();
//        replaceFragmentToActivity(manager, fragment, R.id.notificationlist_fragmentcontainer);

        textview_sheetId = findViewById(R.id.textView_sheetId);
        edittext_sheetId = findViewById(R.id.editText_settings_sheetId);
        edittext_sheetId.setText(getValueFromSharedPreferences(R.string.sheetId_value, R.string.sheetId_default), TextView.BufferType.EDITABLE);
        if (edittext_sheetId.getText().length() == 0) {
            edittext_sheetId.setHint("Ide írja a Sheets ID azonosítóját...");
        }

        button_save = findViewById(R.id.button_settings_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToSharedPreferences(R.string.sheetId_value, edittext_sheetId.getText().toString());
                saveToSharedPreferences(R.string.notification_interval_value, String.valueOf(seekBar.getProgress()));
                Toast.makeText(getApplicationContext(), "Sikeres mentés.", Toast.LENGTH_SHORT).show();
            }
        });
        button_test = findViewById(R.id.button_settings_test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testButtonClicked();
            }
        });

        int seekBarValueFromPrefernces = Integer.parseInt(getValueFromSharedPreferences(R.string.notification_interval_value, R.string.notification_interval_default));
        seekBar = findViewById(R.id.seekBar_settings);
        seekBar.setMax(30);
        seekBar.setProgress(seekBarValueFromPrefernces);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressValue = i;
                seekBarValue.setText(i + " naponta szeretnék értesítést.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarValue.setText(progressValue + " naponta szeretnék értesítést.");
            }
        });
        seekBarValue = findViewById(R.id.seekBar_value);
        seekBarValue.setText(seekBar.getProgress() + " naponta szeretnék értesítést.");
        //TODO link hogy honann szerezheti meg a sheet id-t
    }

    private void testButtonClicked() {
        saveToSharedPreferences(R.string.sheetId_value, edittext_sheetId.getText().toString());
        Intent intent = new Intent(this, GoogleApiActivity.class);
        intent.putExtra("callType", Enums.APICallType.ConnectionTest);
        startActivity(intent);
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
