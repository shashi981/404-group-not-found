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
    private static final String DID_KEY = "did";


    //    ChatGPT Usage: Yes.
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

    //    ChatGPT Usage: Yes.
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

    //    ChatGPT Usage: TODO
    public static void saveDietitianData(Context context, DietitianData dietitianData) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FIRST_NAME_KEY, dietitianData.getFirstName());
        editor.putString(LAST_NAME_KEY, dietitianData.getLastName());
        editor.putString(EMAIL_KEY, dietitianData.getDietitianEmail());
        editor.putString(PROFILE_PICTURE_URI_KEY, dietitianData.getDietitianProfilePictureUrl().toString());
        editor.putInt(DID_KEY, dietitianData.getDID());
        editor.apply();
    }



    //    ChatGPT Usage: TODO
    public static DietitianData loadDietitianData(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        String firstName = sharedPref.getString(FIRST_NAME_KEY, "Default First Name");
        String lastName = sharedPref.getString(LAST_NAME_KEY, "Default Last Name");
        String email = sharedPref.getString(EMAIL_KEY, "Default Email");
        String profilePictureUriString = sharedPref.getString(PROFILE_PICTURE_URI_KEY, "Default Profile Picture Uri");
        int did = sharedPref.getInt(DID_KEY, -1);
        DietitianData dietitianData = new DietitianData(firstName, lastName, email, Uri.parse(profilePictureUriString), did);
        return dietitianData;
    }



}
