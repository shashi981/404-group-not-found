package com.example.grocerymanager.misc;

import android.content.Context;
import android.util.Log;

import com.example.grocerymanager.components.CallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

public class BackendPathingMock {

    final static String TAG = "Pathing";

    public static void postRequest(String endpoint, JSONObject jsonData, Context context, final CallbackListener callbackListener) {
        JSONObject jsonAccount = new JSONObject();
        if(endpoint.equals("/get/users")){
            try {
                jsonAccount.put("UID", 1);
                jsonAccount.put("FirstName", "Mock");
                jsonAccount.put("LastName", "User");
                jsonAccount.put("Email", "MockUser@gmail.com");
                jsonAccount.put("ProfileURL", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());

            }
        }
        else if(endpoint.equals("/get/dietician")){
            try {
                jsonAccount.put("DID", 1);
                jsonAccount.put("FirstName", "Mock");
                jsonAccount.put("LastName", "Dietician");
                jsonAccount.put("Email", "MockDietician@gmail.com");
                jsonAccount.put("ProfileURL", "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());

            }
        }
        else{
            Log.e(TAG, "Error");
            callbackListener.onFailure("error");
        }

        callbackListener.onSuccess(jsonAccount);
    }

    public static void getRequest(String endpoint, Context context, final CallbackListener callbackListener) {
        JSONObject jsonAccountType = new JSONObject();
        if(endpoint.equals("/get/users_type?p1=MockUser@gmail.com")){
            try {
                jsonAccountType.put("Message", "User");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());

            }
        }
        else if(endpoint.equals("/get/users_type?p1=MockDietician@gmail.com")){
            try {
                jsonAccountType.put("Message", "Dietician");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());

            }
        }
        else if(endpoint.equals("/get/users_type?p1=MockAdmin@gmail.com")){
            try {
                jsonAccountType.put("Message", "Admin");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());

            }
        }
        else{
            try {
                jsonAccountType.put("Message", "Error");
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());
            }
        }
        callbackListener.onSuccess(jsonAccountType);
    }

}
