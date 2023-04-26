package com.electrodiux.main;

import java.io.IOException;

import com.electrodiux.Server;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.openServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
    }
}