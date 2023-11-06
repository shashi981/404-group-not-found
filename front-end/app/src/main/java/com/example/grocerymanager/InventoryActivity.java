package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InventoryActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {

    final static String TAG = "InventoryActivity"; //identify where log is coming from
    private OkHttpClient client;
    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private List<Integer> itemIdList;
    private List<Integer> itemIdListEdit;
    private List<String> itemUPCList;
    private List<String> itemExpiryList;
    private List<Integer> itemCountList;
    private ImageButton recipeIcon;
    private ImageButton menuIcon;

    private Button addItemsButton;
    private NetworkManager networkManager;
    private List<Item> itemList;
    private UserData userData;
    private String expiryDateString;


    //    ChatGPT Usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        networkManager = new NetworkManager(this);
        client = networkManager.getClient();
        itemList = new ArrayList<>();

        String serverURL = "https://20.104.197.24/";
        userData = SharedPrefManager.loadUserData(InventoryActivity.this);
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
                            int itemId = jsonObject.getInt("ItemID");
                            String itemUPC = jsonObject.getString("UPC");

                            // Create an Item object
                            Item item = new Item(itemName, expiryDate, quantity, itemId, itemUPC);
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

                ActivityLauncher.launchActivity(InventoryActivity.this, UserListActivity.class);
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

//        cartIcon = findViewById(R.id.shop_icon_inventory);
//        cartIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityLauncher.launchActivity(InventoryActivity.this, ListActivity.class);
//
//            }
//        });

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

    //    ChatGPT Usage: Partial
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
                    displayEditPopup(item);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //need to implement for delete
                    String serverURL = "https://20.104.197.24/";
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    itemList.remove(item);
                    itemIdList = new ArrayList<>();
                    itemIdList.add(item.getItemId());
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("p1", userData.getUID());
                        postData.put("p2", new JSONArray(itemIdList));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(JSON, postData.toString());

                    Request request = new Request.Builder()
                            .url(serverURL + "delete/items")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // Handle successful response
                                String responseData = response.body().string();
                                Log.d(TAG, "Response: " + responseData);

                                ActivityLauncher.launchActivity(InventoryActivity.this, InventoryActivity.class);
                                finish();
                            } else {
                                // Handle unsuccessful response
                                Log.e(TAG, "Unsuccessful response " + response.code());
                            }
                        }
                    });
                }
            });

            LinearLayout mainLayout = findViewById(R.id.inventory_container_inventory);
            mainLayout.addView(view);
        }

    }

    //    ChatGPT Usage: Partial
    private void displayEditPopup(Item item) {
        Dialog dialog = new Dialog(InventoryActivity.this);
        dialog.setContentView(R.layout.edit_item_template);

        EditText quantityEditText = dialog.findViewById(R.id.edit_quantity);
        Button editExpiryDateButton = dialog.findViewById(R.id.edit_expiry_date_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        quantityEditText.setText(String.valueOf(item.getQuantity()));
        editExpiryDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updatedQuantity = Integer.parseInt(quantityEditText.getText().toString());
                if(expiryDateString != null && !expiryDateString.isEmpty() && updatedQuantity > 0){
                    item.setQuantity(updatedQuantity);
                    item.setExpiry(expiryDateString);

                    // Call a method to update the item on the server or database

                    String serverURL = "https://20.104.197.24/";
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    itemIdListEdit = new ArrayList<>();
                    itemUPCList = new ArrayList<>();
                    itemExpiryList = new ArrayList<>();
                    itemCountList = new ArrayList<>();
                    itemIdListEdit.add(item.getItemId());
                    itemUPCList.add(item.getUPC());
                    itemExpiryList.add(item.getExpiry());
                    itemCountList.add(item.getQuantity());
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("p1", userData.getUID());
                        postData.put("p2", new JSONArray(itemIdListEdit));
                        postData.put("p3", new JSONArray(itemUPCList));
                        postData.put("p4", new JSONArray(itemExpiryList));
                        postData.put("p5", new JSONArray(itemCountList));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(JSON, postData.toString());

                    Request request = new Request.Builder()
                            .url(serverURL + "update/items")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // Handle successful response
                                String responseData = response.body().string();
                                Log.d(TAG, "Response: " + responseData);

                                ActivityLauncher.launchActivity(InventoryActivity.this, InventoryActivity.class);
                                finish();
                            } else {
                                // Handle unsuccessful response
                                Log.e(TAG, "Unsuccessful response " + response.code());
                            }
                        }
                    });

                    // Dismiss the dialog after saving
                    dialog.dismiss();

                }

            }
        });

        dialog.show();
    }

    //    ChatGPT Usage: Yes
    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //    ChatGPT Usage: Yes
    @Override
    public void onDateSet(int year, int month, int day) {
        // Use the selected date here as needed
        expiryDateString = String.format("%d-%02d-%02d", year, month + 1, day);
    }
}