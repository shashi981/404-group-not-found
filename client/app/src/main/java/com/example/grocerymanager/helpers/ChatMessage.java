package com.example.grocerymanager.helpers;

public class ChatMessage {
    private String message;
    private int fromUserFlag; // 1 if from user, 0 if from dietician
    private int UID; // User's unique identifier
    private int DID; // Dietician's unique identifier

    //private Timestamp timestamp;
    //CHAT GPT USAGE: NO
    public ChatMessage(String message, int fromUserFlag, int UID, int DID) {
        this.message = message;
        this.fromUserFlag = fromUserFlag;
        this.UID = UID;
        this.DID = DID;
    }

    //CHAT GPT USAGE: NO
    public String getMessage() {
        return message;
    }

    //CHAT GPT USAGE: NO
    public int getFromUserFlag() {
        return fromUserFlag;
    }

    //CHAT GPT USAGE: NO
    public int getUID() {
        return UID;
    }

    //CHAT GPT USAGE: NO
    public int getDID() {
        return DID;
    }

    //CHAT GPT USAGE: NO
    public void setFromUserFlag(int fromUserFlag) {
        this.fromUserFlag = fromUserFlag;
    }

}

