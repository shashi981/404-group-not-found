package com.example.grocerymanager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatUserActivity extends AppCompatActivity {
    final static String TAG = "ChatUserActivity"; //identify where log is coming from
    private NetworkManager networkManager;
    private WebSocket webSocket;
    private OkHttpClient client;
    private UserData userData;
    private ImageButton backIcon;
    private static final String SERVER_URL = "wss://20.104.197.24";

    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private List<ChatMessage> chatHistoryList = new ArrayList<>();

    private int curDID;


    //CHAT GPT USAGE: PARTIAL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);
        curDID = -1000;

        // Initialize UI components
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        EditText inputMessage = findViewById(R.id.inputMessage);
        Button sendButton = findViewById(R.id.sendButton);
        backIcon = findViewById(R.id.imageButton);

        // Set up RecyclerView with an empty adapter
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this,chatHistoryList);
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        //DID and UID
        curDID = getIntent().getIntExtra("selectedDietitianDID", -1000);
        Log.d(TAG, "DID = " + curDID);
        userData = SharedPrefManager.loadUserData(ChatUserActivity.this);
        Log.d(TAG, "UID = " + userData.getUID());

        initializeWebSocket();
        fetchChatHistory(userData.getUID(),curDID);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = inputMessage.getText().toString();
                if(!message.isEmpty()){
                    sendMessage(userData.getUID(), curDID, message);
                    fetchChatHistory(userData.getUID(),curDID);
                    inputMessage.setText("");
                }
            }
        });
    }
    //CHAT GPT USAGE: PARTIAL
    private void initializeWebSocket() {
        networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        Request request = new Request.Builder().url(SERVER_URL)
                .header("actor-id", "" + userData.getUID()) // Replace with the actual actor-id
                .header("actor-type", "user") // Replace with 'user' or 'dietician'
                .build();
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

    //CHAT GPT USAGE: FULL
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
                        chatHistoryList.clear();
                        // String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int fromUserBuffer = jsonObject.getJSONObject("FROM_USER").getJSONArray("data").getInt(0);
                            ChatMessage chatMessage = new ChatMessage(jsonObject.getString("Text"),
                                    fromUserBuffer,
                                    jsonObject.getInt("UID"),
                                    jsonObject.getInt("DID")
                            );
                            Log.d(TAG, "chatMessage = " + chatMessage.getMessage());
                            chatHistoryList.add(chatMessage);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "notifyData");
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

    //CHAT GPT USAGE: NO
    private void sendMessage(int UID, int DID, String Text) {
        try {
            JSONObject message = new JSONObject();
            message.put("UID", UID);
            message.put("DID", DID);
            message.put("Text", Text);
            message.put("FROM_USER", 1);  // 1 denotes "from user"

            if (webSocket != null) {
                webSocket.send(message.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
