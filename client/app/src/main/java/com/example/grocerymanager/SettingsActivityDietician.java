package com.example.grocerymanager;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivityDietician extends AppCompatActivity {


    final static String TAG = "SettingsActivity"; //identify where log is coming from

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount googleAccount;


    //    ChatGPT Usage: No. Adapted from other similar implementation in other activities.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_dietician);

        ImageButton backIcon = findViewById(R.id.back_icon_settings_dietician);
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

        Button signOutButton = findViewById(R.id.sign_out_settings_dietician);
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
        Button deleteAccountButton = findViewById(R.id.delete_account_settings_dietician);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmation();
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
        Intent mainIntent = new Intent(SettingsActivityDietician.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finishAffinity();
    }

    //    ChatGPT Usage: No.
    private void deleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account? This action is irreversible. Once deleted, you will be redirected to the login page.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteAccount();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //    ChatGPT Usage: No.
    private void deleteAccount() {
        // need to connect this to database by deleting entries
        NetworkManager networkManager = new NetworkManager(this);
        OkHttpClient client = networkManager.getClient();


        String serverURL = "https://20.104.197.24/";
        DietitianData dieticianData = SharedPrefManager.loadDietitianData(SettingsActivityDietician.this);
        if (dieticianData != null) {
            int DID = dieticianData.getDID();
            Log.d(TAG, "Deleting user with ID: " + DID);
            Request request = new Request.Builder()
                    .url(serverURL + "delete/dietician?p1=" + DID)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                    launchMainIntent();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Dietician successfully deleted");
                        signOut();
                        launchMainIntent();
                    } else {
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        launchMainIntent();
                    }
                }
            });
        } else {
            Log.d(TAG, "Dietician data not found in shared preferences");
            launchMainIntent();
        }
    }
}