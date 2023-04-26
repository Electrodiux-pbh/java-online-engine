package com.electrodiux.terrain;

import org.joml.Vector3f;

import com.electrodiux.graphics.Loader;
import com.electrodiux.graphics.Model;
import com.electrodiux.graphics.Texture;

public class Chunk {

    public static final float TILE_SIZE = 2f;
    public static final int CHUNK_SIZE = 16;

    private int x, z;

    public Chunk(int chunkX, int chunkZ, Texture texture) {
        this.x = chunkX;
        this.z = chunkZ;
    }

    public Model generateTerrain(float[] heights) {
        final int count = CHUNK_SIZE * CHUNK_SIZE;

        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];

        int[] indices = new int[6 * (CHUNK_SIZE - 1) * (CHUNK_SIZE - 1)]; // 6 indices per square

        int vertexPointer = 0;
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                vertices[vertexPointer * 3] = j * TILE_SIZE;
                vertices[vertexPointer * 3 + 1] = heights[j + i * CHUNK_SIZE]; // use height value from heights array
                vertices[vertexPointer * 3 + 2] = i * TILE_SIZE;

                // calculate normals using neighboring height values
                float heightL = j == 0 ? heights[j + i * CHUNK_SIZE] : heights[(j - 1) + i * CHUNK_SIZE];
                float heightR = j == CHUNK_SIZE - 1 ? heights[j + i * CHUNK_SIZE] : heights[(j + 1) + i * CHUNK_SIZE];
                float heightD = i == 0 ? heights[j + i * CHUNK_SIZE] : heights[j + (i - 1) * CHUNK_SIZE];
                float heightU = i == CHUNK_SIZE - 1 ? heights[j + i * CHUNK_SIZE] : heights[j + (i + 1) * CHUNK_SIZE];
                Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU).normalize();

                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                textureCoords[vertexPointer * 2] = j;
                textureCoords[vertexPointer * 2 + 1] = i;

                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < CHUNK_SIZE - 1; gz++) {
            for (int gx = 0; gx < CHUNK_SIZE - 1; gx++) {
                int topLeft = (gz * CHUNK_SIZE) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * CHUNK_SIZE) + gx;
                int bottomRight = bottomLeft + 1;

                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        Model model = Loader.loadRawModel(vertices, indices, textureCoords);
        return model;
    }

    public int getChunkX() {
        return x;
    }

    public int getChunkZ() {
        return z;
    }

}
