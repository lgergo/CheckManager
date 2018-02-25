package com.yevsp8.checkmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.yevsp8.checkmanager.data.DbHandler;

public class MainActivity extends BaseActivity {

    DbHandler db;
    ListCheckFragment fragment;
    TextView latestSynchTextView;
    FloatingActionButton newImageButton;
    FloatingActionButton testApiButton;
    TextView googleApiResultTextView;
    ProgressDialog updateProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DbHandler.getInstance(this);
        db.generateDemoData();

        FragmentManager manager = getSupportFragmentManager();
        fragment = (ListCheckFragment) manager.findFragmentById(R.id.checklist_fragmentcontainer);
        if (fragment == null) {
            fragment = ListCheckFragment.newInsatce();
        }

        addFragmentToActivity(manager, fragment, R.id.checklist_fragmentcontainer, "tag");
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.checklist_fragmentcontainer, fragment);
//        transaction.commit();

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

        updateProgressBar = new ProgressDialog(this);
        updateProgressBar.setMessage("Updating cell values ...");

        testApiButton = findViewById(R.id.testGoogleApi_button);
        testApiButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                GoogleApiProvider googleApi=GoogleApiProvider.getInstance(MainActivity.this);
//                googleApi.getResultsFromApi();

//                GoogleApiProvider googleApi = GoogleApiProvider.getInstance(MainActivity.this);
//                googleApi.insertData("010101", "11300", "Valaki", "2017.01.01");

//                GoogleApiProvider googleApi = GoogleApiProvider.getInstance(MainActivity.this);
//                googleApi.createEmptyCompanyTemplate("újcég");

                Intent intent = new Intent(getApplicationContext(), GoogleApiActivity.class);
                startActivity(intent);

            }
        });
    }

    public void updateGoogleApiTextView(String result) {
        googleApiResultTextView.setText(result);
    }
}
