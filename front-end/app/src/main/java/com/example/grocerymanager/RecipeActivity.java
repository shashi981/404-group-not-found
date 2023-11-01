package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecipeActivity extends AppCompatActivity {

    final static String TAG = "RecipeActivity"; //identify where log is coming from
    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton cartIcon;
    private ImageButton menuIcon;

    private NetworkManager networkManager;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);


        networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        String serverURL = "https://20.104.197.24/";
        UserData userData = SharedPrefManager.loadUserData(RecipeActivity.this);
        int userID = userData.getUID();
        Request requestName = new Request.Builder()
                .url(serverURL + "get/recipe?p1=" + userID)
                .build();
        client.newCall(requestName).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the response body as a string
                    String responseBody = response.body().string();

                    // Process the response on the main UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update the UI or perform any other necessary actions with the response
                            Log.d(TAG, "Response: " + responseBody);
                        }
                    });
                } else {
                    // Handle unsuccessful response (e.g., non-200 status code)
                    Log.e(TAG, "Unsuccessful response: " + response.code());
                }
            }
        });




        chatIcon = findViewById(R.id.chat_icon_recipe);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(RecipeActivity.this, ChatActivity.class);
                finish();
            }
        });

        scannerIcon = findViewById(R.id.scan_icon_recipe);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(RecipeActivity.this, ScannerActivity.class);
                finish();
            }
        });

        inventoryIcon = findViewById(R.id.inventory_icon_recipe);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(RecipeActivity.this, InventoryActivity.class);
                finish();
            }
        });

//        recipeIcon = findViewById(R.id.recipe_icon_recipe);
//        recipeIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityLauncher.launchActivity(RecipeActivity.this, RecipeActivity.class);
//
//            }
//        });

        cartIcon = findViewById(R.id.shop_icon_recipe);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(RecipeActivity.this, ListActivity.class);

            }
        });

        menuIcon = findViewById(R.id.menu_bar_icon_recipe);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(RecipeActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(RecipeActivity.this, ProfileActivity.class);

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