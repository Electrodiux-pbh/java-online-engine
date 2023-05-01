package com.electrodiux.entity;

import java.util.UUID;

import com.electrodiux.graphics.Color;
import com.electrodiux.math.Vector3;
import com.electrodiux.physics.Collider;

public class ColliderEntity extends Entity {

    private Color color;

    public ColliderEntity(UUID uuid, Vector3 positon, Collider collider) {
        this(uuid, positon, collider, Color.randomColor());
    }

    public ColliderEntity(UUID uuid, Vector3 positon, Collider collider, Color color) {
        super(uuid, positon);
        getRigidBody().setCollider(collider);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
