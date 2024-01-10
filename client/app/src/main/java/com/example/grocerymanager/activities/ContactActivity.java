package com.example.grocerymanager.activities;


import static com.example.grocerymanager.helpers.ServerManager.deleteUser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;
import com.example.grocerymanager.models.ContactForm;
import com.example.grocerymanager.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ContactActivity extends AppCompatActivity {

    private User user;

    private String selectedSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ContactForm contactForm = ContactForm.getInstance();

        user = User.getInstance();

        ImageButton backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        Spinner spinnerSubject = findViewById(R.id.spinnerSubject);

// Define your array of subjects (replace with your own data)
        String[] subjects = {"Request Dietitian View", "Report A User", "Other"};

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinnerSubject.setAdapter(adapter);

        if(!contactForm.getSubject().isEmpty()){
            spinnerSubject.setSelection(0);
        }

// Set a listener to handle item selections
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item
                selectedSubject = (String) parentView.getItemAtPosition(position);
                // Do something with the selected subject
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });


    }

    @Override
    public void onBackPressed() {

        if(selectedSubject.isEmpty()){
            backConfirmation();
        }
        else{
            super.onBackPressed();
        }
    }

    private void backConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Going back does not save your inquiry, are you sure you want to go back?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContactActivity.super.onBackPressed();
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
}