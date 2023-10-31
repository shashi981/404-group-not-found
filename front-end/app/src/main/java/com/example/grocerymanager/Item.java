package com.example.grocerymanager;

public class Item {
    private String name;
    private String expiry;
    private int quantity;

    public Item(String name, String expiry, int quantity) {
        this.name = name;
        this.expiry = expiry;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
