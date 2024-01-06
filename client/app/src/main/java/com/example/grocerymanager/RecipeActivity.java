package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeActivity extends AppCompatActivity {

    final static String TAG = "RecipeActivity"; //identify where log is coming from
    private OkHttpClient client;

    private List<Recipe> recipeList;
    private Map<Integer, Recipe> recipeMap;


    //    ChatGPT Usage: No. Adapted from other similar implementation in other activities.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeList = new ArrayList<>();
        recipeMap = new HashMap<>();


        NetworkManager networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        String serverURL = "https://20.104.197.24/";
        UserData userData = SharedPrefManager.loadUserData(RecipeActivity.this);
        int userID = userData.getUID();
        Request requestName = new Request.Builder()
                .url(serverURL + "get/recipe?p1=" + userID)
                .build();
        client.newCall(requestName).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the response body as a string
                    String responseBody = response.body().string();

                    Log.d(TAG, "Response: " + responseBody);
                    Log.d(TAG, "Check 2");

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        Log.d(TAG, "Check 1");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.d(TAG, "Size of List: " + recipeList.size());
                            Log.d(TAG, "Size of JSON: " + jsonArray.length());
                            Log.d(TAG, "Current Loop: " + i);
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d(TAG, "Check 6");
                            int RID = jsonObject.getInt("RID");
                            Log.d(TAG, "Check 9");
                            JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
                            for(int j = 0; j < ingredientsArray.length(); j++){
                                JSONObject individualIngredientObject = ingredientsArray.getJSONObject(j);
                                String ingredientName = individualIngredientObject.getString("Ingredient");
                                Log.d(TAG, "Check 7");

                                String ingredientAmount = individualIngredientObject.getString("Amount");
                                Log.d(TAG, "Check 8");
                                if(!recipeMap.containsKey(RID)){
                                    Log.d(TAG, "Check 3");
                                    Recipe recipe = new Recipe(RID);
                                    recipe.addIngredient(ingredientName, ingredientAmount);
                                    recipeList.add(recipe);
                                    recipeMap.put(RID, recipe);
                                }
                                else{
                                    Log.d(TAG, "Check 4");
                                    Recipe recipe = recipeMap.get(RID);
                                    recipe.addIngredient(ingredientName, ingredientAmount);
                                }
                                Log.d(TAG, "Check 5");
                            }
                        }
                        Log.d(TAG, "Size of List: " + recipeList.size());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recipeMoreDetails();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response (e.g., non-200 status code)
                    Log.e(TAG, "Unsuccessful response: " + response.code());
                }
            }
        });




        ImageButton chatIcon = findViewById(R.id.chat_icon_recipe);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchActivity(RecipeActivity.this, UserListActivity.class);
                finish();
            }
        });

        ImageButton scannerIcon = findViewById(R.id.scan_icon_recipe);
        scannerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(RecipeActivity.this, ScannerActivity.class);
                finish();
            }
        });

        ImageButton inventoryIcon = findViewById(R.id.inventory_icon_recipe);
        inventoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchActivity(RecipeActivity.this, InventoryActivity.class);
                finish();
            }
        });

//        recipeIcon = findViewById(R.id.recipe_icon_recipe);
//        recipeIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityLauncher.launchActivity(RecipeActivity.this, RecipeActivity.class);
//
//            }
//        });

//        cartIcon = findViewById(R.id.shop_icon_recipe);
//        cartIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityLauncher.launchActivity(RecipeActivity.this, ListActivity.class);
//
//            }
//        });

        ImageButton menuIcon = findViewById(R.id.menu_bar_icon_recipe);
        PopupMenu popupMenu = new PopupMenu(this, menuIcon, 0, 0, R.style.PopupMenuStyle);

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.settings_dropdown) {
                    ActivityLauncher.launchActivity(RecipeActivity.this, SettingsActivity.class);

                    return true;
                } else if (id == R.id.profile_dropdown) {
                    ActivityLauncher.launchActivity(RecipeActivity.this, ProfileActivity.class);

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
    }

    //    ChatGPT Usage: Partial - Atomic aspect of this
    private void recipeMoreDetails() {
        int numberOfRecipes = recipeList.size();
        AtomicInteger completedRequests = new AtomicInteger(0);

        for (Recipe recipe : recipeList) {
            String serverURL = "https://20.104.197.24/";
            Request requestName = new Request.Builder()
                    .url(serverURL + "get/recipe_info?p1=" + recipe.getRID())
                    .build();
            client.newCall(requestName).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                    completedRequests.incrementAndGet();
                    compareRequests(numberOfRecipes, completedRequests.get());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();

                        Log.d(TAG, "Response: " + responseBody);

                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.d(TAG, "here 1");
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String recipeName = jsonObject.getString("Name");
                                String recipeInstructions = jsonObject.getString("Instruction");
                                String recipeYTLink = jsonObject.getString("YTLink");
                                recipe.setRecipeName(recipeName);
                                recipe.setInstructions(recipeInstructions);
                                recipe.setYouTubeLink(recipeYTLink);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        completedRequests.incrementAndGet();
                        compareRequests(numberOfRecipes, completedRequests.get());
                    } else {
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        completedRequests.incrementAndGet();
                        compareRequests(numberOfRecipes, completedRequests.get());
                    }
                }
            });
        }
    }

    //    ChatGPT Usage: Yes

    private synchronized void compareRequests(int totalRequests, int completedRequests) {
        if (completedRequests == totalRequests) {
            runOnUiThread(this::displayRecipes);
        }
    }

    //    ChatGPT Usage: No
    private void displayRecipes() {
        Log.d(TAG, String.valueOf(recipeList.size()));
        if (recipeList.size() == 0) {
            LinearLayout recipeContainer = findViewById(R.id.recipe_container_recipe);
            TextView textView = new TextView(RecipeActivity.this);
            textView.setText("Sorry, we do not have a recipe for you at the moment!\nPlease add more items to your inventory and come back later.");
            textView.setTextColor(getResources().getColor(R.color.dark_blue));
            textView.setTextSize(24);
            recipeContainer.addView(textView);
        } else {
            for (Recipe recipe : recipeList) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.recipe_preview_template, null);

                TextView recipeName = view.findViewById(R.id.name_of_recipe);
                TextView youtubeLink = view.findViewById(R.id.youtube_link);
                TextView fiveIngredients = view.findViewById(R.id.five_ingredients);
                Button selectRecipeButton = view.findViewById(R.id.select_recipe_button);

                recipeName.setText(recipe.getRecipeName());
                youtubeLink.setText(recipe.getYouTubeLink());
                fiveIngredients.setText(recipe.getFiveIngredientsAstString());

                selectRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayFullRecipe(recipe);
                    }
                });

                LinearLayout mainLayout = findViewById(R.id.recipe_container_recipe);
                mainLayout.addView(view);
            }
        }
    }


    //    ChatGPT Usage: No
    private void displayFullRecipe(Recipe recipe){
        Dialog dialog = new Dialog(RecipeActivity.this);
        dialog.setContentView(R.layout.full_recipe_popup_template);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        TextView recipeName = dialog.findViewById(R.id.name_of_recipe_expanded);
        TextView ingredientsList = dialog.findViewById(R.id.ingredients_listed);
        TextView instructionsList = dialog.findViewById(R.id.instructions_listed);

        recipeName.setText(recipe.getRecipeName());
        ingredientsList.setText(recipe.getIngredientsAsString());
        instructionsList.setText(recipe.getInstructions());

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