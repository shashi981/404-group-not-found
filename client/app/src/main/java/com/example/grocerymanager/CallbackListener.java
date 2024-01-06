package com.example.grocerymanager;

import org.json.JSONObject;

interface CallbackListener {
    void onSuccess(JSONObject result);

    void onFailure(String errorMessage);
}
