package com.electrodiux;

import org.lwjgl.glfw.GLFW;

import com.electrodiux.event.MoveEvent;
import com.electrodiux.graphics.DebugDraw;
import com.electrodiux.graphics.GraphicManager;
import com.electrodiux.graphics.Keyboard;
import com.electrodiux.graphics.Mouse;
import com.electrodiux.math.Vector3;

public class ClientBehaviour {

    private World world;
    private Player player;

    private GraphicManager graphicManager;
    private Thread renderThread;

    public void start(World world, Player player) {
        this.world = world;

        this.player = player;
        world.addPlayer(player);

        graphicManager = new GraphicManager(world);
        graphicManager.player = player;

        renderThread = new Thread(graphicManager, "RenderThread");
        renderThread.start();
    }

    public void update() {
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

        if (Keyboard.isKeyTyped(GLFW.GLFW_KEY_F2)) {
            switch (graphicManager.renderMode) {
                case 0:
                    graphicManager.renderMode++;
                    break;
                default:
                    graphicManager.renderMode = 0;
            }
        }

        if (Keyboard.isKeyTyped(GLFW.GLFW_KEY_F1)) {
            DebugDraw.setActive(!DebugDraw.isActive());
        }

        MoveEvent moveEvent = new MoveEvent(player.getUUID(), move, rotation,
                Keyboard.isKeyTyped(GLFW.GLFW_KEY_SPACE),
                Keyboard.isKeyTyped(GLFW.GLFW_KEY_ENTER));

        world.getEventsQueue().add(moveEvent);
        world.getProcesedEventsQueue().add(moveEvent);

        Keyboard.endFrame();
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

}
