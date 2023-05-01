package com.electrodiux.network;

import java.io.IOException;
import java.net.Socket;

import com.electrodiux.Player;
import com.electrodiux.network.packet.ClientConnectPacket;
import com.electrodiux.network.packet.Packet;

public class ClientHandler extends GenericConnection {

    private Server server;
    private Player player;

    public ClientHandler(Server server, Socket socket) throws IOException {
        super(socket);
        this.server = server;
    }

    @Override
    public boolean start() {
        try {
            Packet p = awaitPacket();
            if (p instanceof ClientConnectPacket connectPacket) {
                ClientConnectPacket responsePacket = server.handleClientConnect(this, connectPacket);
                if (responsePacket == null)
                    return false;

                sendPacketImmediately(responsePacket);

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
        server.handlePacket(this, p);
    }

    @Override
    public void disconnect() {
        server.unregisterClientHandler(this);
        super.disconnect();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}