package com.electrodiux.network;

import java.util.Collection;
import java.util.HashSet;

import com.electrodiux.network.packet.Packet;

public abstract class GenericLocalConnection implements Connection {

    private GenericLocalConnection out;

    private Collection<Packet> packetBuffer;

    public GenericLocalConnection() {
        this(null);
    }

    public GenericLocalConnection(GenericLocalConnection out) {
        this.out = out;
        this.packetBuffer = new HashSet<>();
    }

    private void receivePacket(Packet packet) {
        handlePacket(packet);
    }

    @Override
    public void dispatchPackets() {
        if (packetBuffer.size() > 0 && !isClosed()) {
            Packet packet = Packet.compressPackets(packetBuffer);
            packetBuffer.clear();
            out.receivePacket(packet);
        }
    }

    protected abstract void handlePacket(Packet packet);

    protected void setOutput(GenericLocalConnection out) {
        this.out = out;
    }

    @Override
    public void sendPacket(Packet packet) {
        packetBuffer.add(packet);
    }

    @Override
    public boolean isClosed() {
        return out == null;
    }

    @Override
    public boolean isConnected() {
        return out != null;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void disconnect() {
        out = null;
    }

    @Override
    public void sendPacketImmediately(Packet packet) {
        out.receivePacket(packet);
    }

    @Override
    public Packet awaitPacket() {
        return null;
    }

}
