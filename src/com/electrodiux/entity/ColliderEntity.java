package com.electrodiux.entity;

import java.util.UUID;

import com.electrodiux.math.Vector3;
import com.electrodiux.physics.Collider;

public class ColliderEntity extends Entity {

    public ColliderEntity(UUID uuid, Vector3 positon, Collider collider) {
        super(uuid, positon);
        getRigidBody().setCollider(collider);
    }

}
