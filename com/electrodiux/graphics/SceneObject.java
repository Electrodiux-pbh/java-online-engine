package com.electrodiux.graphics;

import org.joml.Matrix4f;

import com.electrodiux.math.MathUtils;
import com.electrodiux.math.Vector3;

public class SceneObject {

    private Vector3 position, rotation, scale;
    private Texture texture;
    private Model model;

    public SceneObject(Model model) {
        this(model, null, new Vector3(), new Vector3(), new Vector3(1, 1, 1));
    }

    public SceneObject(Model model, Texture texture) {
        this(model, texture, new Vector3(), new Vector3(), new Vector3(1, 1, 1));
    }

    public SceneObject(Model model, Texture texture, Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.model = model;
        this.texture = texture;
    }

    public Model getModel() {
        return model;
    }

    public void setScale(Vector3 scale) {
        this.scale.set(scale);
    }

    public Vector3 scale() {
        return scale;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation.set(rotation);
    }

    public Vector3 rotation() {
        return rotation;
    }

    public void setPosition(Vector3 transformation) {
        this.position.set(transformation);
    }

    public Vector3 position() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Matrix4f getTransformationMatrix() {
        return MathUtils.createTransformMatrix(position, rotation, scale);
    }

}
