package com.example.grocerymanager;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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