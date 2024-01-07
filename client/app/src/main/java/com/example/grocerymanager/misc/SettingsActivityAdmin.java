package com.example.grocerymanager.misc;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivityAdmin extends AppCompatActivity {


    final static String TAG = "SettingsActivityAdmin"; //identify where log is coming from

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount googleAccount;

    //    ChatGPT Usage: No. Adapted from other similar implementation in other activities.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_admin);

        ImageButton backIcon = findViewById(R.id.back_icon_settings_admin);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        Button signOutButton = findViewById(R.id.sign_out_settings_admin);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(googleAccount != null){
                    signOut();
                    launchMainIntent();
                }
                else{
                    launchMainIntent();
                }
            }
        });
    }

    //    ChatGPT Usage: No.
    private void signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                    Log.d(TAG, "Log out successful");
                    googleAccount = null;
                }
            });
    }

    //    ChatGPT Usage: No.
    private void launchMainIntent() {
        Intent mainIntent = new Intent(SettingsActivityAdmin.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finishAffinity();
    }
}