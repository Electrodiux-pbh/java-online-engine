package com.electrodiux.network.packet;

public class DisconnectPacket extends Packet {

    public DisconnectPacket() {
        super(PacketType.CLIENT_DISCONNECT);
    }

    private static final long serialVersionUID = 1L;

}
