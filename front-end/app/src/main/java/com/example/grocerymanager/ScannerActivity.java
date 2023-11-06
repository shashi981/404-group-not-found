package com.example.grocerymanager;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

public class ScannerActivity extends AppCompatActivity {

    final static String TAG = "ScannerActivity"; //identify where log is coming from

    private ImageButton chatIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton menuIcon;
    private Button addItemsButton;

    private Button scanBarcodeButton;


    //    ChatGPT Usage: No. Adapted from other similar implementation in other activities.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        chatIcon = findViewById(R.id.chat_icon_scanner);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(ScannerActivity.this, UserListActivity.class);
                finish();
            }
        });


        inventoryIcon = findViewById(R.id.inventory_icon_scanner);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ScannerActivity.this, InventoryActivity.class);
                finish();
            }
        });

        recipeIcon = findViewById(R.id.recipe_icon_scanner);
        recipeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ScannerActivity.this, RecipeActivity.class);
                finish();
            }
        });

//        cartIcon = findViewById(R.id.shop_icon_scanner);
//        cartIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityLauncher.launchActivity(ScannerActivity.this, ListActivity.class);
//
//            }
//        });

        menuIcon = findViewById(R.id.menu_bar_icon_scanner);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(ScannerActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(ScannerActivity.this, ProfileActivity.class);

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
        scanBarcodeButton = findViewById(R.id.scanBarcodeButton);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ScannerActivity.this, BarcodeActivity.class);
            }
        });

        addItemsButton = findViewById(R.id.manual_scanner);
        addItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ScannerActivity.this, AddItemsActivity.class);
            }
        });

    }

}
