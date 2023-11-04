package com.example.grocerymanager;

import java.sql.Timestamp;

public class ChatMessage {
    private String message;
    private int fromUserFlag; // 1 if from user, 0 if from dietician
    private int UID; // User's unique identifier
    private int DID; // Dietician's unique identifier

    //private Timestamp timestamp;

    public ChatMessage(String message, int fromUserFlag, int UID, int DID) {
        this.message = message;
        this.fromUserFlag = fromUserFlag;
        this.UID = UID;
        this.DID = DID;
    }

    public String getMessage() {
        return message;
    }

    public int getFromUserFlag() {
        return fromUserFlag;
    }

    public int getUID() {
        return UID;
    }

    public int getDID() {
        return DID;
    }

    public void setFromUserFlag(int fromUserFlag) {
        this.fromUserFlag = fromUserFlag;
    }

}

