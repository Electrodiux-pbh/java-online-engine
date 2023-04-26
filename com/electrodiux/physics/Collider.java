package com.electrodiux.physics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class Collider implements Serializable {

    private transient RigidBody rigidBody;
    private transient AABB boundingBox;

    public Collider() {
        initializeTransients();
    }

    public Collider(RigidBody body) {
        initializeTransients();
        this.rigidBody = body;
    }

    private void initializeTransients() {
        this.boundingBox = new AABB();
    }

    public boolean checkAABBCollision(AABB other) {
        return AABB.collides(this.boundingBox, other);
    }

    public abstract void calculateBoundingBox();

    public abstract CollisionResult collidesWith(Collider other);

    public AABB getBoundingBox() {

        return boundingBox;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    void setRigidBody(RigidBody body) {
        this.rigidBody = body;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeTransients();
    }

}
