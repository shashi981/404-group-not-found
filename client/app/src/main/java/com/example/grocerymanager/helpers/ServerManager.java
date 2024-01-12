package com.example.grocerymanager.helpers;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.grocerymanager.models.ContactForm;
import com.example.grocerymanager.models.Recipe;
import com.example.grocerymanager.models.RecipeSet;
import com.example.grocerymanager.models.User;
import com.google.gson.Gson;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerManager {

    private static final String TAG = "Pathing";
    private static final String serverURL = "";

    private static OkHttpClient client = new OkHttpClient();
    private static Gson gson = new Gson();

    public static void getRequestTemplate(String data) {
        Request request = new Request.Builder()
            .url(serverURL + "/path")
            .addHeader("Authorization", "Bearer YOUR_TOKEN")
            .get()
            .build();

        // Synchronous
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d(TAG, responseBody);
            } else {
                Log.e(TAG, "Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getRequestTemplate2(String data) {
        Request request = new Request.Builder()
            .url(serverURL + "/path")
            .get()
            .build();

        // Asynchronous
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, responseBody);
                } else {
                    Log.e(TAG, "Request failed with code: " + response.code());
                }
            }
        
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });        
    }

    public static void postRequestTemplate1(String myData) {
        String jsonBody = gson.toJson(myData);

        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), jsonBody);

        Request request = new Request.Builder()
            .url(serverURL + "/path")
            .post(requestBody)
            .build();
        
        // Synchronous
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d(TAG, responseBody);
            } else {
                // Handle unsuccessful response
                Log.e(TAG, "Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean putUser(User user){
        user.setDBInfo("User", 1);
        return true;
    }

//    public static boolean putUser(User user){
//        String jsonBody = gson.toJson(user);
//        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), jsonBody);
//        Request request = new Request.Builder()
//                .url(serverURL + "/user")
//                .put(requestBody)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                Log.d(TAG, responseBody);
//                User updatedUser = gson.fromJson(responseBody, User.class);
//                user.setDBInfo(updatedUser.getUserType(), updatedUser.getID());
//                return true;
//            } else {
//                // Handle unsuccessful response
//                Log.e(TAG, "Request failed with code: " + response.code());
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static boolean deleteUser(User user) {
        User.clearInstance();
        return true;
    }

//    public static boolean deleteUser(User user){
//        Request request = new Request.Builder()
//                .url(serverURL + "/delete/" + user.getEmail())
//                .delete()
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                Log.d(TAG, responseBody);
//                User.clearInstance();
//                return true;
//            } else {
//                // Handle unsuccessful response
//                Log.e(TAG, "Request failed with code: " + response.code());
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }

    public static boolean postForm(ContactForm contactForm){
        ContactForm.clearInstance();
        return true;
    }

//    public static boolean getRecipes(User user){
//        Request request = new Request.Builder()
//                .url(serverURL + "/recipes?id=" + user.getID())
//                .get()
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                Log.d(TAG, responseBody);
//                try {
//                    JSONArray jsonArray = new JSONArray(responseBody);
//                    RecipeSet recipeSet = RecipeSet.getInstance();
//                    recipeSet.clearRecipes();
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String recipeName = jsonObject.getString("Name");
//                        String recipeInstructions = jsonObject.getString("Instruction");
//                        int RID = jsonObject.getInt("RID");
//                        String recipeYTLink = jsonObject.getString("YTLink");
//                        Recipe recipe = new Recipe(recipeName, recipeInstructions, RID, recipeYTLink);
//                        JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
//                        for(int j = 0; j < ingredientsArray.length(); j++){
//                            JSONObject individualIngredientObject = ingredientsArray.getJSONObject(j);
//                            String ingredientName = individualIngredientObject.getString("Ingredient");
//                            String ingredientAmount = individualIngredientObject.getString("Amount");
//                            recipe.addIngredient(ingredientName, ingredientAmount);
//                        }
//                        recipeSet.addRecipe(recipe);
//                    }
//                    return true;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    RecipeSet.clearInstance();
//                    return false;
//                }
//            } else {
//                // Handle unsuccessful response
//                Log.e(TAG, "Request failed with code: " + response.code());
//                RecipeSet.clearInstance();
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            RecipeSet.clearInstance();
//            return false;
//        }
//    }

    public static boolean getRecipes(User user){
        // courtesy of ChatGPT
        String mockedResponse = "["
                + "{\"Name\": \"Spaghetti Bolognese\", \"Instruction\": \"Cook pasta and prepare Bolognese sauce.\", \"RID\": 1, \"YTLink\": \"https://www.youtube.com/watch?v=123\", \"ingredients\": [{\"Ingredient\": \"Pasta\", \"Amount\": \"200g\"}, {\"Ingredient\": \"Ground Beef\", \"Amount\": \"300g\"}]},"
                + "{\"Name\": \"Chicken Stir-Fry\", \"Instruction\": \"Stir-fry chicken and vegetables.\", \"RID\": 2, \"YTLink\": \"https://www.youtube.com/watch?v=456\", \"ingredients\": [{\"Ingredient\": \"Chicken Breast\", \"Amount\": \"500g\"}, {\"Ingredient\": \"Broccoli\", \"Amount\": \"250g\"}]},"
                + "{\"Name\": \"Chocolate Cake\", \"Instruction\": \"Bake a delicious chocolate cake.\", \"RID\": 3, \"YTLink\": \"https://www.youtube.com/watch?v=789\", \"ingredients\": [{\"Ingredient\": \"Flour\", \"Amount\": \"2 cups\"}, {\"Ingredient\": \"Cocoa Powder\", \"Amount\": \"1/2 cup\"}]},"
                + "{\"Name\": \"Caprese Salad\", \"Instruction\": \"Assemble tomatoes, mozzarella, and basil.\", \"RID\": 4, \"YTLink\": \"https://www.youtube.com/watch?v=101\", \"ingredients\": [{\"Ingredient\": \"Tomatoes\", \"Amount\": \"4\"}, {\"Ingredient\": \"Mozzarella\", \"Amount\": \"200g\"}]},"
                + "{\"Name\": \"Vegetarian Pizza\", \"Instruction\": \"Prepare a tasty vegetarian pizza.\", \"RID\": 5, \"YTLink\": \"https://www.youtube.com/watch?v=202\", \"ingredients\": [{\"Ingredient\": \"Pizza Dough\", \"Amount\": \"1 ball\"}, {\"Ingredient\": \"Bell Peppers\", \"Amount\": \"1 cup\"}]}"
                + "]";

        // original code
        try {
            JSONArray jsonArray = new JSONArray(mockedResponse);
            RecipeSet recipeSet = RecipeSet.getInstance();
            recipeSet.clearRecipes();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String recipeName = jsonObject.getString("Name");
                String recipeInstructions = jsonObject.getString("Instruction");
                int RID = jsonObject.getInt("RID");
                String recipeYTLink = jsonObject.getString("YTLink");
                Recipe recipe = new Recipe(recipeName, recipeInstructions, RID, recipeYTLink);

                JSONArray ingredientsArray = jsonObject.getJSONArray("ingredients");
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject individualIngredientObject = ingredientsArray.getJSONObject(j);
                    String ingredientName = individualIngredientObject.getString("Ingredient");
                    String ingredientAmount = individualIngredientObject.getString("Amount");
                    recipe.addIngredient(ingredientName, ingredientAmount);
                }

                recipeSet.addRecipe(recipe);
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            RecipeSet.clearInstance();
            return false;
        }
    }
}