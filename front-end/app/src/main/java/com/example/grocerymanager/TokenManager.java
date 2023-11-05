package com.example.grocerymanager;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String SHARED_PREF_NAME = "token_shared_pref";
    private static final String TOKEN_KEY = "token";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    //    ChatGPT Usage: No. Help from https://www.youtube.com/watch?v=aQ-v4gw9AnY&list=PLam6bY5NszYOhXkY7jOS4EQAKcQwkXrp4&index=2
    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //    ChatGPT Usage: No. Help from https://www.youtube.com/watch?v=aQ-v4gw9AnY&list=PLam6bY5NszYOhXkY7jOS4EQAKcQwkXrp4&index=2
    public void saveToken(String token) {
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    //    ChatGPT Usage: No. Help from https://www.youtube.com/watch?v=aQ-v4gw9AnY&list=PLam6bY5NszYOhXkY7jOS4EQAKcQwkXrp4&index=2
    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }
}

