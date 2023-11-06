package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {


    final static String TAG = "AdminActivity"; //identify where log is coming from
    private ImageButton menuIcon;
    private List<UserData> requestUserList;
    private NetworkManager networkManager;
    private OkHttpClient client;


    //    ChatGPT Usage: No. Created from previous knowledge
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        requestUserList = new ArrayList<>();

        menuIcon = findViewById(R.id.menu_bar_icon_admin);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(AdminActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(AdminActivity.this, ProfileActivity.class);

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
        
        getRequests();
    }

    //    ChatGPT Usage: No. Adapted from previous implementation from different activity.
    private void getRequests() {
        String serverURL = "https://20.104.197.24/";
        Request requestName = new Request.Builder()
                .url(serverURL + "get/dietReq")
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
                            String firstName = jsonObject.getString("FirstName");
                            String lastName = jsonObject.getString("LastName");
                            int UID = jsonObject.getInt("UID");
                            String userEmail = jsonObject.getString("Email");
                            String userUriString = jsonObject.getString("ProfileURL");
                            String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
                            Uri defProfileUri = Uri.parse(defProfile);
                            UserData user;
                            if(userUriString.isEmpty() || userUriString == null){
                                 user = new UserData(firstName, lastName, userEmail, defProfileUri, UID);
                            }
                            else{
                                Uri userUri = Uri.parse(userUriString);
                                user = new UserData(firstName, lastName, userEmail, userUri, UID);
                            }

                            requestUserList.add(user);
                        }

                        // Display the items
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayRequests();
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
    }

    //    ChatGPT Usage: No. Adapted from previous implementation from different activity.
    private void displayRequests(){
        for(UserData user : requestUserList){
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dietitian_request_template, null);

                TextView dietitianName = view.findViewById(R.id.dietitian_name);
                TextView dietitianUID = view.findViewById(R.id.dietitian_user_id);
                Button approveButton = view.findViewById(R.id.approve_button);
                Button removeButton = view.findViewById(R.id.remove_button);

                dietitianName.setText("Name: " + user.getFirstName() + " " + user.getLastName());
                dietitianUID.setText("UID: " + user.getUID());

                String serverURL = "https://20.104.197.24/";

                approveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Request requestName = new Request.Builder()
                                .url(serverURL + "approve/dietReq?p1=" + user.getUID())
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
                                    ActivityLauncher.launchActivity(AdminActivity.this, AdminActivity.class);
                                } else {
                                    // Handle unsuccessful response (e.g., non-200 status code)
                                    Log.e(TAG, "Unsuccessful response: " + response.code());
                                }
                            }
                        });
                    }
                });

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Request requestName = new Request.Builder()
                                .url(serverURL + "remove/dietReq?p1=" + user.getUID())
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
                                    ActivityLauncher.launchActivity(AdminActivity.this, AdminActivity.class);
                                } else {
                                    // Handle unsuccessful response (e.g., non-200 status code)
                                    Log.e(TAG, "Unsuccessful response: " + response.code());
                                }
                            }
                        });
                    }
                });

                LinearLayout mainLayout = findViewById(R.id.inventory_container_admin);
                mainLayout.addView(view);
            }
    }
}