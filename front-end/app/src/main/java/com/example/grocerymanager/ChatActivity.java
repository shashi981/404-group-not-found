package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {


    final static String TAG = "ChatActivity"; //identify where log is coming from
    private ImageButton chatIcon;
    private ImageButton scannerIcon;
    private ImageButton inventoryIcon;
    private ImageButton recipeIcon;
    private ImageButton cartIcon;
    private ImageButton menuIcon;

    private Socket mSocket;

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

        mSocket= SocketManager.getInstance().getSocket();

        // Listener for successful connection
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // You can log or show a toast here to indicate a successful connection
                Log.d(TAG, "Socket connected");
            }
        });

        // Listener for disconnection
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // Log or show a toast for disconnection
                Log.d(TAG, "Socket disconnected");
            }
        });

        // Listener for connection errors
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Throwable e = (Throwable) args[0];
                // Log or show the error
                Log.e(TAG, "Socket connection error", e);
            }
        });

        // Listener for other errors
        mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Throwable e = (Throwable) args[0];
                // Log or show the error
                Log.e(TAG, "Socket error", e);
            }
        });

        mSocket.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }
}