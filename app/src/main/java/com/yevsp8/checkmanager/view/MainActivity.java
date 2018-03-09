package com.yevsp8.checkmanager.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.yevsp8.checkmanager.CheckListFragment;
import com.yevsp8.checkmanager.GoogleApiActivity;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    public ProgressDialog updateProgressBar;
    CheckListFragment fragment;
    TextView latestSyncTextView;
    FloatingActionButton newImageButton;
    FloatingActionButton testApiButton;
    TextView googleApiResultTextView;

    //TODO csak a demo data miatt
    CheckViewModel viewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectMainActivity(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);

        Date date = Calendar.getInstance().getTime();
        viewModel.insertCheck(new Check("013018234", date.getTime(), 1250, "Főtáv", date.getTime(), false));
        viewModel.insertCheck(new Check("471145743", date.getTime(), 1250, "Telekom", date.getTime(), false));
        viewModel.insertCheck(new Check("963349038", date.getTime(), 8900, "Upc", date.getTime(), false));
        viewModel.insertCheck(new Check("459231004", date.getTime(), 22340, "Közművek", date.getTime(), false));

        FragmentManager manager = getSupportFragmentManager();
        fragment = new CheckListFragment();
        replaceFragmentToActivity(manager, fragment, R.id.checklist_fragmentcontainer);

        latestSyncTextView = findViewById(R.id.latest_synch);
        String lastSync = getValueFromSharedPreferences("last_sync");
        latestSyncTextView.setText("Legutoljára szinkronizálva: " + lastSync);

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
//                GoogleApiProviderOld googleApi=GoogleApiProviderOld.getInstance(MainActivity.this);
//                googleApi.getResultsFromApi();

//                GoogleApiProviderOld googleApi = GoogleApiProviderOld.getInstance(MainActivity.this);
//                googleApi.insertData("010101", "11300", "Valaki", "2017.01.01");

//                GoogleApiProviderOld googleApi = GoogleApiProviderOld.getInstance(MainActivity.this);
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
