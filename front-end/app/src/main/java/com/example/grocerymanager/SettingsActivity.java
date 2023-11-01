package com.example.grocerymanager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {


    final static String TAG = "SettingsActivity"; //identify where log is coming from

    private ImageButton backIcon;
    private Button signOutButton;
    private Button deleteAccountButton;
    private Button dietitianButton;

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount googleAccount;
    private int RC_SIGN_IN = 1;
    private NetworkManager networkManager;
    private OkHttpClient client;

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
        deleteAccountButton = findViewById(R.id.delete_account_settings);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmation();
            }
        });
        dietitianButton = findViewById(R.id.request_dietitian_settings);
        dietitianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        startActivity(mainIntent);
        finishAffinity();
    }
    private void deleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account? This action is irreversible. Once deleted, you will be redirected to the login page.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAccount() {
        // need to connect this to database by deleting entries
        networkManager = new NetworkManager(this);
        client = networkManager.getClient();


        String serverURL = "https://20.104.197.24/";
        UserData userData = SharedPrefManager.loadUserData(SettingsActivity.this);
        if (userData != null) {
            int userID = userData.getUID();
            Log.d(TAG, "Deleting user with ID: " + userID);
            Request request = new Request.Builder()
                    .url(serverURL + "delete/users?p1=" + userID)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "User successfully deleted");
                        signOut();
                        launchMainIntent();
                    } else {
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                    }
                }
            });
        } else {
            Log.d(TAG, "User data not found in shared preferences");
        }
    }
}