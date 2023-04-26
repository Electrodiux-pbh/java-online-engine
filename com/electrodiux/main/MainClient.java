package com.electrodiux.main;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

import com.electrodiux.Client;
import com.electrodiux.Server;
import com.electrodiux.network.LocalServerConnection;
import com.electrodiux.network.ServerConnection;

public class MainClient {

    public static void main(String[] args) {
        String[] gameModes = new String[] { "Local", "Server" };

        String selectedGameMode = String.valueOf(JOptionPane.showInputDialog(null, "Select Mode", "Game Mode Selection",
                JOptionPane.DEFAULT_OPTION, null, gameModes, gameModes[1]));

        switch (selectedGameMode) {
            case "Local":
                localGame();
                break;
            case "Server":
                serverGame();
                break;
        }
    }

    private static void serverGame() {
        String username = getUsername();

        String ip = JOptionPane.showInputDialog("Introduce the server ip address");
        if (ip == null)
            return;

        if (ip.isBlank())
            ip = "localhost";

        System.out.println("Server IP: " + ip);

        Client client = new Client();

        try {
            ServerConnection connection = new ServerConnection(new Socket(ip, 5000), client, username);

            client.start(connection, username);
        } catch (SocketException e) {
            System.err.println("Connection lost with server caused by: " + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private static void localGame() {
        String username = getUsername();

        Client client = new Client();
        Server server = new Server();

        LocalServerConnection connection = new LocalServerConnection(client, server, null);

        client.start(connection, username);
    }

    private static String getUsername() {
        String username = JOptionPane.showInputDialog(
                "Introduce your username (If is a Minecraft username, it will be automatically use your skin)",
                "Electrodiux");

        if (username != null)
            System.out.println("Username: " + username);

        return username;
    }

}