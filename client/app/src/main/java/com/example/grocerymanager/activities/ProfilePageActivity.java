package com.example.grocerymanager.activities;

import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;
import com.example.grocerymanager.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {

    final static String TAG = "ProfilePageActivity";

    private User user;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        user = User.getInstance();


        ImageButton backIcon = findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });


        setDetails();
    }

    private void showEditDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.restriction_toggle);

        final boolean[] nonDairy = {user.isNondairy()};
        final boolean[] vegan = {user.isVegan()};
        final boolean[] vegetarian = {user.isVegetarian()};

        Button nonDairyButton = dialog.findViewById(R.id.nondairy_button);
        nonDairyButton.setSelected(user.isNondairy());
        nonDairyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nonDairy[0] = !nonDairy[0];
                nonDairyButton.setSelected(nonDairy[0]);
            }
        });

        Button veganButton = dialog.findViewById(R.id.vegan_button);
        veganButton.setSelected(user.isVegan());
        veganButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vegan[0] = !vegan[0];
                veganButton.setSelected(vegan[0]);
            }
        });

        Button vegetarianButton = dialog.findViewById(R.id.vegetarian_button);
        vegetarianButton.setSelected(user.isVegetarian());
        vegetarianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vegetarian[0] = !vegetarian[0];
                vegetarianButton.setSelected(vegetarian[0]);

            }
        });

        Button saveButton = dialog.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setNondairy(nonDairy[0]);
                user.setVegan(vegan[0]);
                user.setVegetarian(vegetarian[0]);
                setRestrictions();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setDetails(){
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

        setRestrictions();
    }

    private void setRestrictions() {
        boolean nondairy = user.isNondairy();
        boolean vegan = user.isVegan();
        boolean vegetarian = user.isVegetarian();
        TextView restrictionsTextView = findViewById(R.id.restriction_text);
        restrictionsTextView.setText("");
        if(!nondairy && !vegan && !vegetarian){
            restrictionsTextView.setText("None");
        }
        else{
            String text = "";
            if(nondairy){
                text += "Non Dairy \n";
            }
            if(vegan){
                text += "Vegan \n";
            }
            if(vegetarian){
                text += "Vegetarian \n";
            }
            restrictionsTextView.setText(text);
        }
    }

}