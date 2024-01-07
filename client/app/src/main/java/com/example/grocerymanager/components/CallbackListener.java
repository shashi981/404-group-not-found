package com.example.grocerymanager.components;

import org.json.JSONObject;

public interface CallbackListener {
    void onSuccess(JSONObject result);

    void onFailure(String errorMessage);
}
