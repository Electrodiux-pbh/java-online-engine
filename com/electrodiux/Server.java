package com.electrodiux;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import com.electrodiux.entity.BallEntity;
import com.electrodiux.event.Event;
import com.electrodiux.math.Vector3;
import com.electrodiux.network.ClientHandler;
import com.electrodiux.network.OnlineClientHandler;
import com.electrodiux.network.packet.ClientConnectPacket;
import com.electrodiux.network.packet.CompressedPacket;
import com.electrodiux.network.packet.EventPacket;
import com.electrodiux.network.packet.Packet;
import com.electrodiux.util.Timer;

public class Server {

    private ServerSocket serverSocket;
    private Thread serverSocketThread;

    private Collection<ClientHandler> clients = new HashSet<>();
    private Collection<Event> packets = new ArrayList<>();

    private World world;

    private Timer timer;

    public void start() {
        world = new World(true);

        BallEntity ball = new BallEntity(UUID.randomUUID(), new Vector3(15, 5, 15), 5f);
        ball.getRigidBody().mass(40f);
        ball.getRigidBody().setKinematic(false);
        world.addEntity(ball);

        timer = new Timer(20);
        timer.setHandler(this::update);
        timer.start();
    }

    public void openServerSocket() throws IOException {
        serverSocket = new ServerSocket(5000);
        System.out.println("Server started on port 5000");
        serverSocketThread = new Thread(this::runServerSocket, "Server Socket Fetching");
        serverSocketThread.start();
    }

    private void runServerSocket() {
        try {
            while (Thread.currentThread().isAlive()) {
                Socket socket = serverSocket.accept();

                OnlineClientHandler clientHandler = new OnlineClientHandler(this, socket);
                registerClientHandler(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void updatePackets() {
        world.getEventsQueue().addAll(packets);
        packets.clear();
    }

    private void update() {
        // Update Time
        Time.updateTime();
        Time.setDeltaTime(timer.getDeltaTime());
        Time.setFixedDeltaTime(timer.getDeltaTime());

        updatePackets();

        world.update(timer.getDeltaTime());

        EventPacket globalEventPacket = null;
        if (world.getProcesedEventsQueue().size() > 0) {
            globalEventPacket = new EventPacket(world.getProcesedEventsQueue().toEventArray());
        }

        for (ClientHandler clientHandler : this.clients) {
            if (globalEventPacket != null) {
                clientHandler.sendPacket(globalEventPacket);
            }

            Player player = clientHandler.getPlayer();

            if (player != null) {
                EventPacket eventPacket = new EventPacket(player.clearEvents());
                clientHandler.sendPacket(eventPacket);
            }
        }

        world.getProcesedEventsQueue().clear();

        dispatchPackets();

        // Update Time
        Time.increaseUpdateCount();
    }

    public void registerClientHandler(ClientHandler clientHandler) {
        boolean connectionStarted = clientHandler.start();
        if (!connectionStarted) {
            return;
        }

        this.clients.add(clientHandler);
        System.out.println("Client connected from " + clientHandler.getConnectionOrigin());
    }

    public void unregisterClientHandler(OnlineClientHandler clientHandler) {
        this.clients.remove(clientHandler);
        world.removePlayer(clientHandler.getPlayer());
    }

    private void dispatchPackets() {
        for (ClientHandler clientHandler : this.clients) {
            clientHandler.dispatchPackets();
        }
    }

    public World getWorld() {
        return world;
    }

    public void addClientHandler(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void removeClientHandler(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    // #region Packet handling

    public void handlePacket(ClientHandler clientHandler, Packet p) {
        switch (p.getPacketType()) {
            case CLIENT_DISCONNECT:
                clientHandler.disconnect();
                break;
            case EVENT: {
                registerEventPacket(p.getCastPacket(EventPacket.class), clientHandler.getPlayer());
                break;
            }
            case COMPRESSED: {
                CompressedPacket packet = p.getCastPacket(CompressedPacket.class);
                for (Packet pk : packet.getPackets()) {
                    handlePacket(clientHandler, pk);
                }
                break;
            }
            default:
                break;
        }
    }

    private synchronized void registerEventPacket(EventPacket packet, Player source) {
        Event[] events = packet.getEvents();

        for (int i = 0; i < events.length; i++) {
            Event e = events[i];
            if (e != null) {
                e.setSource(source);
            }
        }

        packets.addAll(Arrays.asList(events));
    }

    public ClientConnectPacket handleClientConnect(ClientHandler clientHandler, ClientConnectPacket packet) {
        if (clientHandler.getPlayer() != null) {
            return null;
        }

        Player player = getWorld().registerPlayer(packet.getName());
        clientHandler.setPlayer(player);

        ClientConnectPacket responsePacket = new ClientConnectPacket(player.getName(), player.getUUID(),
                getWorld().getPlayersArray(), getWorld().getEntitiesWithoutPlayers());

        return responsePacket;
    }

    // #endregion

}
