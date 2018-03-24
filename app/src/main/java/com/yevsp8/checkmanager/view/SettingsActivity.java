package com.yevsp8.checkmanager.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yevsp8.checkmanager.NotificationListFragment;
import com.yevsp8.checkmanager.R;


public class SettingsActivity extends BaseActivity {

    private TextView textview_sheetId;
    private EditText edittext_sheetId;
    private Button button_save;
    private Button button_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = new NotificationListFragment();
        replaceFragmentToActivity(manager, fragment, R.id.notificationlist_fragmentcontainer);

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
                Toast.makeText(getApplicationContext(), "Sikeres mentés.", Toast.LENGTH_SHORT).show();
            }
        });
        button_test = findViewById(R.id.button_settings_test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO test bekötése
            }
        });


        //TODO link hogy honann szerezheti meg a sheet id-t
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
