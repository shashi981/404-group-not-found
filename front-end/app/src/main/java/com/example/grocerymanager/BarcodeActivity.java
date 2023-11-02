package com.example.grocerymanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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

public class BarcodeActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
    final static String TAG = "BarcodeActivity";
    private ImageButton backIcon1;
    private String expiryDateString1;
    private Button itemExpiryButton1;
    private Button addItemButton1;
    private EditText itemQuantity1;
    private Button addItemsInventoryButton1;
    private List<String> expiryDateList1;
    private List<Integer> upcList;
    private List<Integer> quantityList1;
    private List<Item> itemList1;
    private OkHttpClient client1;
    private Button scanBarButton;

    private NetworkManager networkManager1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        expiryDateList1 = new ArrayList<>();
        upcList = new ArrayList<>();
        quantityList1 = new ArrayList<>();

        itemExpiryButton1 = findViewById(R.id.set_expiry_date1);
        itemExpiryButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        backIcon1 = findViewById(R.id.back_icon_add1);
        backIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        itemQuantity1 = findViewById(R.id.item_quantity1);

        itemList1 = new ArrayList<>();
        addItemButton1 = findViewById(R.id.add_item_to_list1);
        addItemButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expiryDateString1.isEmpty() || itemQuantity1.getText().toString().isEmpty()) {

                } else {
                    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result ->{
                        if (result.getContents() != null) {

                            String upcCode = result.getContents();
                            int upc = Integer.parseInt(upcCode);
                            String itemQuantityStr = itemQuantity1.getText().toString();
                            Item newItem = new Item("", expiryDateString1, Integer.parseInt(itemQuantityStr), -1, upc);
                            itemList1.add(newItem);
                            addItemToInventory1(newItem);
                            Log.d(TAG, itemList1.toString());

                            itemQuantity1.getText().clear();
                            itemQuantity1.getText().clear();

                        }
                        // else redirect to manual adding
                    });


                }
            }
        });


        networkManager1 = new NetworkManager(this);
        client1 = networkManager1.getClient();

        String serURL = "https://20.104.197.24/";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        UserData userData = SharedPrefManager.loadUserData(BarcodeActivity.this);

        addItemsInventoryButton1 = findViewById(R.id.add_items_to_inventory1);
        addItemsInventoryButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemList1.isEmpty()){
                    launchInventoryIntent();
                }
                else {
                    for (Item item : itemList1){
                        expiryDateList1.add(item.getExpiry());
                        upcList.add(item.getUPC());
                        quantityList1.add(item.getQuantity());
                    }
                    JSONObject postData1 = new JSONObject();
                    try{
                        postData1.put("p1", userData.getUID());
                        postData1.put("p2", new JSONArray(upcList)); // assuming upcList is a list of Integers
                        postData1.put("p3", new JSONArray(expiryDateList1)); // assuming expiryDateList is a list of Strings
                        postData1.put("p4", new JSONArray(quantityList1)); // assuming quantityList is a list of Integers
                        postData1.put("p5", ""); //
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }

                    RequestBody body1 = RequestBody.create(JSON, postData1.toString());
                    Request request = new Request.Builder()
                            .url(serURL + "add/items_bar")
                            .post(body1)
                            .build();

                    client1.newCall(request).enqueue(new Callback() {
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




        scanBarButton = findViewById(R.id.scanBarButton);
        scanBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });


    }

    private void addItemToInventory1(Item item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_display_template, null);

//        TextView itemNameTextView = view.findViewById(R.id.item_name_text_view);
        TextView expiryDateTextView = view.findViewById(R.id.expiry_date_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        Button editButton = view.findViewById(R.id.edit_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

//        itemNameTextView.setText(item.getName());
        expiryDateTextView.setText("Expiry Date: " + item.getExpiry());
        quantityTextView.setText("Quantity: " + item.getQuantity());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to implement editing
                displayEditPopup(item);
                itemList1.remove(item);
                LinearLayout mainLayout = findViewById(R.id.inventory_container);
                mainLayout.removeView(view);

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to implement for delete
                itemList1.remove(item);
                LinearLayout mainLayout = findViewById(R.id.inventory_container);
                mainLayout.removeView(view);
            }
        });

        LinearLayout mainLayout = findViewById(R.id.inventory_container);
        mainLayout.addView(view);

    }

    private void displayEditPopup(Item item) {
        Dialog dialog = new Dialog(BarcodeActivity.this);
        dialog.setContentView(R.layout.edit_item_template);

        EditText quantityEditText = dialog.findViewById(R.id.edit_quantity);
        Button editExpiryDateButton = dialog.findViewById(R.id.edit_expiry_date_button);
        Button saveButton = dialog.findViewById(R.id.save_button);

        quantityEditText.setText(String.valueOf(item.getQuantity()));
        editExpiryDateButton.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> {
            int updatedQuantity = Integer.parseInt(quantityEditText.getText().toString());
            if(expiryDateString1 != null && !expiryDateString1.isEmpty() && updatedQuantity > 0){
                item.setQuantity(updatedQuantity);
                item.setExpiry(expiryDateString1);
                addItemToInventory1(item);
                dialog.dismiss();

            }

        });

        dialog.show();
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        // Use the selected date here as needed
        expiryDateString1 = String.format("%d-%02d-%02d", year, month + 1, day);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn on flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barcodeLauncher.launch(options);
    }

    private void launchInventoryIntent() {
        Intent inventoryIntent = new Intent(BarcodeActivity.this, InventoryActivity.class);
        startActivity(inventoryIntent);
        finish();
    }

    ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result ->{
        if (result.getContents() != null) {

//            String upcCode = result.getContents();

            AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this);
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i){
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}
