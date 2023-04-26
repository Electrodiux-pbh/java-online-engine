package com.electrodiux.network;

import java.io.IOException;

import com.electrodiux.network.packet.Packet;

public interface Connection {

    public static final String LOCAL_CONNECTION = "local_connection";

    public boolean start();

    public void disconnect();

    public void sendPacket(Packet packet);

    public void dispatchPackets();

    public void sendPacketImmediately(Packet packet) throws IOException;

    public boolean isClosed();

    public boolean isConnected();

    public Packet awaitPacket() throws IOException;

    public default String getConnectionOrigin() {
        return LOCAL_CONNECTION;
    }

}
