package com.yevsp8.checkmanager.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.yevsp8.checkmanager.GoogleApiActivity;
import com.yevsp8.checkmanager.ListCheckFragment;
import com.yevsp8.checkmanager.R;

public class MainActivity extends BaseActivity {

    public ProgressDialog updateProgressBar;
    ListCheckFragment fragment;
    TextView latestSynchTextView;
    FloatingActionButton newImageButton;
    FloatingActionButton testApiButton;
    TextView googleApiResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        fragment = (ListCheckFragment) manager.findFragmentById(R.id.checklist_fragmentcontainer);
        if (fragment == null) {
            fragment = ListCheckFragment.newInsatce();
        }

        addFragmentToActivity(manager, fragment, R.id.checklist_fragmentcontainer, "tag");

        latestSynchTextView = findViewById(R.id.latest_synch);
        //TODO resource-ba + lekérdezni az utolsó szinkronizációt
        latestSynchTextView.setText("Legutoljára szinkronizálva: ");

        newImageButton = findViewById(R.id.newImage_button);
        newImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, NewImageActivity.class);
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

                Intent intent = new Intent(context, GoogleApiActivity.class);
                startActivity(intent);

            }
        });
    }

    public void updateGoogleApiTextView(String result) {
        googleApiResultTextView.setText(result);
    }
}
