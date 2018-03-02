package com.yevsp8.checkmanager.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import javax.inject.Inject;

public class CheckDetailsActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    CheckViewModel viewModel;
    Check check;

    TextView id;
    TextView created;
    TextView amount;
    TextView paidto;
    TextView paiddate;
    TextView isuploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_details);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectCheckViewModel(this);

        String checkId = getIntent().getStringExtra("selected_check_id");
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);
        viewModel.getCheckById(checkId).observe(this, new Observer<Check>() {
            @Override
            public void onChanged(@Nullable Check check) {
                if (CheckDetailsActivity.this.check != null) {
                    CheckDetailsActivity.this.check = check;
                    setTextViewValues();
                }

            }
        });

        id = findViewById(R.id.check_details_id);
        created = findViewById(R.id.check_details_create);
        amount = findViewById(R.id.check_details_amount);
        paidto = findViewById(R.id.check_details_paidto);
        paiddate = findViewById(R.id.check_details_paiddate);
        isuploaded = findViewById(R.id.check_details_isUploaded);
    }

    void setTextViewValues() {
        id.setText(getIntent().getStringExtra("selected_check_id"));
        created.setText(getIntent().getStringExtra("selected_check_created"));
        amount.setText(getIntent().getStringExtra("selected_check_amount"));
        paidto.setText(getIntent().getStringExtra("selected_check_paidTo"));
        paiddate.setText(getIntent().getStringExtra("selected_check_paidDate"));
        isuploaded.setText(getIntent().getStringExtra("selected_check_isUploaded"));
    }

}
