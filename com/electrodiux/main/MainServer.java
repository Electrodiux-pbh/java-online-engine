package com.electrodiux.main;

import java.io.IOException;

import com.electrodiux.World;
import com.electrodiux.network.Server;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.openServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        World world = new World(true);
        server.start(world);
    }
}