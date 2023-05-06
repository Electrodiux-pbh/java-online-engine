package com.electrodiux.physics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.electrodiux.math.Vector3;

public final class AABB implements Serializable {

    private float minX, maxX, minY, maxY, minZ, maxZ;

    public boolean collides(AABB other) {
        return AABB.collides(this, other);
    }

    public void recomputeBoundaries(ArrayList<Vector3> vertices) {
        minX = Float.MAX_VALUE;
        maxX = -Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = -Float.MIN_VALUE;
        minZ = Float.MAX_VALUE;
        maxZ = -Float.MIN_VALUE;

        for (Vector3 vertex : vertices) {
            if (vertex.x < minX)
                minX = vertex.x;
            if (vertex.y < minY)
                minY = vertex.y;
            if (vertex.z < minZ)
                minZ = vertex.z;

            if (vertex.x > maxX)
                maxX = vertex.x;
            if (vertex.y > maxY)
                maxY = vertex.y;
            if (vertex.z > maxZ)
                maxZ = vertex.z;
        }
    }

    public void recomputeBoundaries(Vector3... vertices) {
        minX = Float.MAX_VALUE;
        maxX = -Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = -Float.MIN_VALUE;
        minZ = Float.MAX_VALUE;
        maxZ = -Float.MIN_VALUE;

        for (Vector3 vertex : vertices) {
            if (vertex.x < minX)
                minX = vertex.x;
            if (vertex.y < minY)
                minY = vertex.y;
            if (vertex.z < minZ)
                minZ = vertex.z;

            if (vertex.x > maxX)
                maxX = vertex.x;
            if (vertex.y > maxY)
                maxY = vertex.y;
            if (vertex.z > maxZ)
                maxZ = vertex.z;
        }
    }

    public void recomputeBoundaries(Vector3 position, Vector3 size) {
        float xSize = size.x / 2.0f;
        minX = position.x - xSize;
        maxX = position.x + xSize;

        float ySize = size.y / 2.0f;
        minY = position.y - ySize;
        maxY = position.y + ySize;

        float zSize = size.z / 2.0f;
        minZ = position.z - zSize;
        maxZ = position.z + zSize;
    }

    public void recomputeBoundariesWithHalfSize(Vector3 position, Vector3 halfSize) {
        minX = position.x - halfSize.x;
        maxX = position.x + halfSize.x;

        minY = position.y - halfSize.y;
        maxY = position.y + halfSize.y;

        minZ = position.z - halfSize.z;
        maxZ = position.z + halfSize.z;
    }

    public void recomputeBoundaries(Vector3 position, float size) {
        this.recomputeBoundariesWithHalfSize(position, size / 2.0f);
    }

    public void recomputeBoundariesWithHalfSize(Vector3 position, float halfSize) {
        minX = position.x - halfSize;
        maxX = position.x + halfSize;

        minY = position.y - halfSize;
        maxY = position.y + halfSize;

        minZ = position.z - halfSize;
        maxZ = position.z + halfSize;
    }

    public boolean isPointInside(Vector3 point) {
        return (point.x >= minX &&
                point.x <= maxX &&
                point.y >= minY &&
                point.y <= maxY &&
                point.z >= minZ &&
                point.z <= maxZ);
    }

    public static boolean collides(AABB a, AABB b) {
        // check for overlap along all three axes
        if (a == null || b == null)
            return false;

        return (a.maxX >= b.minX && a.minX <= b.maxX &&
                a.maxY >= b.minY && a.minY <= b.maxY &&
                a.maxZ >= b.minZ && a.minZ <= b.maxZ);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(minX);
        result = prime * result + Float.floatToIntBits(maxX);
        result = prime * result + Float.floatToIntBits(minY);
        result = prime * result + Float.floatToIntBits(maxY);
        result = prime * result + Float.floatToIntBits(minZ);
        result = prime * result + Float.floatToIntBits(maxZ);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof AABB aabb) {
            return equals(obj);
        }
        return false;
    }

    public boolean equals(AABB other) {
        if (other == null)
            return false;
        if (Float.floatToIntBits(minX) != Float.floatToIntBits(other.minX))
            return false;
        if (Float.floatToIntBits(maxX) != Float.floatToIntBits(other.maxX))
            return false;
        if (Float.floatToIntBits(minY) != Float.floatToIntBits(other.minY))
            return false;
        if (Float.floatToIntBits(maxY) != Float.floatToIntBits(other.maxY))
            return false;
        if (Float.floatToIntBits(minZ) != Float.floatToIntBits(other.minZ))
            return false;
        if (Float.floatToIntBits(maxZ) != Float.floatToIntBits(other.maxZ))
            return false;
        return true;
    }

    // #region Serialization

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeFloat(minX);
        out.writeFloat(maxX);

        out.writeFloat(minY);
        out.writeFloat(maxY);

        out.writeFloat(minZ);
        out.writeFloat(maxZ);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        minX = in.readFloat();
        maxX = in.readFloat();

        minY = in.readFloat();
        maxY = in.readFloat();

        minZ = in.readFloat();
        maxZ = in.readFloat();
    }

    // #endregion

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public float getMinZ() {
        return minZ;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }

    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }

}
