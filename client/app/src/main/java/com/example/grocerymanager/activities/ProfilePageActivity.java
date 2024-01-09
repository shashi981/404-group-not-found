package com.example.grocerymanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;
import com.example.grocerymanager.models.User;
import com.squareup.picasso.Picasso;

public class ProfilePageActivity extends AppCompatActivity {

    final static String TAG = "ProfilePageActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        ImageButton backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        setDetails();
    }

    private void setDetails(){
        User user = User.getInstance();
        TextView name = findViewById(R.id.profile_name);
        name.setText(user.getFirstName() + " " + user.getLastName());
        TextView email = findViewById(R.id.profile_email);
        email.setText("Email: " + user.getEmail());
        TextView accountType = findViewById(R.id.profile_type);
        accountType.setText("Account Type: " + user.getUserType());
        TextView userId = findViewById(R.id.profile_id);
        userId.setText("ID: " + user.getID());
        ImageView profilePicture = findViewById(R.id.profile_image);
        Picasso.get().load(user.getUri()).into(profilePicture);
    }

}