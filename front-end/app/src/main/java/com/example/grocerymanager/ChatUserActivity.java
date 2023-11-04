package com.example.grocerymanager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatUserActivity extends AppCompatActivity {
    final static String TAG = "ChatUserActivity"; //identify where log is coming from
    private NetworkManager networkManager;
    private WebSocket webSocket;
    private OkHttpClient client;
    private UserData userData;
    private static final String SERVER_URL = "wss://20.104.197.24";


    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private List<ChatMessage> chatHistoryList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);
        int curDID = -1000;

        // Initialize UI components
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        EditText inputMessage = findViewById(R.id.inputMessage);
        Button sendButton = findViewById(R.id.sendButton);

        // Set up RecyclerView with an empty adapter
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatHistoryList);
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();


        // GET DIETICION FORM THE INVOKING CLASS
        //TODO
        curDID = getIntent().getIntExtra("selectedDietitianDID", -1000);
        Log.d(TAG, "" + curDID);

//        if (didString != null && !didString.isEmpty()) {
//            try {
//                 curDID = Integer.parseInt(didString);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//        } else {
//            // DID wasn't provided. Handle this case, possibly finish the activity or show an error message.
//            Log.d(TAG,"DID is NULL");
//        }




        userData = SharedPrefManager.loadUserData(ChatUserActivity.this);
        Log.d(TAG, "UID" + userData.getUID());
        initializeWebSocket();
        sendTestMessage();
        //fetchChatHistory(userData.getUID(),curDID);
        fetchChatHistory(27,curDID);
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
                Log.d(TAG, "CONNECTED");
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
                    Log.d(TAG, "ChatHistory: " + responseBody);

                    try {
                        // String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int fromUserBuffer = jsonObject.getJSONObject("FROM_USER").getJSONArray("data").getInt(0);
                            ChatMessage chatMessage = new ChatMessage(jsonObject.getString("Text"),
                                    fromUserBuffer,
                                    jsonObject.getInt("UID"),
                                    jsonObject.getInt("DID")
                            );
                            Log.d(TAG, "MESSAGE" + chatMessage.toString());
                            chatHistoryList.add(chatMessage);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyDataSetChanged();
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

    private void sendTestMessage() {
        try {
            JSONObject message = new JSONObject();
            message.put("UID", 27);
            message.put("DID", 2);
            message.put("Text", "Test for Websocket");
            message.put("FROM_USER", 1);  // 1 denotes "from user"

            if (webSocket != null) {
                webSocket.send(message.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
