package com.electrodiux.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;

import com.electrodiux.network.packet.DisconnectPacket;
import com.electrodiux.network.packet.Packet;

public abstract class GenericConnection implements Connection {

    private static final int READING_THREAD_PRIORITY = 2;
    private static final int WRITING_THREAD_PRIORITY = 4;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Thread connectionThread;

    private Collection<Packet> packetBuffer;

    public GenericConnection(Socket socket) throws IOException {
        connectionThread = new Thread(this::run, "Connection");
        connectionThread.setPriority(READING_THREAD_PRIORITY);

        this.socket = socket;

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        this.packetBuffer = new HashSet<>();
    }

    private void run() {
        try {
            while (!socket.isClosed() && socket.isConnected()) {
                if (in.readObject() instanceof Packet packet) {
                    handlePacket(packet);
                } else {
                    throw new ClassCastException("The object received is not a Packet");
                }
            }
            return;
        } catch (ClassNotFoundException | ClassCastException e) {
            System.err.println("Client '" + socket.getInetAddress().getHostAddress()
                    + "' sent invalid data format");
        } catch (EOFException e) {
            System.err.println("Connection with '" + socket.getInetAddress().getHostAddress()
                    + "' closed unexpectedly");
            return;
        } catch (SocketException e) {
            System.err.println("Connection lost with '" + socket.getInetAddress().getHostAddress() + "' caused by: "
                    + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        disconnect();
    }

    @Override
    public void dispatchPackets() {
        if (packetBuffer.size() > 0 && !isClosed()) {
            Thread dispatchThread = new Thread(this::flushBufferedPackets, "Packet Dispatch");
            dispatchThread.setPriority(WRITING_THREAD_PRIORITY);
            dispatchThread.start();
        }
    }

    private synchronized void flushBufferedPackets() {
        Packet packet = Packet.compressPackets(packetBuffer);
        packetBuffer.clear();
        try {
            sendPacketImmediately(packet);
        } catch (IOException e) {
            System.err.println("Connection lost caused by: " + e.getMessage());
            disconnect();
        }
    }

    @Override
    public synchronized void sendPacket(Packet packet) {
        packetBuffer.add(packet);
    }

    @Override
    public boolean start() {
        connectionThread.start();
        return true;
    }

    protected abstract void handlePacket(Packet packet);

    @Override
    public synchronized void disconnect() {
        try {
            if (!isClosed()) {
                try {
                    sendPacketImmediately(new DisconnectPacket());
                } catch (IOException e) {
                }
                socket.close();
            }
        } catch (IOException e) {
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void sendPacketImmediately(Packet packet) throws IOException {
        try {
            out.writeObject(packet);
        } catch (IOException e) {
            throw new IOException("An error occurred while trying to send a packet to '" + getConnectionOrigin() + "'");
        }
    }

    @Override
    public String getConnectionOrigin() {
        return getSocket().getInetAddress().getHostAddress();
    }

    @Override
    public Packet awaitPacket() throws IOException {
        try {
            if (in.readObject() instanceof Packet packet) {
                return packet;
            }
        } catch (Exception e) {
            throw new IOException("An error ocurred while awaiting packet", e);
        }
        return null;
    }

}
