package com.example.grocerymanager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String SHARED_PREFS_KEY = "my_shared_prefs";
    private static final String NAME_KEY = "name";
    private static final String EMAIL_KEY = "email";
    private static final String PROFILE_PICTURE_URL_KEY = "profile_picture_url";

    public static void saveData(Context context, String name, String email, String profilePictureUrl) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_KEY, name);
        editor.putString(EMAIL_KEY, email);
        editor.putString(PROFILE_PICTURE_URL_KEY, profilePictureUrl);
        editor.apply();
    }

    public static UserData loadData(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String name = sharedPref.getString(NAME_KEY, "Default Name");
        String email = sharedPref.getString(EMAIL_KEY, "Default Email");
        String profilePictureUrl = sharedPref.getString(PROFILE_PICTURE_URL_KEY, "Default URL");
        return new UserData(name, email, profilePictureUrl);
    }
}

