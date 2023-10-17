package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private Button scanGroceriesButton;
    private Button manageInventoryButton;
    private Button consultDietitianButton;
    private Button suggestedRecipesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
}