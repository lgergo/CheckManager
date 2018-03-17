package com.yevsp8.checkmanager.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

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

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);

        //TODO demodata
        long today = Calendar.getInstance().getTime().getTime();
        String month = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        viewModel.insertCheck(new Check("013018234", today, 1250, "Főtáv", month));
        viewModel.insertCheck(new Check("471145743", today, 1250, "Telekom", month));
        viewModel.insertCheck(new Check("963349038", today, 8900, "Upc", month));
        viewModel.insertCheck(new Check("459231004", today, 22340, "Közművek", month));

        FragmentManager manager = getSupportFragmentManager();
        fragment = new CheckListFragment();
        replaceFragmentToActivity(manager, fragment, R.id.checklist_fragmentcontainer);

        latestSyncTextView = findViewById(R.id.latest_synch);
        String lastSync = getValueFromSharedPreferences(R.string.last_sync_value, R.string.last_sync_default);
        latestSyncTextView.setText("Legutoljára szinkronizálva: " + lastSync);

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
//                GoogleApiProviderOld googleApi=GoogleApiProviderOld.getInstance(MainActivity.this);
//                googleApi.getResultsFromApi();

//                GoogleApiProviderOld googleApi = GoogleApiProviderOld.getInstance(MainActivity.this);
//                googleApi.insertData("010101", "11300", "Valaki", "2017.01.01");

//                GoogleApiProviderOld googleApi = GoogleApiProviderOld.getInstance(MainActivity.this);
//                googleApi.createEmptyCompanyTemplate("újcég");

                Intent intent = new Intent(getApplicationContext(), GoogleApiActivity.class);
                startActivity(intent);
            }
        });

        String val = getValueFromSharedPreferences(R.string.first_start_value, R.string.first_start_default);
        if (val.equals("1")) {
            showFirstStartAlertDialog();
        }
    }

    public void updateGoogleApiTextView(String result) {
        googleApiResultTextView.setText(result);
    }

    private void showFirstStartAlertDialog() {
        saveToSharedPreferences(R.string.sheetId_value, "0");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CheckManager");
        builder.setMessage("Hogy ki tudd használni a Google Sheets szinkronizációt, kérlek add meg a dokumentum azonosítóját.");
        builder.setNegativeButton("Most nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast t = Toast.makeText(getApplicationContext(), "Később is lehetőséged lesz megadni a Beállítások menüpont alatt.", Toast.LENGTH_SHORT);
                t.show();
            }
        });
        builder.setPositiveButton("Megadom", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
            }
        });
        builder.show();
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
