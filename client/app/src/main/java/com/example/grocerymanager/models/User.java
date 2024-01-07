package com.example.grocerymanager.models;

import java.net.URI;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private URI uri;
    // add token somewhere here, unsure yet.
    private String userType;
    private int ID;

    private static User instance;

    User(String firstName, String lastName, String email, URI uri){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uri = uri;
    }

    public static synchronized User getInstance() {
        if (instance == null) {
            // User is not signed in, provide default values or handle accordingly
            instance = new User("Default", "User", "default@example.com", null);
        }
        return instance;
    }

    public void setDetails(String firstName, String lastName, String email, URI uri) {
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

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getUserType() {
        return userType;
    }

    public int getID() {
        return ID;
    }
}
