package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

    final static String TAG = "ChatUserActivity"; //identify where log is coming from
    private WebSocket webSocket;
    private OkHttpClient client;
    private UserData userData;
    private static final String SERVER_URL = "wss://20.104.197.24";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        userData = SharedPrefManager.loadUserData(ChatActivity.this);

        initializeWebSocket();
        sendTestMessage();
        fetchChatHistory(userData.getUID(),1);
        Log.d(TAG, "UID" + userData.getUID());
    }


    private void initializeWebSocket() {
        NetworkManager networkManager = new NetworkManager(this);
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
