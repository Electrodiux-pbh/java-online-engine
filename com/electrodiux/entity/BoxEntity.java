package com.electrodiux.entity;

import java.util.UUID;

import com.electrodiux.math.Vector3;
import com.electrodiux.physics.BoxCollider;

public class BoxEntity extends Entity {

    public BoxEntity(UUID uuid, Vector3 positon, Vector3 size) {
        super(uuid, positon);
        getRigidBody().setCollider(new BoxCollider(new Vector3(), size));
    }

}
