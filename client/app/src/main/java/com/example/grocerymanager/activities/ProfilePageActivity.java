package com.example.grocerymanager.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;

public class ProfilePageActivity extends AppCompatActivity {

    final static String TAG = "ProfilePageActivity"; //identify where log is coming from


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

    }

}