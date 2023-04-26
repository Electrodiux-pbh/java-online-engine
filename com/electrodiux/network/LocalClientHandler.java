package com.electrodiux.network;

import com.electrodiux.Player;
import com.electrodiux.Server;
import com.electrodiux.network.packet.Packet;

public class LocalClientHandler extends GenericLocalConnection implements ClientHandler {

    private Server server;
    private Player player;

    public LocalClientHandler(Server server, LocalServerConnection out) {
        super(out);
        this.server = server;
    }

    protected void handlePacket(Packet p) {
        server.handlePacket(this, p);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

}
