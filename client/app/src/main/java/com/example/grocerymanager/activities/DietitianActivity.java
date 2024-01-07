package com.example.grocerymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.grocerymanager.R;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.misc.ProfileActivityDietician;
import com.example.grocerymanager.misc.SettingsActivityDietician;

public class DietitianActivity extends AppCompatActivity {

    final static String TAG = "DietitianActivity"; //identify where log is coming from

    //    ChatGPT Usage: No. Adapted from previous implementation from different activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietitian);

        ImageButton menuIcon = findViewById(R.id.menu_bar_icon_dietitian);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(DietitianActivity.this, SettingsActivityDietician.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(DietitianActivity.this, ProfileActivityDietician.class);

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

        Button chatButton = findViewById(R.id.chatDietitianUserButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(DietitianActivity.this, DietitianListActivity.class);
            }
        });
    }
}