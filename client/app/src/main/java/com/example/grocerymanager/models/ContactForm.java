package com.example.grocerymanager.models;

import android.net.Uri;

public class ContactForm {
    private String name;
    private String email;
    private String phoneNumber;
    private String subject;
    private String inquiry;
    private User user;

    private static ContactForm instance;

    public ContactForm(String name, String email, String phoneNumber, String subject, String inquiry, User user){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
        this.inquiry = inquiry;
        this.user = user;
    }

    public ContactForm(ContactForm other) {
        this.name = other.name;
        this.email = other.email;
        this.phoneNumber = other.phoneNumber;
        this.subject = other.subject;
        this.inquiry = other.inquiry;
        this.user = other.user;
    }

    public static synchronized ContactForm getInstance() {
        if (instance == null) {
            // User is not signed in, provide default values or handle accordingly
            instance = new ContactForm("","","","","", null);
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getSubject(){
        return subject;
    }


}