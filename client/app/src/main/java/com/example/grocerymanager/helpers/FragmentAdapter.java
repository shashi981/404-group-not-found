package com.example.grocerymanager.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.grocerymanager.fragments.HomeFragment;
import com.example.grocerymanager.fragments.InventoryFragment;
import com.example.grocerymanager.fragments.RecipeFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new InventoryFragment();
            case 1:
                return new HomeFragment();
            case 2:
                return new RecipeFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        // Return the total number of screens
        return 3; // Change this to the actual number of screens
    }
}