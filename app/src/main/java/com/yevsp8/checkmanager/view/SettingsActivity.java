package com.yevsp8.checkmanager.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yevsp8.checkmanager.R;

import java.util.Calendar;

public class SettingsActivity extends BaseActivity {

    private TextView textview_sheetId;
    private EditText edittext_sheetId;
    private Button button_save;
    private Button button_test;

    private TextView fromDate;
    private TextView toDate;
    private DatePickerDialog.OnDateSetListener fromDateListener;
    private DatePickerDialog.OnDateSetListener toDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        fromDate = findViewById(R.id.company1_fromDate);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateDialog(fromDateListener);
            }
        });
        fromDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int day, int month) {
                String date = year + "/" + month + "/" + day;
                fromDate.setText(date);
            }
        };

        toDate = findViewById(R.id.company1_toDate);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateDialog(toDateListener);
            }
        });
        toDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int day, int month) {
                String date = year + "/" + month + "/" + day;
                toDate.setText(date);
            }
        };

        textview_sheetId = findViewById(R.id.textView_sheetId);
        textview_sheetId.setText("Sheets ID: ");
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

            }
        });


        //TODO link hogy honann szerezheti meg a sheet id-t
    }

    private void setDateDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                SettingsActivity.this,
                R.style.Theme_AppCompat_Light_Dialog_MinWidth,
                listener,
                year, month, day
        );
        dialog.show();
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
