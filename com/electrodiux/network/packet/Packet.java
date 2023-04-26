package com.electrodiux.network.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    private PacketType packetType;

    public Packet(PacketType packetType) {
        if (!this.getClass().isAssignableFrom(packetType.getPacketClass())) {
            throw new IllegalArgumentException("Invalid packet type '" + packetType.getPacketClass() + "'");
        }

        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public <T extends Packet> T getCastPacket(Class<T> clazz) {
        if (packetType.getPacketClass().isAssignableFrom(clazz)) {
            try {
                return clazz.cast(this);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Packet compressPackets(Collection<? extends Packet> packets) {
        if (packets == null || packets.size() == 0)
            return null;

        List<Packet> pcks = new ArrayList<Packet>();
        for (Packet p : packets) {
            if (p == null)
                continue;

            if (p instanceof CompressedPacket compressedPacket)
                pcks.addAll(Arrays.asList(compressedPacket.getPackets()));
            else
                pcks.add(p);

        }
        if (pcks.size() == 0)
            return null;
        else if (pcks.size() == 1)
            return pcks.get(0);
        else
            return new CompressedPacket(pcks.toArray(new Packet[pcks.size()]));
    }

}
