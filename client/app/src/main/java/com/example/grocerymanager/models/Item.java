package com.example.grocerymanager.models;

public class Item {
    private String name;
    private String expiry;
    private int quantity;
    private int itemId;
    private String UPC;

    //    ChatGPT Usage: No. Although ChatGPT has generated such, this was created before that chat happened.
    public Item(String name, String expiry, int quantity, int itemId, String UPC) {
        this.name = name;
        this.expiry = expiry;
        this.quantity = quantity;
        this.itemId = itemId;
        this.UPC = UPC;
    }

    //    ChatGPT Usage: No.
    public String getName() {
        return name;
    }

    //    ChatGPT Usage: No.
    public void setName(String name) {
        this.name = name;
    }

    //    ChatGPT Usage: No.
    public String getExpiry() {
        return expiry;
    }

    //    ChatGPT Usage: No.
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    //    ChatGPT Usage: No.
    public int getQuantity() {
        return quantity;
    }

    //    ChatGPT Usage: No.
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //    ChatGPT Usage: No.
    public int getItemId(){return itemId;}

    //    ChatGPT Usage: No.
    public void setItemId(int itemId){
        this.itemId = itemId;
    }

    //    ChatGPT Usage: No.
    public String getUPC(){return UPC;}

    //    ChatGPT Usage: No.
    public void setUPC(String UPC){
        this.UPC = UPC;
    }
}
