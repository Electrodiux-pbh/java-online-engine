package com.electrodiux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.electrodiux.entity.BallEntity;
import com.electrodiux.entity.Entity;
import com.electrodiux.event.DespawnEntityEvent;
import com.electrodiux.event.Event;
import com.electrodiux.event.EventQueue;
import com.electrodiux.event.MoveEvent;
import com.electrodiux.event.PlayerConnectionEvent;
import com.electrodiux.event.PositionEvent;
import com.electrodiux.event.SpawnEntityEvent;
import com.electrodiux.math.Maths;
import com.electrodiux.math.Vector3;
import com.electrodiux.physics.ForceMode;
import com.electrodiux.physics.PhysicsSystem;

public class World {

    private PhysicsSystem physics;

    private EventQueue eventsQueue;
    private EventQueue events;

    private Collection<Player> players = new ArrayList<Player>();
    private Map<UUID, Entity> entities = new HashMap<UUID, Entity>();

    private boolean isServer;

    public World(boolean isServer) {
        this.isServer = isServer;

        physics = new PhysicsSystem(new Vector3(0.0f, -9.81f, 0.0f));

        eventsQueue = new EventQueue();
        events = new EventQueue();
    }

    public EventQueue getEventsQueue() {
        return eventsQueue;
    }

    public EventQueue getProcesedEventsQueue() {
        return events;
    }

    public synchronized void update(float deltaTime) {
        physics.updateSimulation();

        if (isServer) {
            for (Entity entity : getEntities()) {
                sendEventToAll(new PositionEvent(entity));
            }
        }

        for (Iterator<Event> it = eventsQueue.getQueue().iterator(); it.hasNext();) {
            Event event = it.next();

            if (event instanceof MoveEvent moveEvent) {
                Player player = getPlayer(moveEvent.getEntityUUID());

                if (player != null) {
                    player.getRigidBody().addForce(moveEvent.getMoveVector().getNormalized().mul(20),
                            ForceMode.ACCELERATION);

                    Vector3 rotation = moveEvent.getRotationVector();
                    rotation.x(Maths.clamp((float) Math.PI / -2f, rotation.x(), (float) Math.PI / 2f));

                    player.rotation().set(rotation);

                    if (moveEvent.isJumping()) {
                        player.getRigidBody().addForce(Vector3.UP.getMul(7), ForceMode.IMPULSE);
                    }

                    if (isServer) {
                        if (moveEvent.isSpawning()) {
                            BallEntity entity = new BallEntity(UUID.randomUUID(), new Vector3(player.position()), 2.5f);

                            addEntity(entity);
                        }
                    }
                }
            }

            if (!isServer) {
                if (event instanceof PositionEvent positionEvent) {
                    Entity entity = getEntity(positionEvent.getEntityUUID());
                    if (entity != null) {
                        entity.position().set(positionEvent.getPosition());
                    }
                }

                if (event instanceof PlayerConnectionEvent connectionEvent) {
                    switch (connectionEvent.getConnectionType()) {
                        case JOIN:
                            if (!containsPlayer(connectionEvent.getPlayerUUID())) {
                                Player player = connectionEvent.getPlayer();
                                if (player != null) {
                                    addPlayer(player);
                                }
                            }
                            break;
                        case LEAVE:
                            removePlayer(connectionEvent.getPlayerUUID());
                            break;
                    }
                }

                if (event instanceof SpawnEntityEvent spawnEvent) {
                    Entity entity = spawnEvent.getEntity();
                    addEntity(entity);
                }

                if (event instanceof DespawnEntityEvent spawnEvent) {
                    removeEntity(spawnEvent.getEntityUUID());
                }
            }
        }

        eventsQueue.clear();
    }

    private void sendEventToAll(Event event) {
        events.add(event);
    }

    // private void sendEventToAllExcept(Event event, Player exception) {
    // for (Player player : getPlayers()) {
    // if (player == exception)
    // continue;
    // player.addEvent(event);
    // }
    // }

    public Player registerPlayer(String name) {
        Player player = new Player(UUID.randomUUID(), name);

        addPlayer(player);

        return player;
    }

    public boolean addPlayer(Player player) {
        if (player == null)
            return false;

        if (!entities.containsKey(player.getUUID())) {
            players.add(player);

            addEntity(player);

            events.add(new PlayerConnectionEvent(player, PlayerConnectionEvent.ConnectionType.JOIN));
            System.out.println(player.getName() + " joined game");
            return true;
        }
        return false;
    }

    public void addEntity(Entity entity) {
        if (entity == null)
            return;

        physics.addRigidBody(entity.getRigidBody());

        if (isServer)
            events.add(new SpawnEntityEvent(entity));

        entities.put(entity.getUUID(), entity);
    }

    public void addPlayers(Collection<Player> players) {
        for (Player player : players) {
            addPlayer(player);
        }
    }

    public void addEntities(Collection<Entity> entities) {
        for (Entity entity : entities) {
            addEntity(entity);
        }
    }

    public void removePlayer(Player player) {
        if (player == null)
            return;
        removePlayer(player.getUUID());
    }

    public void removeEntity(Entity entity) {
        if (entity == null)
            return;
        removeEntity(entity.getUUID());
    }

    public Player removePlayer(UUID playerId) {
        if (removeEntity(playerId) instanceof Player player) {
            players.remove(player);

            events.add(new PlayerConnectionEvent(player, PlayerConnectionEvent.ConnectionType.LEAVE));
            System.out.println(player.getName() + " left the game");

            return player;
        }
        return null;
    }

    public Entity removeEntity(UUID entityId) {
        Entity entity = entities.remove(entityId);
        if (entity == null)
            return null;

        physics.removeRigidBody(entity.getRigidBody());

        if (isServer)
            events.add(new DespawnEntityEvent(entity));

        return entity;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public Collection<Entity> getEntities() {
        return entities.values();
    }

    public Player[] getPlayersArray() {
        return players.toArray(Player[]::new);
    }

    public Entity[] getEntitiesArray() {
        Collection<Entity> entities = this.entities.values();
        return entities.toArray(Entity[]::new);
    }

    public Entity[] getEntitiesWithoutPlayers() {
        Entity[] entities = getEntities().stream()
                .filter(entity -> !(entity instanceof Player))
                .toArray(Entity[]::new);

        return entities;
    }

    public boolean containsPlayer(Player player) {
        return containsPlayer(player.getUUID());
    }

    public boolean containsPlayer(UUID player) {
        return entities.containsKey(player);
    }

    public Player getPlayer(UUID id) {
        if (entities.get(id) instanceof Player player)
            return player;
        return null;
    }

    public Entity getEntity(UUID id) {
        return entities.get(id);
    }

}
