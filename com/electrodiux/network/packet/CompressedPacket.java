package com.electrodiux.network.packet;

public final class CompressedPacket extends Packet {

    private Packet[] packets;

    protected CompressedPacket(Packet[] packets) {
        super(PacketType.COMPRESSED);
        this.packets = packets;
    }

    public Packet[] getPackets() {
        return this.packets;
    }

}
