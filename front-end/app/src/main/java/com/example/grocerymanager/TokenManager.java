package com.example.grocerymanager;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String SHARED_PREF_NAME = "token_shared_pref";
    private static final String TOKEN_KEY = "token";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveToken(String token) {
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }
}

