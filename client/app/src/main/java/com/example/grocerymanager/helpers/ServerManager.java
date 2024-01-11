package com.example.grocerymanager.helpers;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.grocerymanager.models.ContactForm;
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
}