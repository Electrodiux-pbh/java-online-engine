package com.electrodiux.physics;

import com.electrodiux.math.Vector3;

public class SlopeCollider extends Collider {

    private Vector3 pos1, pos2, pos3, pos4;

    public SlopeCollider(Vector3 pos1, Vector3 pos2, Vector3 pos3, Vector3 pos4) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
        this.pos4 = pos4;
    }

    @Override
    public void calculateBoundingBox() {
        AABB aabb = getBoundingBox();
        aabb.recomputeBoundaries(pos1, pos2, pos3, pos4);
    }

    @Override
    public CollisionResult collidesWith(Collider other) {
        if (other instanceof SphereCollider collider) {
            return sphereCollidesSlope(collider, this);
        }
        return CollisionResult.failed();
    }

    public static CollisionResult sphereCollidesSlope(SphereCollider sphereCollider, SlopeCollider slopeCollider) {
        return CollisionResult.failed();
    }

}
