package com.electrodiux.terrain;

import java.io.Serializable;

import com.electrodiux.math.MathUtils;
import com.electrodiux.math.Vector2;
import com.electrodiux.math.Vector3;

public class Chunk implements Serializable {

    public static final int CHUNK_SIZE = 64 * 10;

    private int chunkX, chunkZ;
    private float[] heights;

    public Chunk(int chunkX, int chunkZ, float[] heights) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.heights = heights;
    }

    public float getHeightAt(float worldX, float worldZ) {
        float chunkX = worldX - this.chunkX;
        float chunkZ = worldZ - this.chunkZ;

        float gridSquareSize = 1;

        int gridX = (int) Math.floor(chunkX);
        int gridZ = (int) Math.floor(chunkZ);

        if (gridX >= CHUNK_SIZE || gridZ >= CHUNK_SIZE || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (chunkX % gridSquareSize) / gridSquareSize;
        float zCoord = (chunkZ % gridSquareSize) / gridSquareSize;

        float height;

        if (xCoord <= (1 - zCoord)) {
            height = MathUtils.barryCentric(new Vector3(0, getHeight(gridX, gridZ), 0),
                    new Vector3(1, getHeight(gridX + 1, gridZ), 0),
                    new Vector3(0, getHeight(gridX, gridZ + 1), 1),
                    new Vector2(xCoord, zCoord));
        } else {
            height = MathUtils.barryCentric(new Vector3(1, getHeight(gridX + 1, gridZ), 0),
                    new Vector3(1, getHeight(gridX + 1, gridZ + 1), 1),
                    new Vector3(0, getHeight(gridX, gridZ + 1), 1),
                    new Vector2(xCoord, zCoord));
        }

        return height;
    }

    public Vector3 getSlopeAt(float worldX, float worldZ) {
        float h1 = getHeightAt(worldX, worldZ);
        float h2 = getHeightAt(worldX + 1, worldZ);
        float h3 = getHeightAt(worldX, worldZ + 1);
        return new Vector3(h1 - h2, 0, h1 - h3);
    }

    public float getHeight(int x, int z) {
        if (x < 0 || x >= CHUNK_SIZE || z < 0 || z >= CHUNK_SIZE)
            return 0;
        return heights[x + z * CHUNK_SIZE];
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public float[] getHeights() {
        return heights;
    }

}
