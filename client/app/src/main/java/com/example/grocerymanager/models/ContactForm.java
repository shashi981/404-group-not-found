package com.example.grocerymanager.models;

import android.net.Uri;

public class ContactForm {
    private String name;
    private String email;
    private String phoneNumber;
    private String subject;
    private String concerns;
    private User user;

    private static ContactForm instance;

    public ContactForm(String name, String email, String phoneNumber, String subject, String concerns, User user){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
        this.concerns = concerns;
        this.user = user;
    }

    public ContactForm(ContactForm other) {
        this.name = other.name;
        this.email = other.email;
        this.phoneNumber = other.phoneNumber;
        this.subject = other.subject;
        this.concerns = other.concerns;
        this.user = other.user;
    }

    public static synchronized ContactForm getInstance() {
        if (instance == null) {
            instance = new ContactForm("","","","","", null);
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

    public void updateForm(String name, String email, String phoneNumber, String subject, String concerns, User user){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
        this.concerns = concerns;
        this.user = user;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getSubject(){
        return subject;
    }


}
