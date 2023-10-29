package com.example.grocerymanager;

import static com.example.grocerymanager.GoogleAccountManager.setAccountInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity {


    final static String TAG = "SettingsActivity"; //identify where log is coming from

    private ImageButton backIcon;
    private Button signOutButton;

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount googleAccount;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backIcon = findViewById(R.id.back_icon_settings);
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

        signOutButton = findViewById(R.id.sign_out_settings);
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
    private void launchMainIntent() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        setAccountInfo(null, null, null);
        startActivity(mainIntent);
        finishAffinity();
    }
}