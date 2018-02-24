package com.yevsp8.checkmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {

    private TextView textview_sheetId;
    private EditText edittext_sheetId;
    private Button button_save;
    private Button button_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textview_sheetId = findViewById(R.id.textView_sheetId);
        textview_sheetId.setText("Insert your google sheet ID:");
        edittext_sheetId = findViewById(R.id.editText_settings_sheetId);
        edittext_sheetId.setText(getValueFromSharedPreferences(getString(R.string.sheetId)), TextView.BufferType.EDITABLE);
        if (edittext_sheetId.getText().toString() == "") {
            edittext_sheetId.setHint("Fill with your sheetId to able to synchronise");
        }

        button_save = findViewById(R.id.button_settings_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToSharedPreferences(getString(R.string.sheetId), edittext_sheetId.getText().toString());
                Toast.makeText(getApplicationContext(), "Preferences are saved.", Toast.LENGTH_SHORT).show();
            }
        });
        button_test = findViewById(R.id.button_settings_test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //TODO test connection to sheet + első induláskor állítsa be pop up window + link hogy honann szerezheti meg
    }


}
