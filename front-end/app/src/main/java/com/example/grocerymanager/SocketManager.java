package com.example.grocerymanager; // Adjust this package name if needed

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class SocketManager {
    private Socket mSocket;
    private static SocketManager instance;

    private SocketManager() {
        try {
            mSocket = IO.socket("https://20.104.197.24:8081/"); // Replace with your server's URL
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return mSocket;
    }
}