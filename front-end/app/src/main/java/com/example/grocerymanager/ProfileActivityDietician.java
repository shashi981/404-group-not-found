package com.example.grocerymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ProfileActivityDietician extends AppCompatActivity {

    final static String TAG = "ProfileActivityDietician"; //identify where log is coming from



    //    ChatGPT Usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dietician);
        ImageButton backIcon = findViewById(R.id.back_icon_profile);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        setDetails();


    }

    //    ChatGPT Usage: No.
    private void setDetails(){
        DietitianData dieticianData = SharedPrefManager.loadDietitianData(ProfileActivityDietician.this);
        TextView name = findViewById(R.id.google_name_profile);
        name.setText("Name: " + dieticianData.getFirstName() + " " + dieticianData.getLastName());
        TextView email = findViewById(R.id.google_email_profile);
        email.setText("Email: " + dieticianData.getDietitianEmail());
        TextView DID = findViewById(R.id.user_id_profile);
        DID.setText("Dietician ID: " + dieticianData.getDID());
        ImageView profilePicture = findViewById(R.id.google_image_profile);
        Picasso.get().load(dieticianData.getDietitianProfilePictureUrl()).into(profilePicture);

    }
}