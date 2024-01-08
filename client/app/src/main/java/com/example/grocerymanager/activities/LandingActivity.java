package com.example.grocerymanager.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.grocerymanager.R;
import com.example.grocerymanager.helpers.FragmentAdapter;

public class LandingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        viewPager = findViewById(R.id.viewPager);

        FragmentAdapter adapter = new FragmentAdapter(this);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2,false);
    }

    public void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, true);
    }

}
