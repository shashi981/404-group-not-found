package com.example.grocerymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class SharedPrefManager {

    private static final String SHARED_PREFS_KEY = "my_shared_prefs";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String EMAIL_KEY = "email";
    private static final String PROFILE_PICTURE_URI_KEY = "profile_picture_uri";
    private static final String UID_KEY = "uid";

    public static void saveUserData(Context context, UserData userData) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FIRST_NAME_KEY, userData.getFirstName());
        editor.putString(LAST_NAME_KEY, userData.getLastName());
        editor.putString(EMAIL_KEY, userData.getUserEmail());
        editor.putString(PROFILE_PICTURE_URI_KEY, userData.getUserProfilePictureUrl().toString());
        editor.putInt(UID_KEY, userData.getUID());
        editor.apply();
    }

    public static UserData loadUserData(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String firstName = sharedPref.getString(FIRST_NAME_KEY, "Default First Name");
        String lastName = sharedPref.getString(LAST_NAME_KEY, "Default Last Name");
        String email = sharedPref.getString(EMAIL_KEY, "Default Email");
        String profilePictureUriString = sharedPref.getString(PROFILE_PICTURE_URI_KEY, "Default Profile Picture Uri");
        int uid = sharedPref.getInt(UID_KEY, -1);
        UserData userData = new UserData(firstName, lastName, email, Uri.parse(profilePictureUriString), uid);
        return userData;
    }
}
