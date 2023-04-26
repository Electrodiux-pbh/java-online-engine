package com.electrodiux.entity;

import java.util.UUID;

import com.electrodiux.math.Vector3;
import com.electrodiux.physics.SphereCollider;

public class BallEntity extends Entity {

    private float radius;

    public BallEntity() {
        this(UUID.randomUUID(), new Vector3(), 1);
    }

    public BallEntity(UUID id, Vector3 position, float radius) {
        super(id, position);
        this.radius = radius;
        getRigidBody().setCollider(new SphereCollider(radius));
    }

    public float getRadius() {
        return radius;
    }

}
