package com.electrodiux.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import com.electrodiux.Player;
import com.electrodiux.World;
import com.electrodiux.WorldUpdater;
import com.electrodiux.network.packet.ClientConnectPacket;
import com.electrodiux.network.packet.CompressedPacket;
import com.electrodiux.network.packet.EventPacket;
import com.electrodiux.network.packet.Packet;

public class Server extends WorldUpdater {

    public static final int DEFAULT_SERVER_BACKLOG = 50;

    private ServerSocket serverSocket;
    private Thread serverSocketThread;
    private boolean isServerConnected = false;

    private Collection<ClientHandler> clients = new HashSet<>();

    public void start(World world) {
        this.world = world;
        super.start();
    }

    public int openServerSocket(int port) throws IOException {
        return openServerSocket(port, DEFAULT_SERVER_BACKLOG);
    }

    /**
     * @return the port that is connected to
     * 
     * @see java.net.ServerSocket.ServerSocket
     */
    public int openServerSocket(int port, int backlog) throws IOException {
        if (isServerConnected()) {
            throw new IllegalStateException("Cannot open server socket because it's already opened");
        }

        serverSocket = new ServerSocket(port, backlog);

        port = serverSocket.getLocalPort();

        System.out.println("Server started on port " + port);
        serverSocketThread = new Thread(this::runServerSocket, "Server Socket Fetching");
        serverSocketThread.start();
        isServerConnected = true;

        return port;
    }

    private void runServerSocket() {
        try {
            while (Thread.currentThread().isAlive()) {
                Socket socket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(this, socket);
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

    protected synchronized void updatePackets() {
        EventPacket globalEventPacket = null;
        if (world.getProcesedEventsQueue().size() > 0) {
            globalEventPacket = new EventPacket(world.getProcesedEventsQueue().toEventArray());
        }

        for (ClientHandler clientHandler : this.clients) {
            if (globalEventPacket != null) {
                clientHandler.sendPacket(globalEventPacket);
            }
        }

        dispatchPackets();
    }

    protected void update() {
    }

    public void registerClientHandler(ClientHandler clientHandler) {
        boolean connectionStarted = clientHandler.start();
        if (!connectionStarted) {
            return;
        }

        this.clients.add(clientHandler);
        System.out.println("Client connected from " + clientHandler.getConnectionOrigin());
    }

    public void unregisterClientHandler(ClientHandler clientHandler) {
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
        packet.setSourceToEvents(source);
        this.events.addAll(Arrays.asList(packet.getEvents()));
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

    public boolean isServerConnected() {
        return isServerConnected;
    }

    // #endregion

}
