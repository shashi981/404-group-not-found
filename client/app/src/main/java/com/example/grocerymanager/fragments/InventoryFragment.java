package com.example.grocerymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.ProfilePageActivity;
import com.example.grocerymanager.activities.SettingsPageActivity;
import com.example.grocerymanager.helpers.ActivityLauncher;

public class InventoryFragment extends Fragment {

    private ImageButton profileIcon;
    private ImageButton settingsIcon;

    public InventoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);
        profileIcon = view.findViewById(R.id.profile_icon);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchActivity(requireActivity(), ProfilePageActivity.class);
            }
        });

        settingsIcon = view.findViewById(R.id.settings_icon);

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchActivity(requireActivity(), SettingsPageActivity.class);
            }
        });

        return view;
    }
}
