package com.example.grocerymanager.activities;


import static com.example.grocerymanager.helpers.ServerManager.deleteUser;
import static com.example.grocerymanager.helpers.ServerManager.postForm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

public class ContactActivity extends AppCompatActivity {
    final static String TAG = "ContactActivity";

    private User user;

    private String selectedSubject;

    private Spinner spinnerSubject;

    private ContactForm contactForm;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText concernsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactForm = ContactForm.getInstance();

        user = User.getInstance();

        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        concernsEditText = findViewById(R.id.concerns_edit_text);

        ImageButton backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        spinnerSubject = findViewById(R.id.subject_spinner);

        String[] subjects = {"Request Dietitian View", "Report A User", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSubject.setAdapter(adapter);

        if(contactForm.getSubject().equals("Request Dietitian View")){
            spinnerSubject.setSelection(0);
        }
        else{
            Log.d(TAG, contactForm.getSubject());
            spinnerSubject.setSelection(1);
        }

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSubject = (String) parentView.getItemAtPosition(position);
                View view = spinnerSubject.getSelectedView();
                ((TextView) view).setTextColor(Color.parseColor("#FF20462F"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });


    }

    private void submitForm() {
        String name = nameEditText.getText().toString();
        if(name.isEmpty()){
            emptySection("name");
            return;
        }

        String email = emailEditText.getText().toString();
        if(email.isEmpty()){
            emptySection("email");
            return;
        }

        String phone = phoneEditText.getText().toString();
        if(phone.isEmpty() || phone.length() != 10){
            emptySection("phone");
            return;
        }

        String subject = spinnerSubject.getSelectedItem().toString();
        if(subject.isEmpty()){
            emptySection(("subject"));
            return;
        }

        String concerns = concernsEditText.getText().toString();
        if(concerns.isEmpty()){
            emptySection(("concerns"));
            return;
        }

        contactForm.updateForm(name, email, phone, subject, concerns, user);
        if(postForm(contactForm)){
            ActivityLauncher.launchActivity(ContactActivity.this, SettingsPageActivity.class);
            Toast.makeText(ContactActivity.this, "Thank you for your submission, we will get back to you within 2-3 business days.", Toast.LENGTH_LONG).show();
        }
        else{
            submissionFailure();
        }
    }

    private void submissionFailure(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unfortunately we were unable to receive your message, please try submitting again. If this issue persists, please contact us at 404groupnotfound@gmail.com, our apologies for the inconvenience.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void emptySection(String section){
        Toast.makeText(ContactActivity.this, "Incorrect " + section + " entry, please ensure proper formatting.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        if(!nameEditText.getText().toString().isEmpty() || !emailEditText.getText().toString().isEmpty() || !phoneEditText.getText().toString().isEmpty() || !concernsEditText.getText().toString().isEmpty()){
            backConfirmation();
        }
        else{
            ContactForm.clearInstance();
            super.onBackPressed();
        }
    }

    private void backConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Going back does not save your inquiry, are you sure you want to go back?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContactForm.clearInstance();
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