package com.yevsp8.checkmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class CheckDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_details);

        TextView id = findViewById(R.id.check_details_id);
        TextView created = findViewById(R.id.check_details_create);
        TextView amount = findViewById(R.id.check_details_amount);
        TextView paidto = findViewById(R.id.check_details_paidto);
        TextView paiddate = findViewById(R.id.check_details_paiddate);
        TextView isuploaded = findViewById(R.id.check_details_isUploaded);


        id.setText(getIntent().getStringExtra("selected_check_id"));

    }

}
