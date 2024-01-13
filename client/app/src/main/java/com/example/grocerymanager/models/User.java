package com.example.grocerymanager.models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private Uri uri;
    // add token somewhere here, unsure yet.
    private String userType;
    private int ID;
    private boolean nondairy;
    private boolean vegan;
    private boolean vegetarian;


    private static User instance;

    public User(String firstName, String lastName, String email, Uri uri){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uri = uri;
        this.nondairy = false;
        this.vegan = false;
        this.vegetarian = false;
    }

    public User(String firstName, String lastName, String email, Uri uri, String userType, int ID) {
        this(firstName, lastName, email, uri);
        this.userType = userType;
        this.ID = ID;
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

    public void setRestrictions(boolean nondairy, boolean vegan, boolean vegetarian){
        this.nondairy = nondairy;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
    }

    public void setNondairy(boolean nondairy) {
        this.nondairy = nondairy;
    }

    public boolean isNondairy() {
        return nondairy;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }
}
