package com.example.grocerymanager;

interface UserTypeCallback {
    void onUserTypeReceived(String userType);

    void onFailure(String errorMessage);
}
