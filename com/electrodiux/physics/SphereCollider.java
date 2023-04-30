package com.electrodiux.physics;

import com.electrodiux.math.Vector3;

public class SphereCollider extends Collider {

    private float radius;

    public SphereCollider(float radius) {
        this.radius = radius;
    }

    public SphereCollider(RigidBody body, float radius) {
        super(body);
        this.radius = radius;
    }

    @Override
    public CollisionResult collidesWith(Collider other) {
        if (other instanceof SphereCollider collider) {
            return SphereCollider.sphereCollisions(this, collider);
        } else if (other instanceof BoxCollider collider) {
            return BoxCollider.boxSphereCollision(collider, this);
        }
        return CollisionResult.failed();
    }

    public static CollisionResult sphereCollisions(SphereCollider a, SphereCollider b) {
        RigidBody body1 = a.getRigidBody();
        RigidBody body2 = b.getRigidBody();

        if (body1 == null || body2 == null)
            return CollisionResult.failed();

        Vector3 body1MassCenter = body1.globalCenterOfMass();
        Vector3 body2MassCenter = body2.globalCenterOfMass();

        float dx = body1MassCenter.x - body2MassCenter.x;
        float dy = body1MassCenter.y - body2MassCenter.y;
        float dz = body1MassCenter.z - body2MassCenter.z;

        float centerDistance = dx * dx + dy * dy + dz * dz;
        float radiusSum = a.getRadius() + b.getRadius();

        if (centerDistance < radiusSum * radiusSum) {
            centerDistance = (float) Math.sqrt(centerDistance);

            Vector3 mdt = body1MassCenter.getSubstract(body2MassCenter);
            mdt.magnitude(radiusSum - centerDistance);

            return CollisionResult.successCollision(mdt, body1, body1MassCenter, body2, body2MassCenter);
        }

        return CollisionResult.failed();
    }

    @Override
    public void calculateBoundingBox() {
        AABB aabb = getBoundingBox();
        aabb.recomputeBoundariesWithHalfSize(getPosition(), radius);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
