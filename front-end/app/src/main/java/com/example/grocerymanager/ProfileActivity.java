package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    final static String TAG = "ProfileActivity"; //identify where log is coming from

    private ImageButton backIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        updateName();
        updateEmail();
        updatePicture();



        backIcon = findViewById(R.id.back_icon_profile);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });


    }

    private void updateName(){
        TextView textView = (TextView) findViewById(R.id.google_name_profile);
        textView.setText(GoogleAccountManager.getGoogleName());
    }
    private void updateEmail(){
        TextView textView = (TextView) findViewById(R.id.google_name_profile);
        textView.setText(GoogleAccountManager.getGoogleEmail());
    }
    private void updatePicture(){
        ImageView imageView = (ImageView) findViewById(R.id.google_image_profile);
        imageView.setImageURI(GoogleAccountManager.getGoogleProfilePictureUrl());
    }
}