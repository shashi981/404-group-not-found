package com.example.grocerymanager.activities;

import static com.example.grocerymanager.helpers.ServerManager.getRecipes;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.grocerymanager.R;
import com.example.grocerymanager.helpers.FragmentAdapter;
import com.example.grocerymanager.models.User;

public class LandingActivity extends AppCompatActivity {

    final static String TAG = "LandingActivity";
    ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        User user = User.getInstance();
        Log.d(TAG, user.getEmail());
        Log.d(TAG, user.getFirstName());
        Log.d(TAG, user.getLastName());
        Log.d(TAG, user.getUri().toString());
        Log.d(TAG, user.getUserType());
        Log.d(TAG, "" + user.getID());

        viewPager = findViewById(R.id.viewPager);

        FragmentAdapter adapter = new FragmentAdapter(this);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2,false);

        getRecipes(user);
    }

    public void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, true);
    }

}
