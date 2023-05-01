package com.electrodiux.network;

import java.io.IOException;
import java.net.Socket;

import com.electrodiux.network.packet.ClientConnectPacket;
import com.electrodiux.network.packet.Packet;

public class ServerConnection extends GenericConnection {

    private Client client;
    private String username;

    public ServerConnection(Socket socket, Client client, String username) throws IOException {
        super(socket);
        this.client = client;
        this.username = username;
    }

    public static ServerConnection createConnection(String ipAddress, Client client, String username)
            throws IOException {
        String[] ipParts = ipAddress.split(":");
        String hostName = ipParts[0];
        int port = 5000; // Default port is 5000
        if (ipParts.length > 1) {
            try {
                port = Integer.parseInt(ipParts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ip format", e);
            }
        }

        return new ServerConnection(new Socket(hostName, port), client, username);
    }

    @Override
    public boolean start() {
        try {
            sendPacketImmediately(new ClientConnectPacket(username));
            if (awaitPacket() instanceof ClientConnectPacket connectPacket) {
                client.handleConnect(this, connectPacket);
                return super.start();
            }
        } catch (IOException e) {
            System.err.println("An error occurred while trying to connect to the server");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void handlePacket(Packet p) {
        client.handlePacket(this, p);
    }

}
