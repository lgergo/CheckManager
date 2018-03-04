package com.yevsp8.checkmanager.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yevsp8.checkmanager.ImageProcessor;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.data.CheckRepository;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;

import javax.inject.Inject;

public class RecognisedCheckActivity extends AppCompatActivity {

    @Inject
    ImageProcessor imageProcessor;
    @Inject
    CheckRepository repo;
    private ProgressDialog progressDialog;
    private TextView id;
    private TextView amount;
    private TextView paidto;
    private TextView paiddate;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognised_check);

        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        component.injectRecognisedCheckActivity(this);

        path = getIntent().getExtras().getString("path");

        //egyenlőre demo data
        String demo_checkId = "0123456789876";
        String demo_amount = "1250";
        String demo_paidTo = "Főgáz";
        String demo_paidDate = "2017.12.02";

        id = findViewById(R.id.recognised_check_id);
        amount = findViewById(R.id.recognised_check_amount);
        paidto = findViewById(R.id.recognised_check_paidTo);
        paiddate = findViewById(R.id.recognised_check_paidDate);

        id.setText(demo_checkId);
        amount.setText(demo_amount);
        paidto.setText(demo_paidTo);
        paiddate.setText(demo_paidDate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Recognise in progress...");

        Button buttonUpload = findViewById(R.id.button_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO google api upload meghívása
            }
        });

        progressDialog.show();
        callTesseractForRecognise();
        progressDialog.hide();
    }

    private void callTesseractForRecognise() {
        String result = imageProcessor.startImageProcess(path);

        //TODO egyenlőre csak ide berakja
        id.setText(result);

        //TODO check mentése adatbázisba repo-n keresztül
    }
}
