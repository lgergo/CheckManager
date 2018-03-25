package com.yevsp8.checkmanager.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.yevsp8.checkmanager.CustomNotificationManager;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.data.Notification;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Converter;
import com.yevsp8.checkmanager.viewModel.NotificationListViewModel;

import java.util.Calendar;

import javax.inject.Inject;

public class CreateNotificationActivity extends AppCompatActivity {

    @Inject
    CustomNotificationManager notManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    NotificationListViewModel viewModel;
    private EditText companyName;
    private EditText message;
    private TextView fromDate;
    private TextView toDate;
    private DatePickerDialog.OnDateSetListener fromDateListener;
    private DatePickerDialog.OnDateSetListener toDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectCreateNotificationActivity(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationListViewModel.class);

        Button saveButton = findViewById(R.id.create_notification_saveButton);
        companyName = findViewById(R.id.company1_title);
        message = findViewById(R.id.company1_message);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotification();
            }
        });

        fromDate = findViewById(R.id.company1_fromDate);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateDialog(fromDateListener);
            }
        });
        fromDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = year + "/" + month + "/" + day;
                toDate.setText(date);
            }
        };
    }

    private void setDateDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                R.style.Theme_AppCompat_Light_Dialog_MinWidth,
                listener,
                year, month, day
        );
        dialog.show();
    }

    private void saveNotification() {
        if (companyName.getText().length() > 0) {
            Notification toInsert = new Notification(
                    companyName.getText().toString(),
                    message.getText().toString(),
                    Converter.stringDateToLong(fromDate.getText().toString()),
                    Converter.stringDateToLong(toDate.getText().toString())
            );
            viewModel.insertNotification(toInsert);
            notManager.createNotification(this, toInsert.getTitle(), toInsert.getMessage(), fromDate.getText().toString(), fromDate.getText().toString());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cím");
            builder.setMessage("A szöveg mezők nem lehetnek üresek!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }
}
