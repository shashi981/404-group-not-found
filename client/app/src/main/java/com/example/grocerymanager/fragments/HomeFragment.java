package com.example.grocerymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.LandingActivity;
import com.example.grocerymanager.activities.ProfilePageActivity;
import com.example.grocerymanager.activities.SettingsPageActivity;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.models.User;

public class HomeFragment extends Fragment {

    private ImageButton profileIcon;
    private ImageButton settingsIcon;
    private TextView greetingsText;
//    private TextView assistanceHome;
    private LinearLayout inventoryButton;
    private LinearLayout shoppingButton;
    private LinearLayout chatButton;
    private LinearLayout recipeButton;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

//        salutationsNameHome = view.findViewById(R.id.salutations_name_home);
//        assistanceHome = view.findViewById(R.id.assistance_home);

        inventoryButton = view.findViewById(R.id.inventory_button);
        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(1);
            }
        });

        shoppingButton = view.findViewById(R.id.shopping_button);
        shoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(3);
            }
        });
        chatButton = view.findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(0);
            }
        });
        recipeButton = view.findViewById(R.id.recipe_button);
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(4);
            }
        });

        User user = User.getInstance();
        greetingsText = view.findViewById(R.id.greetings_text);
        greetingsText.setText("Hello " + user.getFirstName() + "!");

        return view;
    }
}
