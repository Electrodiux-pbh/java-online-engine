package com.electrodiux.math;

import org.joml.Matrix4f;

public final class Maths {

    private Maths() {
    }

    public static float clamp(float min, float value, float max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Matrix4f createTransformMatrix(Vector3 position, Vector3 rotation, Vector3 scale) {
        Matrix4f target = new Matrix4f();

        target.identity();

        target.translate(position.x(), position.y(), position.z());
        target.rotate(rotation.x(), 1, 0, 0);
        target.rotate(rotation.y(), 0, 1, 0);
        target.rotate(rotation.z(), 0, 0, 1);
        target.scale(scale.x(), scale.y(), scale.z());

        return target;
    }

    public static Matrix4f createTransformMatrix(Vector3 position, Vector3 rotation, float scale) {
        Matrix4f target = new Matrix4f();

        target.identity();

        target.translate(position.x(), position.y(), position.z());
        target.rotate(rotation.x(), 1, 0, 0);
        target.rotate(rotation.y(), 0, 1, 0);
        target.scale(scale, scale, scale);

        return target;
    }

}
