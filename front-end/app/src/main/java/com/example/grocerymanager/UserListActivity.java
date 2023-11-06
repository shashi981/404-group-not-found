
package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

public class UserListActivity extends AppCompatActivity {
    final static String TAG = "UserListActivity"; //identify where log is coming from

    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton cartIcon;
    private ImageButton menuIcon;
    private NetworkManager networkManager;
    private OkHttpClient client;

    private static final String SERVER_URL = "https://20.104.197.24/";

    private RecyclerView dieticianRecyclerView;
    private DieticianAdapter dieticianAdapter;
    private List<DietitianData> dieticianList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        scannerIcon = findViewById(R.id.scan_icon_chat);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(UserListActivity.this, ScannerActivity.class);
                finish();
            }
        });

        inventoryIcon = findViewById(R.id.inventory_icon_chat);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(UserListActivity.this, InventoryActivity.class);
                finish();
            }
        });

        recipeIcon = findViewById(R.id.recipe_icon_chat);
        recipeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(UserListActivity.this, RecipeActivity.class);
                finish();
            }
        });

        cartIcon = findViewById(R.id.shop_icon_chat);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(UserListActivity.this, ListActivity.class);
            }
        });

        menuIcon = findViewById(R.id.menu_bar_icon_chat);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(UserListActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(UserListActivity.this, ProfileActivity.class);

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

        networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        dieticianRecyclerView = findViewById(R.id.dietician_recyclerview);
        dieticianRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager
        dieticianAdapter = new DieticianAdapter(this, dieticianList);
        dieticianRecyclerView.setAdapter(dieticianAdapter);

        fetchAvailableDieticians();
    }
    private void fetchAvailableDieticians() {
        String url = SERVER_URL + "get/availableDieticians";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching available dieticians: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Available Dieticians: " + responseBody);

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int did = jsonObject.getInt("DID");
                            String firstName = jsonObject.getString("FirstName");
                            String lastName = jsonObject.getString("LastName");
                            String email = jsonObject.getString("Email");
                            String profile = jsonObject.getString("ProfileURL");
                            Uri uri = Uri.parse(profile);
                            DietitianData dietitian = new DietitianData(firstName, lastName, email, uri, did);
                            dieticianList.add(dietitian);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dieticianAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error: " + response.body().string());
                }
            }
        });
    }
}




