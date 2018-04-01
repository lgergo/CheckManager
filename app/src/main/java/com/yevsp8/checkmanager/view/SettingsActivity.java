package com.yevsp8.checkmanager.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Constants;
import com.yevsp8.checkmanager.util.Enums;

import javax.inject.Inject;


public class SettingsActivity extends BaseActivity {

    @Inject
    CustomNotificationManager notManager;
    private EditText edittext_sheetId;
    private SeekBar seekBar;
    private TextView seekBarValue;
    private int notificationInterval;
    private String sheetId;

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

        sheetId = getValueFromSharedPreferences(R.string.sheetId_value, R.string.sheetId_default);
        notificationInterval = Integer.parseInt(getValueFromSharedPreferences(R.string.notification_interval_value, R.string.notification_interval_default));


        edittext_sheetId = findViewById(R.id.editText_settings_sheetId);
        edittext_sheetId.setText(sheetId, TextView.BufferType.EDITABLE);
        if (edittext_sheetId.getText().length() == 0) {
            edittext_sheetId.setHint("Ide írja a Sheets ID azonosítóját...");
        }

        Button button_save = findViewById(R.id.button_settings_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonClicked();
            }
        });
        Button button_test = findViewById(R.id.button_settings_test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testButtonClicked();
            }
        });
        Button button_create = findViewById(R.id.button_settings_create);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createButtonClickeed();
            }
        });

        seekBar = findViewById(R.id.seekBar_settings);
        seekBar.setMax(Constants.Max_Notification_Day_Interval);
        seekBar.setProgress(notificationInterval);
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
        if (edittext_sheetId.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.settings_empty_sheetId_toast, Toast.LENGTH_SHORT).show();
        } else {
            saveToSharedPreferences(R.string.sheetId_value, edittext_sheetId.getText().toString());
            Intent intent = new Intent(this, GoogleApiActivity.class);
            intent.putExtra("callType", Enums.APICallType.ConnectionTest);
            startActivity(intent);
        }
    }

    private void createButtonClickeed() {
        if (edittext_sheetId.getText().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Táblázat létrehozás");
            builder.setMessage("Az azonosító mező nem üres. Biztos hogy létre akar hozni egy új táblázatot?");
            builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), GoogleApiActivity.class);
                    intent.putExtra("callType", Enums.APICallType.CreateSpreadSheet);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else {
            Intent intent = new Intent(getApplicationContext(), GoogleApiActivity.class);
            intent.putExtra("callType", Enums.APICallType.CreateSpreadSheet);
            startActivity(intent);
        }
    }

    private void saveButtonClicked() {
        if (!sheetId.equals(edittext_sheetId.getText().toString())) {
            saveToSharedPreferences(R.string.sheetId_value, edittext_sheetId.getText().toString());
        }
        if (notificationInterval != seekBar.getProgress()) {
            saveToSharedPreferences(R.string.notification_interval_value, String.valueOf(seekBar.getProgress()));
            notManager.deleteNotification(this);
            if (notificationInterval != 0) {
                notManager.createNotification(this, notificationInterval);
            }
        }
        Toast.makeText(getApplicationContext(), R.string.successful_save, Toast.LENGTH_SHORT).show();
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
