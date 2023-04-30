package com.electrodiux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.electrodiux.entity.ColliderEntity;
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
import com.electrodiux.physics.SphereCollider;

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

        updateEvents();
    }

    private void updateEvents() {
        for (Iterator<Event> it = eventsQueue.getQueue().iterator(); it.hasNext();) {
            Event event = it.next();

            if (event instanceof MoveEvent ev) {
                processMoveEvent(ev);
                continue;
            }

            if (!isServer) {
                if (event instanceof PositionEvent ev) {
                    processPositionEvent(ev);
                    continue;
                }

                if (event instanceof PlayerConnectionEvent ev) {
                    processPlayerConnectionEvent(ev);
                    continue;
                }

                if (event instanceof SpawnEntityEvent spawnEvent) {
                    Entity entity = spawnEvent.getEntity();
                    if (entity instanceof Player)
                        continue;

                    addEntity(entity);
                    continue;
                }

                if (event instanceof DespawnEntityEvent spawnEvent) {
                    Entity entity = getEntity(spawnEvent.getEntityUUID());
                    if (entity instanceof Player)
                        continue;
                    removeEntity(entity);
                }
            }
        }

        eventsQueue.clear();
    }

    private void processMoveEvent(MoveEvent event) {
        Player player = getPlayer(event.getEntityUUID());

        if (player != null) {
            player.getRigidBody().addForce(event.getMoveVector().getNormalized().mul(20),
                    ForceMode.ACCELERATION);

            Vector3 rotation = event.getRotationVector();
            rotation.x(Maths.clamp((float) Math.PI / -2f, rotation.x(), (float) Math.PI / 2f));

            player.rotation().set(rotation);

            if (event.isJumping()) {
                player.getRigidBody().addForce(Vector3.UP.getMul(7), ForceMode.IMPULSE);
            }

            if (isServer) {
                if (event.isSpawning()) {
                    ColliderEntity entity = new ColliderEntity(UUID.randomUUID(), new Vector3(player.position()),
                            new SphereCollider(2.5f));
                    addEntity(entity);
                }
            }
        }
    }

    private void processPositionEvent(PositionEvent event) {
        Entity entity = getEntity(event.getEntityUUID());
        if (entity != null) {
            entity.position().set(event.getPosition());
        }
    }

    private void processPlayerConnectionEvent(PlayerConnectionEvent event) {
        switch (event.getConnectionType()) {
            case JOIN:
                if (!containsPlayer(event.getPlayerUUID())) {
                    Player player = event.getPlayer();
                    if (player != null) {
                        addPlayer(player);
                    }
                }
                break;
            case LEAVE:
                removePlayer(event.getPlayerUUID());
                break;
        }
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

    public Player getPlayer(UUID id) {
        if (entities.get(id) instanceof Player player)
            return player;
        return null;
    }

    public Entity getEntity(UUID id) {
        return entities.get(id);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public int getEntityCount() {
        return entities.size();
    }

}
