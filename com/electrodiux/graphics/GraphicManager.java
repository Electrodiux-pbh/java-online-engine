package com.electrodiux.graphics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.electrodiux.Player;
import com.electrodiux.World;
import com.electrodiux.entity.Entity;
import com.electrodiux.math.Maths;
import com.electrodiux.math.Vector3;
import com.electrodiux.physics.BoxCollider;
import com.electrodiux.physics.Collider;
import com.electrodiux.physics.SphereCollider;
import com.electrodiux.terrain.Chunk;

public class GraphicManager implements Runnable {

    private Window window;

    private Camera camera;
    private Shader shader;

    private double lastTime = GLFW.glfwGetTime();

    private World world;
    public Player player;

    public GraphicManager(World simulation) {
        this.world = simulation;
    }

    public void run() {
        window = new Window(640, 360, "Online Game");

        GL.createCapabilities();

        Keyboard.configureKeyboard(window);
        GLFW.glfwSetInputMode(window.getWindowID(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        Mouse.configureMouse(window);

        load();

        GL11.glCullFace(GL11.GL_BACK);

        while (!GLFW.glfwWindowShouldClose(window.getWindowID())) {
            // calc deltatime
            double currentTime = GLFW.glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            // update and render
            GLFW.glfwPollEvents();
            update(deltaTime);
            render();
            DebugDraw.render(camera);

            // swapbuffers
            GLFW.glfwSwapBuffers(window.getWindowID());

            Mouse.endFrame();
        }

        Loader.clear();

        System.exit(0);
    }

    private float cameraDistance = 0.0f;
    public int cameraMode = 0;

    private List<SceneObject> sceneObjects = new ArrayList<SceneObject>();
    private Map<String, Texture> playerTextures = new HashMap<String, Texture>();

    private Model playerModel;
    private Model sphereModel, cubeModel;
    private Texture defaultPlayerTexture;

    private Chunk chunk;

    private void render() {

        // clear depth buffer and color buffer
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        camera.startFrame();

        camera.setProjectionsToShader(shader);

        for (SceneObject sceneObject : sceneObjects) {
            if (sceneObject != null)
                renderObject(sceneObject);
        }

        for (Entity entity : world.getEntitiesArray()) {
            if (DebugDraw.isActive())
                renderDebugEntity(entity);

            Collider collider = entity.getRigidBody().getCollider();
            if (collider instanceof SphereCollider sphere) {
                Matrix4f transformMatrix = Maths.createTransformMatrix(entity.position(), entity.rotation(),
                        Vector3.mul(Vector3.ONE, sphere.getRadius()));

                shader.setMatrix4f("transformMatrix", transformMatrix);

                GL30.glBindVertexArray(sphereModel.getVaoId());

                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);

                GL11.glDrawElements(GL11.GL_TRIANGLES, sphereModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                GL20.glDisableVertexAttribArray(0);
                GL20.glDisableVertexAttribArray(1);
            } else if (collider instanceof BoxCollider box) {
                Matrix4f transformMatrix = Maths.createTransformMatrix(entity.position().getAdded(box.getCenter()),
                        entity.rotation(), box.getSize());

                shader.setMatrix4f("transformMatrix", transformMatrix);

                GL30.glBindVertexArray(cubeModel.getVaoId());

                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);

                GL11.glDrawElements(GL11.GL_TRIANGLES, cubeModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                GL20.glDisableVertexAttribArray(0);
                GL20.glDisableVertexAttribArray(1);
            }
        }

        GL30.glBindVertexArray(playerModel.getVaoId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        for (Player player : world.getPlayers()) {
            if (cameraMode == 0 && player == this.player)
                continue;

            Texture texture = getPlayerTexture(player.getName());

            if (texture == null)
                texture = defaultPlayerTexture;
            // Texture texture = defaultPlayerTexture;

            Matrix4f transformMatrix = Maths.createTransformMatrix(player.position(),
                    new Vector3(0, -player.rotation().y(), 0), Vector3.ONE);

            shader.setMatrix4f("transformMatrix", transformMatrix);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            if (texture != null)
                texture.bind();

            GL11.glDrawElements(GL11.GL_TRIANGLES, playerModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            if (texture != null)
                texture.unbind();
        }

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);

        shader.detach();
        camera.endFrame();
    }

    private void renderDebugEntity(Entity entity) {
        DebugDraw.addLine(entity.position(), entity.position().getAdded(entity.getRigidBody().velocity()), Color.LIME);

        Collider playerCollider = player.getRigidBody().getCollider();

        Collider collider = entity.getRigidBody().getCollider();
        if (collider != null) {
            collider.calculateBoundingBox();

            /*
             * Color PURPLE if is player bounding box
             * Color BLUE if is a bounding box that is not touching player
             * Color RED if the boundingbox is touching player
             */

            Color color = collider == playerCollider ? Color.PURPLE
                    : (collider.checkAABBCollision(playerCollider.getBoundingBox()) ? Color.RED : Color.BLUE);

            DebugDraw.addAABB(collider.getBoundingBox(), color, 0.001f);
        }
    }

    private void renderObject(SceneObject object) {
        renderObject(object.getModel(), object.getTexture(), object.getTransformationMatrix());
    }

    private void renderObject(Model model, Texture texture, Matrix4f transformMatrix) {
        shader.setMatrix4f("transformMatrix", transformMatrix);

        GL30.glBindVertexArray(model.getVaoId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        if (texture != null)
            texture.bind();

        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        if (texture != null)
            texture.unbind();

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    private void update(float deltaTime) {
        cameraDistance = Maths.clamp(1, cameraDistance - Mouse.getScrollY(), 50);

        Vector3 pos = player.position();

        final float playerHeight = 1.8f;

        if (cameraMode == 0) {
            camera.position().set(pos.x(), pos.y() + playerHeight, pos.z());
            camera.rotation().set(player.rotation().x(), player.rotation().y(), 0);
        } else {
            float distance = cameraDistance;
            float rotationX = player.rotation().x();
            float rotationY = player.rotation().y();

            switch (cameraMode) {
                case 2:
                    distance = -cameraDistance;
                    rotationX = -rotationX;
                    rotationY += (float) Math.PI;
                case 1:
                    final float horizontalDistance = (float) (Math.cos(player.rotation().x()) * distance);

                    final float x = pos.x() + (float) (Math.sin(-player.rotation().y()) * horizontalDistance);
                    final float z = pos.z() + (float) (Math.cos(-player.rotation().y()) * horizontalDistance);
                    final float y = pos.y() + playerHeight + (float) (Math.sin(player.rotation().x()) * distance);

                    camera.position().set(x, y, z);
                    camera.rotation().set(rotationX, rotationY, 0);
            }
        }

    }

    private void load() {
        DebugDraw.load();

        camera = new Camera();
        camera.setBackgroundColor(Color.LIGHT_BLUE);
        camera.setAspectRatio(window.getWidth(), window.getHeight());
        cameraDistance = 10f;

        Texture texture = null;
        try {
            shader = Shader.loadShader("/assets/shaders/default.glsl");
            texture = Loader.loadTexture("/assets/grass_pixel.jpg", GL11.GL_NEAREST);
            defaultPlayerTexture = Loader.loadTexture("/assets/player.png", GL11.GL_NEAREST);
            playerModel = Loader.loadObjModel("/assets/player.obj");
            sphereModel = Loader.loadObjModel("/assets/sphere.obj");
            cubeModel = Loader.loadObjModel("/assets/cube.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        chunk = new Chunk(0, 0, texture);

        Model chunkModel = chunk.generateTerrain(new float[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE]);

        SceneObject floor = new SceneObject(chunkModel, texture);
        floor.position().set(0, 0, 0);
        sceneObjects.add(floor);
    }

    private Texture getPlayerTexture(String username) {
        if (playerTextures.containsKey(username)) {
            return playerTextures.get(username);
        }

        Texture texture = null;

        try {
            File skinFile = new File("assets/skins/" + username + ".png");

            if (skinFile.exists()) {
                return playerTextures.put(username,
                        Loader.loadFileTexture(skinFile.getAbsolutePath(), GL11.GL_NEAREST));
            } else {
                skinFile.getParentFile().mkdirs();
            }

            URL url = new URL("https://minecraft.tools/download-skin/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(skinFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            connection.disconnect();

            texture = Loader.loadFileTexture(skinFile.getAbsolutePath(), GL11.GL_NEAREST);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        playerTextures.put(username, texture);
        return texture;
    }

}
