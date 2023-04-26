package com.electrodiux.network.packet;

public enum PacketType {

    EVENT(EventPacket.class),
    COMPRESSED(CompressedPacket.class),

    CLIENT_CONNECT(ClientConnectPacket.class),
    SERVER_CONNECT(Packet.class),
    CLIENT_DISCONNECT(DisconnectPacket.class);

    private Class<? extends Packet> packetClass;

    private PacketType(Class<? extends Packet> packetClass) {
        this.packetClass = packetClass;
    }

    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }
}
