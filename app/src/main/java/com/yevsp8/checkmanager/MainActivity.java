package com.yevsp8.checkmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    DbHandler db;
    ListCheckFragment fragment;
    TextView latestSynchTextView;
    FloatingActionButton newImageButton;
    FloatingActionButton testApiButton;
    TextView googleApiResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DbHandler.getInstance(this);
        db.generateDemoData();

        fragment = ListCheckFragment.newInsatce();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.checklist_fragmentcontainer, fragment);
        transaction.commit();

        latestSynchTextView = findViewById(R.id.latest_synch);
        //TODO resource-ba + lekérdezni az utolsó szinkronizációt
        latestSynchTextView.setText("Legutoljára szinkronizálva: ");

        newImageButton = findViewById(R.id.newImage_button);
        newImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewImageActivity.class);
                startActivity(intent);
            }
        });

        googleApiResultTextView = findViewById(R.id.googleApiResult_textView);

        testApiButton = findViewById(R.id.testGoogleApi_button);
        testApiButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                GoogleApiProvider googleApi=GoogleApiProvider.getInstance(MainActivity.this);
//                googleApi.getResultsFromApi();

                GoogleApiProvider googleApi = GoogleApiProvider.getInstance(MainActivity.this);
                googleApi.updateData("010101", "11300", "Valaki", "2017.01.01");

//                Intent intent = new Intent(getApplicationContext(), GoogleApiActivity.class);
//                startActivity(intent);
            }
        });
    }

    public void updateGoogleApiTextView(String result) {
        googleApiResultTextView.setText(result);
    }
}
