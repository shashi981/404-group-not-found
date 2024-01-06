package com.example.grocerymanager;

import android.net.Uri;

public class UserData {
    private String firstName;
    private String lastName;
    private String userEmail;
    private Uri userProfilePictureUrl;
    private int UID;

    //    ChatGPT Usage: No. Although a prompt came up in ChatGPT, this class was entirely created by us.
    public UserData(String firstName, String lastName, String userEmail, Uri userProfilePictureUrl, int UID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userProfilePictureUrl = userProfilePictureUrl;
        this.UID = UID;
    }

    //    ChatGPT Usage: No.
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    //    ChatGPT Usage: No.
    public String getFirstName() {
        return firstName;
    }

    //    ChatGPT Usage: No.
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    //    ChatGPT Usage: No.
    public String getLastName() {
        return lastName;
    }

    //    ChatGPT Usage: No.
    public String getUserEmail(){
        return userEmail;
    }

    //    ChatGPT Usage: No.
    public int getUID(){
        return UID;
    }

    //    ChatGPT Usage: No.
    public void setUserProfilePictureUrl(Uri userProfilePictureUrl){
        this.userProfilePictureUrl = userProfilePictureUrl;
    }

    //    ChatGPT Usage: No.
    public Uri getUserProfilePictureUrl() {
        return userProfilePictureUrl;
    }

}
