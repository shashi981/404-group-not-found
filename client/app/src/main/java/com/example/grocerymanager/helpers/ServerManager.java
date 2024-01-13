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
        user.setRestrictions(true, true, true);
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
                + "{\"Name\": \"Spaghetti Bolognese\", \"Instruction\": \"Cook pasta and prepare Bolognese sauce.\", \"RID\": 1, \"YTLink\": \"https://www.youtube.com/watch?v=v2WqcHH65NQ\", \"ingredients\": [{\"Ingredient\": \"Pasta\", \"Amount\": \"200g\"}, {\"Ingredient\": \"Ground Beef\", \"Amount\": \"300g\"}]},"
                + "{\"Name\": \"Chicken Stir-Fry\", \"Instruction\": \"Stir-fry chicken and vegetables.\", \"RID\": 2, \"YTLink\": \"https://www.youtube.com/watch?v=456\", \"ingredients\": [{\"Ingredient\": \"Chicken Breast\", \"Amount\": \"500g\"}, {\"Ingredient\": \"Broccoli\", \"Amount\": \"250g\"}]},"
                + "{\"Name\": \"Chocolate Cake\", \"Instruction\": \"Bake a delicious chocolate cake.\", \"RID\": 3, \"YTLink\": \"https://www.youtube.com/watch?v=789\", \"ingredients\": [{\"Ingredient\": \"Flour\", \"Amount\": \"2 cups\"}, {\"Ingredient\": \"Cocoa Powder\", \"Amount\": \"1/2 cup\"}]},"
                + "{\"Name\": \"Caprese Salad\", \"Instruction\": \"Assemble tomatoes, mozzarella, and basil.\", \"RID\": 4, \"YTLink\": \"https://www.youtube.com/watch?v=101\", \"ingredients\": [{\"Ingredient\": \"Tomatoes\", \"Amount\": \"4\"}, {\"Ingredient\": \"Mozzarella\", \"Amount\": \"200g\"}]},"
                + "{\"Name\": \"Vegetarian Pizza\", \"Instruction\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Vel quam elementum pulvinar etiam non quam lacus suspendisse faucibus. Sit amet mattis vulputate enim nulla aliquet porttitor lacus luctus. Faucibus in ornare quam viverra orci sagittis eu. Nulla aliquet porttitor lacus luctus accumsan tortor posuere ac ut. Sed euismod nisi porta lorem mollis. Quis risus sed vulputate odio ut. Nullam non nisi est sit. Laoreet suspendisse interdum consectetur libero id faucibus. Lectus vestibulum mattis ullamcorper velit sed ullamcorper morbi tincidunt. Vitae nunc sed velit dignissim sodales ut eu sem. Elit ut aliquam purus sit amet. Sed ullamcorper morbi tincidunt ornare massa eget egestas purus. Interdum consectetur libero id faucibus nisl tincidunt eget nullam. Imperdiet massa tincidunt nunc pulvinar sapien et ligula ullamcorper malesuada. Eget velit aliquet sagittis id consectetur purus ut. Nec ullamcorper sit amet risus. Urna nunc id cursus metus aliquam eleifend mi in nulla. Phasellus vestibulum lorem sed risus ultricies tristique nulla aliquet. Sit amet commodo nulla facilisi nullam vehicula ipsum.\n" +
                "\n" +
                "Ultrices mi tempus imperdiet nulla malesuada pellentesque elit eget gravida. Amet risus nullam eget felis eget nunc lobortis mattis aliquam. Est placerat in egestas erat imperdiet sed euismod nisi porta. Urna neque viverra justo nec ultrices dui. Orci ac auctor augue mauris augue neque gravida in. Cras tincidunt lobortis feugiat vivamus at augue. Integer enim neque volutpat ac tincidunt vitae semper quis lectus. Egestas dui id ornare arcu. Eget magna fermentum iaculis eu. Tortor posuere ac ut consequat semper viverra nam libero justo. Amet facilisis magna etiam tempor. Sed adipiscing diam donec adipiscing. Nullam ac tortor vitae purus. Bibendum arcu vitae elementum curabitur vitae nunc sed velit. Sed blandit libero volutpat sed cras ornare arcu dui. Vel pretium lectus quam id. Morbi tempus iaculis urna id volutpat lacus. Justo laoreet sit amet cursus sit amet dictum sit amet.\n" +
                "\n" +
                "Eu feugiat pretium nibh ipsum consequat nisl vel pretium. Nam at lectus urna duis convallis convallis tellus id interdum. Nibh nisl condimentum id venenatis a condimentum vitae. Nec sagittis aliquam malesuada bibendum arcu vitae elementum curabitur vitae. Viverra ipsum nunc aliquet bibendum enim. Ac auctor augue mauris augue neque gravida in fermentum. Sapien et ligula ullamcorper malesuada proin libero nunc consequat. Augue lacus viverra vitae congue eu consequat. Ornare suspendisse sed nisi lacus sed viverra tellus in hac. Proin fermentum leo vel orci porta non. In pellentesque massa placerat duis ultricies lacus sed turpis tincidunt. Orci dapibus ultrices in iaculis nunc sed augue lacus viverra. Ipsum nunc aliquet bibendum enim.\n" +
                "\n" +
                "Elementum pulvinar etiam non quam lacus suspendisse faucibus interdum posuere. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus. Tortor vitae purus faucibus ornare suspendisse. Libero volutpat sed cras ornare arcu dui vivamus arcu felis. Nulla porttitor massa id neque aliquam. Gravida cum sociis natoque penatibus et. Pellentesque elit ullamcorper dignissim cras tincidunt. Nibh venenatis cras sed felis eget velit aliquet sagittis. Porttitor massa id neque aliquam vestibulum morbi blandit cursus risus. Placerat in egestas erat imperdiet. Eget velit aliquet sagittis id. Ac turpis egestas maecenas pharetra convallis posuere morbi leo urna. Dignissim cras tincidunt lobortis feugiat vivamus at augue eget.\n" +
                "\n" +
                "Cursus in hac habitasse platea dictumst quisque sagittis. Eget nunc lobortis mattis aliquam faucibus purus in massa tempor. Elit ut aliquam purus sit amet luctus venenatis lectus. Blandit libero volutpat sed cras ornare arcu dui. Nibh sit amet commodo nulla facilisi nullam. Mauris commodo quis imperdiet massa tincidunt nunc. Sed libero enim sed faucibus turpis in eu mi. Eu sem integer vitae justo. Neque volutpat ac tincidunt vitae semper quis lectus. Phasellus faucibus scelerisque eleifend donec pretium vulputate sapien nec.\", \"RID\": 5, \"YTLink\": \"https://www.youtube.com/watch?v=202\", \"ingredients\": [{\"Ingredient\": \"Pizza Dough\", \"Amount\": \"1 ball\"}, {\"Ingredient\": \"Bell Peppers\", \"Amount\": \"1 cup\"}]}"
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