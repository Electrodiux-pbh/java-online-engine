package com.electrodiux;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import com.electrodiux.graphics.Keyboard;
import com.electrodiux.network.Server;

public class LocalPlay extends Server {

    private ClientBehaviour client;

    public void start(World world, Player player) {
        client = new ClientBehaviour();
        client.start(world, player);
        super.start(world);
    }

    @Override
    protected void update() {
        if (Keyboard.isKeyTyped(GLFW.GLFW_KEY_F8) && !isServerConnected()) {
            try {
                this.openServerSocket(5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        client.update();
        super.update();
    }

}
