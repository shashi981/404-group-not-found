package com.example.grocerymanager.helpers;

import android.content.Context;
import android.content.Intent;

public class ActivityLauncher {

    //    ChatGPT Usage: Partial
    public static void launchActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
