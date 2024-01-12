package com.example.grocerymanager.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.ProfilePageActivity;
import com.example.grocerymanager.activities.SettingsPageActivity;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.models.Recipe;
import com.example.grocerymanager.models.RecipeSet;

public class RecipeFragment extends Fragment {

    final static String TAG = "RecipeFragment";
    private ImageButton profileIcon;
    private ImageButton settingsIcon;
    private LinearLayout linearLayout;
    private Context context;
    public RecipeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        context = getContext();

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

        linearLayout = view.findViewById(R.id.linear_layout);

        return view;
    }

    @Override
    public void onStart() {
        setRecipes();
        super.onStart();
    }

    private void setRecipes() {
        linearLayout.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecipeSet recipeSet = RecipeSet.getInstance();
        Log.d(TAG, "" + recipeSet.getRecipes().size());
        for(Recipe recipe : recipeSet.getRecipes()){
            View recipePreview = inflater.inflate(R.layout.recipe_preview, linearLayout, false);

            TextView recipeName = recipePreview.findViewById(R.id.recipe_name);
            recipeName.setText(recipe.getRecipeName());
            TextView recipeIngredients = recipePreview.findViewById(R.id.recipe_ingredients);
            recipeIngredients.setText(recipe.getFiveIngredientsAsString());
            recipePreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayFullRecipe(recipe);
                }
            });

            linearLayout.addView(recipePreview);
        }
    }

    private void displayFullRecipe(Recipe recipe){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.recipe_fullview);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView recipeName = dialog.findViewById(R.id.recipe_text);
        TextView ingredientsText = dialog.findViewById(R.id.ingredients_text);
        TextView instructionsText = dialog.findViewById(R.id.instructions_text);
        TextView youtubeText = dialog.findViewById(R.id.youtube_text);

        recipeName.setText(recipe.getRecipeName());
        ingredientsText.setText(recipe.getIngredientsAsString());
        instructionsText.setText(recipe.getInstructions());
        youtubeText.setText(recipe.getYouTubeLink());

        ImageView closeButton = dialog.findViewById(R.id.close_icon);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
