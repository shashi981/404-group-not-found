package com.example.grocerymanager;

import android.net.Uri;

import java.net.URI;

public class GoogleAccountManager {
    private static String googleName;
    private static String googleEmail;
    private static Uri googleProfilePictureUrl;

    public static void setAccountInfo(String name, String userEmail, Uri pictureUrl) {
        googleName = name;
        googleEmail = userEmail;
        googleProfilePictureUrl = pictureUrl;
    }

    public static String getGoogleName() {
        return googleName;
    }

    public static String getGoogleEmail() {
        return googleEmail;
    }

    public static Uri getGoogleProfilePictureUrl() {
        return googleProfilePictureUrl;
    }

}
