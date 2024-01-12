package com.example.grocerymanager.models;

import android.net.Uri;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private Uri uri;
    // add token somewhere here, unsure yet.
    private String userType;
    private int ID;

    private static User instance;

    public User(String firstName, String lastName, String email, Uri uri){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uri = uri;
    }

    public User(String firstName, String lastName, String email, Uri uri, String userType, int ID) {
        this(firstName, lastName, email, uri);
        this.userType = userType;
        this.ID = ID;
    }

    public User(User other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.uri = other.uri;
        this.userType = other.userType;
        this.ID = other.ID;
    }

    public static synchronized User getInstance() {
        if (instance == null) {
            // User is not signed in, provide default values or handle accordingly
            instance = new User("Default", "User", "default@example.com", null);
        }
        return instance;
    }

    public static boolean checkInstance(){
        if(instance == null){
            return false;
        }
        return true;
    }

    public static void clearInstance() {
        instance = null;
    }

    public void setDetails(String firstName, String lastName, String email, Uri uri) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uri = uri;
    }

    public void setDBInfo(String userType, int ID) {
        this.userType = userType;
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getUserType() {
        return userType;
    }


    public int getID() {
        return ID;
    }
}
