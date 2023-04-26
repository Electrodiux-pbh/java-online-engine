package com.electrodiux.entity;

import java.io.Serializable;
import java.util.UUID;

import com.electrodiux.math.Vector3;
import com.electrodiux.physics.RigidBody;

public abstract class Entity implements Serializable {

    private UUID id;

    private RigidBody rigidBody;

    public Entity() {
        // This constructor is required by Serializable
    }

    public Entity(UUID id) {
        this(id, new Vector3(0, 0, 0));
    }

    public Entity(UUID id, Vector3 position) {
        this.id = id;
        this.rigidBody = new RigidBody();
        rigidBody.mass(1);
        rigidBody.position(position);
    }

    public UUID getUUID() {
        return id;
    }

    public Vector3 position() {
        return rigidBody.position();
    }

    public void position(Vector3 position) {
        this.rigidBody.position(position);
    }

    public Vector3 rotation() {
        return rigidBody.rotation();
    }

    public void rotation(Vector3 rotation) {
        this.rigidBody.rotation(rotation);
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }
}
