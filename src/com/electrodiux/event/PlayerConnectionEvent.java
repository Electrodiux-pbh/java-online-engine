package com.electrodiux.event;

import java.io.Serial;

import com.electrodiux.Player;

public class PlayerConnectionEvent extends PlayerEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private ConnectionType connectionType;

    private Player player;

    public PlayerConnectionEvent(Player player, ConnectionType connectionType) {
        super(player);

        this.connectionType = connectionType;

        if (connectionType == ConnectionType.JOIN) {
            this.player = player;
        }
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public Player getPlayer() {
        return player;
    }

    public static enum ConnectionType {
        JOIN, LEAVE
    }

}
