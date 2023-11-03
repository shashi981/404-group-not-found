package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.net.URISyntaxException;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {


    final static String TAG = "ChatActivity"; //identify where log is coming from
    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton cartIcon;
    private ImageButton menuIcon;
    private NetworkManager networkManager;

    private WebSocket webSocket;
    private OkHttpClient client;
    private UserData userData;

    private static final String SERVER_URL = "wss://20.104.197.24";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        scannerIcon = findViewById(R.id.scan_icon_chat);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ChatActivity.this, ScannerActivity.class);
                finish();
            }
        });

        inventoryIcon = findViewById(R.id.inventory_icon_chat);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ChatActivity.this, InventoryActivity.class);
                finish();
            }
        });

        recipeIcon = findViewById(R.id.recipe_icon_chat);
        recipeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ChatActivity.this, RecipeActivity.class);
                finish();
            }
        });

        cartIcon = findViewById(R.id.shop_icon_chat);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(ChatActivity.this, ListActivity.class);
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
                    ActivityLauncher.launchActivity(ChatActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(ChatActivity.this, ProfileActivity.class);

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
        userData = SharedPrefManager.loadUserData(ChatActivity.this);

        initializeWebSocket();
        sendTestMessage();
        // fetchAvailableDieticians();
        // fetchUsersForDietician(1);
        fetchChatHistory(userData.getUID(),1);
        Log.d(TAG, "UID" + userData.getUID());
    }


    private void initializeWebSocket() {
        networkManager = new NetworkManager(this);
        client = networkManager.getClient();



        Request request = new Request.Builder().url(SERVER_URL)
                .header("actor-id", "" + userData.getUID()) // Replace with the actual actor-id
                .header("actor-type", "user") // Replace with 'user' or 'dietician'
                .build();
        Log.d(TAG, "ATTEMPTING");
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                // Handle successful connection
                Log.d(TAG, "CONNECTED BITCH");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // Handle incoming messages
                Log.d(TAG, "Received message: " + text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                // Handle connection failures
                Log.e(TAG, "WebSocket connection failed: " + t.getMessage());
            }
        });
    }

    private void sendTestMessage() {
        try {
            JSONObject message = new JSONObject();
            message.put("UID", userData.getUID());
            message.put("DID", 1);
            message.put("Text", "Test for Websocket");
            message.put("FROM_USER", 1);  // 1 denotes "from user"

            if (webSocket != null) {
                webSocket.send(message.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocket != null) {
            webSocket.close(1000, "App paused");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSocket == null) {
            initializeWebSocket();
            sendTestMessage();
        }
    }

    // This function fetches the list of available dieticians.
    private void fetchAvailableDieticians() {
        String url = "https://20.104.197.24/get/availableDieticians";

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
                    // TODO: Parse the response and update your UI accordingly.
                } else {
                    Log.e(TAG, "Error: " + response.body().string());
                }
            }
        });
    }

    // This function retrieves the chat history between a user and a dietician.
    // This function retrieves the unique users a dietician has communicated with.
    private void fetchUsersForDietician(int DID) {
        String url = "https://20.104.197.24/get/usersForDietician/" + DID;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching users for dietician: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Users for Dietician: " + responseBody);
                    // TODO: Parse the response and update your UI accordingly.
                } else {
                    Log.e(TAG, "Error: " + response.body().string());
                }
            }
        });
    }

    // This function retrieves the chat history between a user and a dietician
    private void fetchChatHistory(int UID, int DID) {
        String url = "https://20.104.197.24/get/chatHistory/" + UID + "/" + DID;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching chat history: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Chat History: " + responseBody);
                    // TODO: Parse the response, update your data model and refresh the UI.
                } else {
                    Log.e(TAG, "Error: " + response.body().string());
                }
            }
        });
    }



}
