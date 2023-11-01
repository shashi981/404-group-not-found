package com.example.grocerymanager;

import static android.system.Os.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import com.google.firebase.FirebaseApp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.net.URISyntaxException;
import io.socket.client.IO;


public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private EditText messageInput;
    private Button sendButton;

    final static String TAG = "ChatActivity"; //identify where log is coming from
    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton cartIcon;
    private ImageButton menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//        chatIcon = findViewById(R.id.chat_icon_scanner);
//        chatIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                ActivityLauncher.launchActivity(ScannerActivity.this, ChatActivity.class);
//
//            }
//        });


        try {
            socket = IO.socket("https://20.104.197.24/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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

        FirebaseApp.initializeApp(this);


        messageInput = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageInput.setText("");
                }
            }
        });

        socket.on("chat message", onNewMessage);
        socket.connect();

    }

    private void sendMessage(String message) {
        // Send the message to the server
        JSONObject data = new JSONObject();
        try {
            data.put("uid", userId);
            data.put("did", dieticianId);
            data.put("text", message);
            data.put("fromUser", 1); // 1 for user, 0 for dietician
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("chat message", data);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    // Handle and display the incoming message in your UI
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the server when the activity is destroyed
        socket.disconnect();
    }
}