package com.electrodiux.main;

import java.io.IOException;
import java.net.SocketException;
import java.util.UUID;

import javax.swing.JOptionPane;

import com.electrodiux.LocalPlay;
import com.electrodiux.Player;
import com.electrodiux.World;
import com.electrodiux.network.Client;
import com.electrodiux.network.Server;
import com.electrodiux.network.ServerConnection;

public class Main {

    public static void main(String[] args) {
        String[] gameModes = new String[] { "Local Game", "Server Connection", "Server" };

        String selectedGameMode = String.valueOf(JOptionPane.showInputDialog(null, "Select Mode", "Game Mode Selection",
                JOptionPane.DEFAULT_OPTION, null, gameModes, gameModes[0]));

        switch (selectedGameMode) {
            case "Local Game":
                localGame();
                break;
            case "Server Connection":
                serverGame();
                break;
            case "Server":
                server();
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
            ServerConnection connection = ServerConnection.createConnection(ip, client, username);
            client.start(connection, username);
        } catch (SocketException e) {
            displayException(e, "Cannot connect with server");
            return;
        } catch (Exception e) {
            displayException(e);
            return;
        }
    }

    private static void localGame() {
        String username = getUsername();

        LocalPlay localPlay = new LocalPlay();

        World world = new World(true);
        Player player = new Player(UUID.randomUUID(), username);

        localPlay.start(world, player);
    }

    private static void server() {
        String portData = JOptionPane.showInputDialog("Introduce the server port", "5000");

        int port = 0;
        try {
            port = Integer.parseInt(portData);
        } catch (NumberFormatException e) {
        }

        Server server = new Server();
        try {
            server.openServerSocket(port, 50);
        } catch (IOException e) {
            displayException(e);
        }
        World world = new World(true);
        server.start(world);
    }

    private static String getUsername() {
        String username = JOptionPane.showInputDialog(
                "Introduce your username (If is a Minecraft username, it will be automatically use your skin)",
                "Electrodiux");

        if (username != null)
            System.out.println("Username: " + username);

        return username;
    }

    private static void displayException(Exception e) {
        displayException(e, e.getMessage());
    }

    private static void displayException(Exception e, String title) {
        System.err.println(title);
        JOptionPane.showMessageDialog(null, e, title, JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

}