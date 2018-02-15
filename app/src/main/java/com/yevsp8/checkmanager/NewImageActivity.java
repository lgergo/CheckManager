package com.yevsp8.checkmanager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NewImageActivity extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image);

        btn = findViewById(R.id.take_picture);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivity(i);
            }
        });
    }
}
