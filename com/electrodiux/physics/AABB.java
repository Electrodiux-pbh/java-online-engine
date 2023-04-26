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

    public void recomputeBoundaries(Vector3 position, Vector3 size) {
        minX = position.x - size.x;
        maxX = position.x + size.x;

        minY = position.y - size.y;
        maxY = position.y + size.y;

        minZ = position.z - size.z;
        maxZ = position.z + size.z;
    }

    public void recomputeBoundaries(Vector3 position, float size) {
        minX = position.x - size;
        maxX = position.x + size;

        minY = position.y - size;
        maxY = position.y + size;

        minZ = position.z - size;
        maxZ = position.z + size;
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

}
