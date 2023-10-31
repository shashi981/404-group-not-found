package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InventoryActivity extends AppCompatActivity {

    final static String TAG = "InventoryActivity"; //identify where log is coming from
    private OkHttpClient client;
    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton cartIcon;
    private ImageButton menuIcon;

    private Button addItemsButton;
    private InputStream inputStream;
    private Certificate certificate;
    private KeyStore keyStore;
    private TrustManagerFactory trustManagerFactory;
    private TrustManager[] trustManagers;
    private SSLContext sslContext;
    private NetworkManager networkManager;
    private List<Item> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        networkManager = new NetworkManager(this);
        client = networkManager.getClient();
        itemList = new ArrayList<>();

        String serverURL = "https://20.104.197.24/";
        UserData userData = SharedPrefManager.loadUserData(InventoryActivity.this);
        int userID = userData.getUID();
        Request requestName = new Request.Builder()
                .url(serverURL + "get/items?p1=" + userID)
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

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String itemName = jsonObject.getString("Name");
                            String expiryDate = jsonObject.getString("ExpireDate");
                            int quantity = jsonObject.getInt("ItemCount");

                            // Create an Item object
                            Item item = new Item(itemName, expiryDate, quantity);
                            itemList.add(item);
                        }

                        // Display the items
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayItems();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-200 status code)
                    Log.e(TAG, "Unsuccessful response: " + response.code());
                }
            }
        });

        chatIcon = findViewById(R.id.chat_icon_inventory);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(InventoryActivity.this, ChatActivity.class);
                finish();
            }
        });

        scannerIcon = findViewById(R.id.scan_icon_inventory);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(InventoryActivity.this, ScannerActivity.class);
                finish();
            }
        });

//        inventoryIcon = findViewById(R.id.inventory_icon_recipe);
//        inventoryIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityLauncher.launchActivity(RecipeActivity.this, InventoryActivity.class);
//
//            }
//        });

        recipeIcon = findViewById(R.id.recipe_icon_inventory);
        recipeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(InventoryActivity.this, RecipeActivity.class);
                finish();
            }
        });

        cartIcon = findViewById(R.id.shop_icon_inventory);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(InventoryActivity.this, ListActivity.class);

            }
        });

        menuIcon = findViewById(R.id.menu_bar_icon_inventory);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(InventoryActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(InventoryActivity.this, ProfileActivity.class);

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

        addItemsButton = findViewById(R.id.add_items_inventory);
        addItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(InventoryActivity.this, AddItemsActivity.class);
            }
        });
    }
    private void displayItems(){
        for(Item item: itemList) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_display_template, null);

            TextView itemNameTextView = view.findViewById(R.id.item_name_text_view);
            TextView expiryDateTextView = view.findViewById(R.id.expiry_date_text_view);
            TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
            Button editButton = view.findViewById(R.id.edit_button);
            Button deleteButton = view.findViewById(R.id.delete_button);

            itemNameTextView.setText(item.getName());
            expiryDateTextView.setText("Expiry Date: " + item.getExpiry());
            quantityTextView.setText("Quantity: " + item.getQuantity());

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //need to implement editing
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //need to implement for delete
                }
            });

            LinearLayout mainLayout = findViewById(R.id.inventory_container_inventory);
            mainLayout.addView(view);
        }

    }
}