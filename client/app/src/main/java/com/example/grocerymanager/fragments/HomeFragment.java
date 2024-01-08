package com.example.grocerymanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.LandingActivity;
import com.example.grocerymanager.activities.ProfilePageActivity;
import com.example.grocerymanager.activities.SettingsPageActivity;
import com.example.grocerymanager.helpers.ActivityLauncher;

public class HomeFragment extends Fragment {

    private ImageButton profileIcon;
    private ImageButton settingsCog;
//    private TextView salutationsNameHome;
//    private TextView assistanceHome;
    private LinearLayout panel1;
    private LinearLayout panel2;
    private LinearLayout panel3;
    private LinearLayout panel4;

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

        settingsCog = view.findViewById(R.id.settings_cog);

        settingsCog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchActivity(requireActivity(), SettingsPageActivity.class);
            }
        });

//        salutationsNameHome = view.findViewById(R.id.salutations_name_home);
//        assistanceHome = view.findViewById(R.id.assistance_home);

        panel1 = view.findViewById(R.id.panel1);
        panel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(1);
            }
        });

        panel2 = view.findViewById(R.id.panel2);
        panel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(3);
            }
        });
        panel3 = view.findViewById(R.id.panel3);
        panel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(0);
            }
        });
        panel4 = view.findViewById(R.id.panel4);
        panel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LandingActivity) requireActivity()).setCurrentItem(4);
            }
        });

        return view;
    }
}
