package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    final static String TAG = "ProfileActivity"; //identify where log is coming from

    private ImageButton backIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        backIcon = findViewById(R.id.back_icon_profile);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        setDetails();


    }

    private void setDetails(){
        UserData userData = SharedPrefManager.loadUserData(ProfileActivity.this);
        TextView name = findViewById(R.id.google_name_profile);
        name.setText("Name: " + userData.getFirstName() + " " + userData.getLastName());
        TextView email = findViewById(R.id.google_email_profile);
        email.setText("Email: " + userData.getUserEmail());
        TextView userId = findViewById(R.id.user_id_profile);
        userId.setText("User ID: " + userData.getUID());
        ImageView profilePicture = findViewById(R.id.google_image_profile);
        Picasso.get().load(userData.getUserProfilePictureUrl()).into(profilePicture);

    }
}