package com.example.grocerymanager.activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;

public class SettingsPageActivity extends AppCompatActivity {


    final static String TAG = "SettingsActivity"; //identify where log is coming from

    //    ChatGPT Usage: No. Adapted from other similar implementation in other activities.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

    }

}