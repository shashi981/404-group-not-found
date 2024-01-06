package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ListActivity extends AppCompatActivity {

    final static String TAG = "ListActivity"; //identify where log is coming from


    //    ChatGPT Usage: No. Activity Never Used Functionally.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ImageButton backIcon = findViewById(R.id.back_icon_list);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }
}