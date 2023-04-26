package com.electrodiux.network;

import com.electrodiux.Client;
import com.electrodiux.Server;
import com.electrodiux.network.packet.Packet;

public class LocalServerConnection extends GenericLocalConnection {

    private Client client;
    private String username;

    public LocalServerConnection(Client client, Server server, String username) {
        this.client = client;
        this.username = username;

        LocalClientHandler out = new LocalClientHandler(server, this);
        setOutput(out);

        server.addClientHandler(out);
    }

    @Override
    protected void handlePacket(Packet packet) {
        client.handlePacket(this, packet);
    }

}