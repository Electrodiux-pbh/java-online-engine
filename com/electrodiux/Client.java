package com.electrodiux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.lwjgl.glfw.GLFW;

import com.electrodiux.event.Event;
import com.electrodiux.event.EventQueue;
import com.electrodiux.event.MoveEvent;
import com.electrodiux.graphics.GraphicManager;
import com.electrodiux.graphics.Keyboard;
import com.electrodiux.graphics.Mouse;
import com.electrodiux.math.Vector3;
import com.electrodiux.network.Connection;
import com.electrodiux.network.packet.ClientConnectPacket;
import com.electrodiux.network.packet.CompressedPacket;
import com.electrodiux.network.packet.EventPacket;
import com.electrodiux.network.packet.Packet;
import com.electrodiux.util.Timer;

public class Client {

    private Connection connection;
    private Collection<Event> events = new ArrayList<>();

    private Player player;
    private World world;

    private Timer timer;

    private GraphicManager graphicManager;

    public void start(Connection connection, String username) {
        this.connection = connection;

        boolean connectionStarted = connection.start();
        if (!connectionStarted) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> connection.disconnect()));

        graphicManager = new GraphicManager(world);
        graphicManager.player = player;

        Thread renderThread = new Thread(graphicManager, "RenderThread");

        timer = new Timer(20);
        timer.setHandler(this::update);

        Time.startTime();
        timer.start();
        renderThread.start();
    }

    private void update() {
        // Update Time
        Time.updateTime();
        Time.setDeltaTime(timer.getDeltaTime());
        Time.setFixedDeltaTime(timer.getDeltaTime());

        Vector3 move = getMoveVector();
        Vector3 rotation = getRotationVector();

        if (Keyboard.isKeyTyped(GLFW.GLFW_KEY_F5)) {
            switch (graphicManager.cameraMode) {
                case 0:
                case 1:
                    graphicManager.cameraMode++;
                    break;
                default:
                    graphicManager.cameraMode = 0;
            }
        }

        MoveEvent moveEvent = new MoveEvent(player.getUUID(), move, rotation,
                Keyboard.isKeyTyped(GLFW.GLFW_KEY_SPACE),
                Keyboard.isKeyTyped(GLFW.GLFW_KEY_ENTER));

        // Sync update with render using Lock

        world.getEventsQueue().add(moveEvent);
        world.getProcesedEventsQueue().add(moveEvent);

        updatePackets();

        world.update(timer.getDeltaTime());

        EventQueue events = world.getProcesedEventsQueue();
        if (events.size() > 0) {
            connection.sendPacket(new EventPacket(world.getProcesedEventsQueue().toEventArray()));
        }
        world.getProcesedEventsQueue().clear();

        connection.dispatchPackets();

        Keyboard.endFrame();
        Time.increaseUpdateCount();
    }

    private Vector3 getMoveVector() {
        Vector3 move = new Vector3();

        final float cos = (float) Math.cos(player.rotation().y());
        final float sin = (float) Math.sin(player.rotation().y());

        if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_W))
            move.add(sin, 0, -cos);

        if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_S))
            move.add(-sin, 0, cos);

        if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_A))
            move.add(-cos, 0, -sin);

        if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_D))
            move.add(cos, 0, sin);

        return move.normalize();
    }

    private Vector3 getRotationVector() {
        final float value = 5 * Time.deltaTime();
        return new Vector3((float) Math.toRadians(-Mouse.getDY()) * value,
                (float) Math.toRadians(-Mouse.getDX()) * value, 0).getAdded(player.rotation());
    }

    private synchronized void updatePackets() {
        world.getEventsQueue().addAll(events);
        events.clear();
    }

    public Collection<Event> getEvents() {
        return events;
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
        getEvents().addAll(Arrays.asList(packet.getEvents()));
    }

    public void handleConnect(Connection connection, ClientConnectPacket packet) {
        if (player != null)
            return;

        world = new World(false);

        player = new Player(packet.getUUID(), packet.getName());
        world.addPlayer(player);
        world.addPlayers(Arrays.asList(packet.getPlayers()));
        world.addEntities(Arrays.asList(packet.getEntities()));
    }

    // #endregion

}
