package com.example.grocerymanager.activities;


import static com.example.grocerymanager.helpers.ServerManager.deleteUser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.models.ContactForm;
import com.example.grocerymanager.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsPageActivity extends AppCompatActivity {


    final static String TAG = "SettingsPageActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount googleAccount;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        user = User.getInstance();

        ImageButton backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        LinearLayout logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(googleAccount != null){
                    logout();
                    launchMainIntent();
                }
                else{
                    launchMainIntent();
                }
            }
        });

        LinearLayout deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmation();
            }
        });

        LinearLayout reportButton = findViewById(R.id.report_button);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(SettingsPageActivity.this, ContactActivity.class);
            }
        });

        LinearLayout dietitianButton = findViewById(R.id.dietitian_button);
        dietitianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactForm contactForm = ContactForm.getInstance();
                contactForm.setSubject("Request Dietitian View");
                ActivityLauncher.launchActivity(SettingsPageActivity.this, ContactActivity.class);
            }
        });

    }

    private void logout() {
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

    private void deleteAccount() {
        if(!deleteUser(user)){
            Toast.makeText(SettingsPageActivity.this, "Deletion Unsuccessful ... Logging Out", Toast.LENGTH_LONG).show();
        }
        if(googleAccount != null){
            logout();
            launchMainIntent();
        }
        else{
            launchMainIntent();
        }
    }

    private void launchMainIntent() {
        Intent mainIntent = new Intent(SettingsPageActivity.this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finishAffinity();
    }


}