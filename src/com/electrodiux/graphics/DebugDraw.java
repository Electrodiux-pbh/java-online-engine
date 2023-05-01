package com.electrodiux.graphics;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.electrodiux.Time;
import com.electrodiux.math.Vector3;
import com.electrodiux.physics.AABB;

public class DebugDraw {

    private static final int MAX_LINES = 1024;
    private static final int MAX_POINTS = 300;

    private static boolean loaded = false;
    private static boolean active = false;

    private static final List<Shape> shapes = new ArrayList<Shape>();
    private static final List<Point> points = new ArrayList<Point>();

    // 6 floats per point, 2 points per line
    private static RenderBatch linesBatch;
    private static RenderBatch pointsBatch;

    private static Shader debugShader;

    private static boolean ignoreDepthTest = false;

    public static void load() {
        try {
            debugShader = Shader.loadShader("/assets/shaders/debug.glsl");

            // Each line have two points
            linesBatch = new RenderBatch(MAX_LINES * 2);
            linesBatch.load();

            pointsBatch = new RenderBatch(MAX_POINTS);
            pointsBatch.load();

            loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void update() {
        for (Iterator<Shape> it = shapes.iterator(); it.hasNext();) {
            Shape shape = it.next();

            if (shape.canRemove()) {
                it.remove();
                continue;
            }

            shape.updateLifeTime(Time.deltaTime());
        }

        for (Iterator<Point> it = points.iterator(); it.hasNext();) {
            Point point = it.next();

            if (point.canRemove()) {
                it.remove();
                continue;
            }

            point.updateLifeTime(Time.deltaTime());
        }
    }

    public static void render(Camera camera) {
        if (!loaded) {
            return;
        }

        update();

        if (!active || shapes.size() <= 0) {
            return;
        }

        computeBuffer(linesBatch);
        computePointsBuffer(pointsBatch);

        if (ignoreDepthTest) {
            glDisable(GL_DEPTH_TEST);
        } else {
            glEnable(GL_DEPTH_TEST);
        }

        debugShader.use();
        camera.setProjectionsToShader(debugShader);

        glLineWidth(3.5f);
        linesBatch.render(GL_LINES, 2); // Two per line
        glPointSize(6f);
        pointsBatch.render(GL_POINTS, 1); // One per point

        debugShader.detach();
    }

    private static void computePointsBuffer(RenderBatch batch) {
        batch.count = 0;
        batch.vertexIndex = 0;
        for (Point point : points) {
            if (batch.count >= MAX_LINES) {
                return;
            }

            Vector3 pos = point.getPosition();
            Color color = point.getColor();

            batch.vertexArray[batch.vertexIndex + 0] = pos.x;
            batch.vertexArray[batch.vertexIndex + 1] = pos.y;
            batch.vertexArray[batch.vertexIndex + 2] = pos.z;
            batch.vertexArray[batch.vertexIndex + 3] = color.r();
            batch.vertexArray[batch.vertexIndex + 4] = color.g();
            batch.vertexArray[batch.vertexIndex + 5] = color.b();
            batch.vertexIndex += 6;

            batch.count++;
        }
    }

    private static void computeBuffer(RenderBatch batch) {
        batch.count = 0;
        batch.vertexIndex = 0;
        for (Shape shape : shapes) {
            if (batch.count >= MAX_LINES)
                break;

            Color color = shape.getColor();

            if (shape instanceof Line line) {
                Vector3 from = line.getFrom();
                Vector3 to = line.getTo();

                addLineToVertexBuffer(batch, from, to, color);
            } else if (shape instanceof AABBBox box) {
                AABB aabb = box.getAABB();

                Vector3 v1 = new Vector3(aabb.getMinX(), aabb.getMinY(), aabb.getMinZ());
                Vector3 v2 = new Vector3(aabb.getMaxX(), aabb.getMinY(), aabb.getMinZ());
                Vector3 v3 = new Vector3(aabb.getMaxX(), aabb.getMinY(), aabb.getMaxZ());
                Vector3 v4 = new Vector3(aabb.getMinX(), aabb.getMinY(), aabb.getMaxZ());
                Vector3 v5 = new Vector3(aabb.getMinX(), aabb.getMaxY(), aabb.getMinZ());
                Vector3 v6 = new Vector3(aabb.getMaxX(), aabb.getMaxY(), aabb.getMinZ());
                Vector3 v7 = new Vector3(aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ());
                Vector3 v8 = new Vector3(aabb.getMinX(), aabb.getMaxY(), aabb.getMaxZ());

                addLineToVertexBuffer(batch, v1, v2, color);
                addLineToVertexBuffer(batch, v2, v3, color);
                addLineToVertexBuffer(batch, v3, v4, color);
                addLineToVertexBuffer(batch, v4, v1, color);

                addLineToVertexBuffer(batch, v5, v6, color);
                addLineToVertexBuffer(batch, v6, v7, color);
                addLineToVertexBuffer(batch, v7, v8, color);
                addLineToVertexBuffer(batch, v8, v5, color);

                addLineToVertexBuffer(batch, v1, v5, color);
                addLineToVertexBuffer(batch, v2, v6, color);
                addLineToVertexBuffer(batch, v3, v7, color);
                addLineToVertexBuffer(batch, v4, v8, color);
            }
        }
    }

    private static void addLineToVertexBuffer(RenderBatch batch, Vector3 from, Vector3 to, Color color) {
        addLineToVertexBuffer(batch, from.x(), from.y(), from.z(), to.x(), to.y(), to.z(), color);
    }

    private static void addLineToVertexBuffer(RenderBatch batch, float fromx, float fromy, float fromz, float tox,
            float toy,
            float toz, Color color) {
        if (batch.count >= MAX_LINES) {
            return;
        }

        batch.vertexArray[batch.vertexIndex + 0] = fromx;
        batch.vertexArray[batch.vertexIndex + 1] = fromy;
        batch.vertexArray[batch.vertexIndex + 2] = fromz;
        batch.vertexArray[batch.vertexIndex + 3] = color.r();
        batch.vertexArray[batch.vertexIndex + 4] = color.g();
        batch.vertexArray[batch.vertexIndex + 5] = color.b();
        batch.vertexIndex += 6;

        batch.vertexArray[batch.vertexIndex + 0] = tox;
        batch.vertexArray[batch.vertexIndex + 1] = toy;
        batch.vertexArray[batch.vertexIndex + 2] = toz;
        batch.vertexArray[batch.vertexIndex + 3] = color.r();
        batch.vertexArray[batch.vertexIndex + 4] = color.g();
        batch.vertexArray[batch.vertexIndex + 5] = color.b();
        batch.vertexIndex += 6;

        batch.count++;
    }

    private static class RenderBatch {
        private float[] vertexArray;
        private int vertexIndex = 0;
        private int count = 0;
        private int vaoId, vboId;

        public RenderBatch(int maxCapacity) {
            vertexArray = new float[maxCapacity * 6];
        }

        public void load() {
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(0);
        }

        public void render(int renderMode, int elementsPerCount) {
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            float[] subData = Arrays.copyOfRange(vertexArray, 0, count * 6 * elementsPerCount);
            glBufferSubData(GL_ARRAY_BUFFER, 0, subData);

            glBindVertexArray(vaoId);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            glDrawArrays(renderMode, 0, subData.length);

            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
        }
    }

    public static void addLine(Vector3 from, Vector3 to) {
        addLine(from, to, Color.BLACK, 0.001f);
    }

    public static void addLine(Vector3 from, Vector3 to, Color color) {
        addLine(from, to, color, 0.001f);
    }

    public static void addLine(Vector3 from, Vector3 to, Color color, float lifeTime) {
        if (loaded) {
            shapes.add(new Line(from, to, lifeTime, color));
        }
    }

    public static void addAABB(AABB aabb, Color color, float lifeTime) {
        if (loaded) {
            shapes.add(new AABBBox(aabb, lifeTime, color));
        }
    }

    public static void addPoint(Vector3 position, Color color) {
        addPoint(position, color, 0.001f);
    }

    public static void addPoint(Vector3 position, Color color, float lifeTime) {
        if (loaded) {
            points.add(new Point(position, lifeTime, color));
        }
    }

    public static boolean isIgnoreDepthTest() {
        return ignoreDepthTest;
    }

    public static void setIgnoreDepthTest(boolean ignoreDepthTest) {
        DebugDraw.ignoreDepthTest = ignoreDepthTest;
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        DebugDraw.active = active;
    }

}

abstract class Shape {

    protected float lifeTime;
    protected boolean isInfinite;
    protected Color color;

    protected Shape(float lifeTime, Color color) {
        this.lifeTime = lifeTime;
        this.isInfinite = lifeTime == -1.0f;
        this.color = color;
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public void updateLifeTime(float deltaTime) {
        lifeTime -= deltaTime;
    }

    public boolean canRemove() {
        return lifeTime < 0 && !isInfinite;
    }

    public Color getColor() {
        return color;
    }

}

class Line extends Shape {

    private Vector3 from, to;

    public Line(Vector3 from, Vector3 to, float lifeTime, Color color) {
        super(lifeTime, color);
        this.from = from;
        this.to = to;
    }

    public Vector3 getFrom() {
        return from;
    }

    public Vector3 getTo() {
        return to;
    }

}

class AABBBox extends Shape {

    private AABB aabb;

    public AABBBox(AABB aabb, float lifeTime, Color color) {
        super(lifeTime, color);
        this.aabb = aabb;
    }

    public AABB getAABB() {
        return aabb;
    }

}

class Point extends Shape {

    private Vector3 position;

    protected Point(Vector3 position, float lifeTime, Color color) {
        super(lifeTime, color);
        this.position = position;
    }

    public Vector3 getPosition() {
        return position;
    }

}