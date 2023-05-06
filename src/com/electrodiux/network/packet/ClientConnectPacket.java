package com.electrodiux.network.packet;

import java.util.UUID;

import com.electrodiux.Player;
import com.electrodiux.entity.Entity;
import com.electrodiux.terrain.Chunk;

public class ClientConnectPacket extends Packet {

    private static final long serialVersionUID = 1L;

    private String name;
    private UUID id;

    private Entity[] entities;
    private Player[] players;

    private Chunk chunk;

    public ClientConnectPacket(String name) {
        super(PacketType.CLIENT_CONNECT);
        this.name = name;
    }

    public ClientConnectPacket(String name, UUID id, Player[] players, Entity[] entities, Chunk chunk) {
        super(PacketType.CLIENT_CONNECT);
        this.name = name;
        this.id = id;
        this.players = players;
        this.entities = entities;
        this.chunk = chunk;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return id;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Entity[] getEntities() {
        return entities;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
