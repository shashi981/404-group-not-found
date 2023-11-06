package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class AddItemsActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {

    final static String TAG = "AddItemsActivity"; //identify where log is coming from
    private Button addItemButton;
    private EditText itemName;
    private Button itemExpiryButton;
    private EditText itemQuantity;
    private Button addItemsInventoryButton;

    private List<Item> itemList;
    private String expiryDateString;
    private List<String> expiryDateList;
    private List<String> itemNameList;
    private List<Integer> quantityList;
    private NetworkManager networkManager;
    private OkHttpClient client;


    //    ChatGPT Usage: Partial
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        expiryDateList = new ArrayList<>();
        itemNameList = new ArrayList<>();
        quantityList = new ArrayList<>();

        itemExpiryButton = findViewById(R.id.set_expiry_date);
        itemExpiryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        ImageButton backIcon = findViewById(R.id.back_icon_add);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
        itemName = findViewById(R.id.item_name);
        itemQuantity = findViewById(R.id.item_quantity);

        itemList = new ArrayList<>();


        addItemButton = findViewById(R.id.add_item_to_list);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemName.getText().toString().isEmpty() || expiryDateString.isEmpty() || itemQuantity.getText().toString().isEmpty()){

                }
                else{
                    String itemNameString = itemName.getText().toString();
                    String itemQuantityString = itemQuantity.getText().toString();

                    Item newItem = new Item(itemNameString, expiryDateString, Integer.parseInt(itemQuantityString), -1, "-1");
                    itemList.add(newItem);
                    addItemToInventory(newItem);
                    Log.d(TAG, itemList.toString());

                    itemName.getText().clear();
                    expiryDateString = "";
                    itemQuantity.getText().clear();
                }
            }

        });

        networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        String serverURL = "https://20.104.197.24/";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        UserData userData = SharedPrefManager.loadUserData(AddItemsActivity.this);

        addItemsInventoryButton = findViewById(R.id.add_items_to_inventory);
        addItemsInventoryButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if(itemList.isEmpty()){
                    launchInventoryIntent();
                }
                else{
                    for (Item item : itemList) {
                        expiryDateList.add(item.getExpiry());
                        itemNameList.add(item.getName());
                        quantityList.add(item.getQuantity());
                    }
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("p1", userData.getUID());
                        postData.put("p2", -1);
                        postData.put("p3", new JSONArray(expiryDateList)); // assuming expiryDateList is a list of Strings
                        postData.put("p4", new JSONArray(quantityList)); // assuming quantityList is a list of Integers
                        postData.put("p5", new JSONArray(itemNameList)); // assuming itemNameList is a list of Strings
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(JSON, postData.toString());

                    Request request = new Request.Builder()
                            .url(serverURL + "add/items_man")
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

                                launchInventoryIntent();
                            } else {
                                // Handle unsuccessful response
                                Log.e(TAG, "Unsuccessful response " + response.code());
                            }
                        }
                    });

                }

            }
        });

    }

    //    ChatGPT Usage: Partial
    private void addItemToInventory(Item item) {
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
                itemList.remove(item);
                LinearLayout mainLayout = findViewById(R.id.inventory_container);
                mainLayout.removeView(view);

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to implement for delete
                itemList.remove(item);
                LinearLayout mainLayout = findViewById(R.id.inventory_container);
                mainLayout.removeView(view);
            }
        });

        LinearLayout mainLayout = findViewById(R.id.inventory_container);
        mainLayout.addView(view);

    }

    //    ChatGPT Usage: Partial
    private void displayEditPopup(Item item) {
        Dialog dialog = new Dialog(AddItemsActivity.this);
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
                    addItemToInventory(item);
                    dialog.dismiss();

                }

            }
        });

        dialog.show();
    }


    //    ChatGPT Usage: No
    private void launchInventoryIntent() {
        Intent inventoryIntent = new Intent(AddItemsActivity.this, InventoryActivity.class);
        startActivity(inventoryIntent);
        finish();
    }

    //    ChatGPT Usage: Partial
    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //    ChatGPT Usage: Partial
    @Override
    public void onDateSet(int year, int month, int day) {
        // Use the selected date here as needed
        expiryDateString = String.format("%d-%02d-%02d", year, month + 1, day);
    }

}