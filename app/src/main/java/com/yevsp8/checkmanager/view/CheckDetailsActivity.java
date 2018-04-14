package com.yevsp8.checkmanager.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Constants;

public class CheckDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_details);

        Toolbar toolbar = findViewById(R.id.toolbar_checkDetails);
        setSupportActionBar(toolbar);

        String[] recognisedText = new String[3];
        String checkId = "";
        if (getIntent().getExtras() != null) {
            recognisedText = getIntent().getExtras().getStringArray(Constants.RecognisedTextsArray);
            checkId = getIntent().getExtras().getString(Constants.SelectedCheckId);
        }

        FragmentManager manager = getSupportFragmentManager();
        CheckDetailsFragment fragment = new CheckDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SelectedCheckId, checkId);
        bundle.putStringArray(Constants.RecognisedTextsArray, recognisedText);
        fragment.setArguments(bundle);

        replaceFragmentToActivity(manager, fragment, R.id.checkdetails_fragmentcontainer);
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
