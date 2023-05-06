package com.electrodiux.network;

import java.util.Arrays;

import com.electrodiux.ClientBehaviour;
import com.electrodiux.LocalPlay;
import com.electrodiux.Player;
import com.electrodiux.World;
import com.electrodiux.event.EventQueue;
import com.electrodiux.network.packet.ClientConnectPacket;
import com.electrodiux.network.packet.CompressedPacket;
import com.electrodiux.network.packet.EventPacket;
import com.electrodiux.network.packet.Packet;

public class Client extends LocalPlay {

    private ServerConnection connection;

    private Player player;
    private ClientBehaviour client;

    public void start(ServerConnection connection, String username) {
        this.connection = connection;

        client = new ClientBehaviour();

        boolean connectionStarted = connection.start();
        if (!connectionStarted) {
            return;
        }

        client.start(world, player);

        super.start();
    }

    protected void update() {
        client.update();
    }

    protected synchronized void updatePackets() {
        EventQueue events = world.getProcesedEventsQueue();
        if (events.size() > 0) {
            connection.sendPacket(new EventPacket(world.getProcesedEventsQueue().toEventArray()));
        }
        connection.dispatchPackets();
    }

    // #region Packet handling

    public void handlePacket(Connection connection, Packet p) {
        switch (p.getPacketType()) {
            case CLIENT_DISCONNECT:
                connection.disconnect();
                break;
            case EVENT:
                registerEventPacket(p.getCastPacket(EventPacket.class));
                break;
            case COMPRESSED: {
                CompressedPacket packet = p.getCastPacket(CompressedPacket.class);
                for (Packet pk : packet.getPackets()) {
                    handlePacket(connection, pk);
                }
                break;
            }
            default:
                break;
        }
    }

    private synchronized void registerEventPacket(EventPacket packet) {
        packet.setSourceToEvents("server");
        this.events.addAll(Arrays.asList(packet.getEvents()));
    }

    public void handleConnect(Connection connection, ClientConnectPacket packet) {
        if (player != null)
            return;

        world = new World(false);

        player = new Player(packet.getUUID(), packet.getName());
        world.addPlayer(player);
        world.addPlayers(Arrays.asList(packet.getPlayers()));
        world.addEntities(Arrays.asList(packet.getEntities()));
        world.setChunk(packet.getChunk());
    }

    // #endregion

}
