package com.example.grocerymanager.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.grocerymanager.fragments.ChatFragment;
import com.example.grocerymanager.fragments.HomeFragment;
import com.example.grocerymanager.fragments.InventoryFragment;
import com.example.grocerymanager.fragments.ListFragment;
import com.example.grocerymanager.fragments.RecipeFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new InventoryFragment();
            case 2:
                return new HomeFragment();
            case 3:
                return new ListFragment();
            case 4:
                return new RecipeFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}