package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    final static String TAG = "HomeActivity"; //identify where log is coming from
    private Button scanGroceriesButton;
    private Button manageInventoryButton;
    private Button consultDietitianButton;
    private Button suggestedRecipesButton;

    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;


    private Spinner dropdownMenu;
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dropdownMenu = findViewById(R.id.menu_bar_home);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(adapter);

        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection here
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("...")){
                }
                else if(selectedItem.equals("Profile")){
                    launchProfileIntent();
                }
                else if(selectedItem.equals("Settings")){
                    launchSettingsIntent();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        scanGroceriesButton = findViewById(R.id.scan_groceries_button_home);
        scanGroceriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchScannerIntent();
            }
        });
        manageInventoryButton = findViewById(R.id.manage_inventory_button_home);
        manageInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchInventoryIntent();
            }
        });

        consultDietitianButton = findViewById(R.id.consult_dietitian_button_home);
        consultDietitianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchChatIntent();
            }
        });

        suggestedRecipesButton = findViewById(R.id.suggested_recipes_button_home);
        suggestedRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRecipeIntent();
            }
        });


        chatIcon = findViewById(R.id.chat_icon_home);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchChatIntent();
            }
        });

        scannerIcon = findViewById(R.id.scan_icon_home);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchScannerIntent();
            }
        });

        inventoryIcon = findViewById(R.id.inventory_icon_home);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchInventoryIntent();
            }
        });

        recipeIcon = findViewById(R.id.recipe_icon_home);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRecipeIntent();
            }
        });


    }

    private void launchScannerIntent() {
        Intent scannerIntent = new Intent(HomeActivity.this, ScannerActivity.class);
        startActivity(scannerIntent);
    }
    private void launchInventoryIntent() {
        Intent inventoryIntent = new Intent(HomeActivity.this, InventoryActivity.class);
        startActivity(inventoryIntent);
    }
    private void launchChatIntent() {
        Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);
        startActivity(chatIntent);
    }
    private void launchRecipeIntent() {
        Intent recipeIntent = new Intent(HomeActivity.this, RecipeActivity.class);
        startActivity(recipeIntent);
    }


    private void launchProfileIntent(){
        Log.d(TAG, "launched profile intent");
    }
    private void launchSettingsIntent(){
        Log.d(TAG, "launched settings intent");
    }
}