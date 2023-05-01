package com.electrodiux;

import java.util.UUID;

import com.electrodiux.entity.Entity;
import com.electrodiux.physics.SphereCollider;

public class Player extends Entity {

    private String name;

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
        getRigidBody().setCollider(new SphereCollider(1));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
