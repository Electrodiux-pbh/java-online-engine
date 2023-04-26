package com.electrodiux.network;

import java.io.IOException;
import java.net.Socket;

import com.electrodiux.Client;
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
