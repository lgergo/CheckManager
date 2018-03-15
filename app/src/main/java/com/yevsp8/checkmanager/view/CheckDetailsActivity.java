package com.yevsp8.checkmanager.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

import com.yevsp8.checkmanager.CheckDetailsFragment;
import com.yevsp8.checkmanager.R;

public class CheckDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_details);

        String checkId = getIntent().getExtras().getString("selected_check_id");
        String[] recognisedText = getIntent().getExtras().getStringArray("result_array");

        FragmentManager manager = getSupportFragmentManager();
        CheckDetailsFragment fragment = new CheckDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("selected_check_id", checkId);
        bundle.putStringArray("result_array", recognisedText);
        fragment.setArguments(bundle);

        replaceFragmentToActivity(manager, fragment, R.id.checkdetails_fragmentcontainer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
