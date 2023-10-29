package com.example.grocerymanager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

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
    private ImageButton cartIcon;
    private ImageButton menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        scanGroceriesButton = findViewById(R.id.scan_groceries_button_home);
        scanGroceriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(HomeActivity.this, ScannerActivity.class);
                finish();

            }
        });
        manageInventoryButton = findViewById(R.id.manage_inventory_button_home);
        manageInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(HomeActivity.this, InventoryActivity.class);
                finish();

            }
        });

        consultDietitianButton = findViewById(R.id.consult_dietitian_button_home);
        consultDietitianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(HomeActivity.this, ChatActivity.class);
                finish();

            }
        });

        suggestedRecipesButton = findViewById(R.id.suggested_recipes_button_home);
        suggestedRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(HomeActivity.this, RecipeActivity.class);
                finish();

            }
        });


        chatIcon = findViewById(R.id.chat_icon_home);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(HomeActivity.this, ChatActivity.class);
                finish();

            }
        });

        scannerIcon = findViewById(R.id.scan_icon_home);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(HomeActivity.this, ScannerActivity.class);
                finish();

            }
        });

        inventoryIcon = findViewById(R.id.inventory_icon_home);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(HomeActivity.this, InventoryActivity.class);
                finish();
            }
        });

        recipeIcon = findViewById(R.id.recipe_icon_home);
        recipeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(HomeActivity.this, RecipeActivity.class);
                finish();
            }
        });

        cartIcon = findViewById(R.id.shop_icon_home);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(HomeActivity.this, ListActivity.class);

            }
        });

        menuIcon = findViewById(R.id.menu_bar_icon_home);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(HomeActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(HomeActivity.this, ProfileActivity.class);

                    return true;
                }
                return false;
            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });


    }
}