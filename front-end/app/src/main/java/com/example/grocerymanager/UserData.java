package com.example.grocerymanager;

import android.net.Uri;

public class UserData {
    private String firstName;
    private String lastName;
    private String userEmail;
    private Uri userProfilePictureUrl;
    private int UID;

    public UserData(String firstName, String lastName, String userEmail, Uri userProfilePictureUrl, int UID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userProfilePictureUrl = userProfilePictureUrl;
        this.UID = UID;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

//    public void setUserEmail(String userEmail){
//        this.userEmail = userEmail;
//    }
    public String getUserEmail(){
        return userEmail;
    }
    public int getUID(){
        return UID;
    }
    public void setUserProfilePictureUrl(Uri userProfilePictureUrl){
        this.userProfilePictureUrl = userProfilePictureUrl;
    }
    public Uri getUserProfilePictureUrl() {
        return userProfilePictureUrl;
    }

}
