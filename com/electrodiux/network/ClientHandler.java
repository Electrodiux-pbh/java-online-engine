package com.electrodiux.network;

import com.electrodiux.Player;

public interface ClientHandler extends Connection {

    public Player getPlayer();

    public void setPlayer(Player player);

}
