package com.yevsp8.checkmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHandler db = DbHandler.getInstance(this);
        db.generateDemoData();

        ListCheckFragment fragment = ListCheckFragment.newInsatce();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.checklist_fragmentcontainer, fragment);
        transaction.commit();

        TextView latestSynch = findViewById(R.id.latest_synch);
        //TODO resource-ba + lekérdezni az utolsó szinkronizációt
        latestSynch.setText("Legutoljára szinkronizálva: ");

        FloatingActionButton button = findViewById(R.id.newImage_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewImageActivity.class);
                startActivity(intent);
            } 
    });
    }
}
