package com.electrodiux.physics;

import com.electrodiux.math.Maths;
import com.electrodiux.math.Vector3;

public class BoxCollider extends Collider {

    private Vector3 center;
    private Vector3 size;

    public BoxCollider() {
        this(Vector3.zeroVector(), Vector3.zeroVector());
    }

    public BoxCollider(Vector3 center, Vector3 size) {
        this.center = center;
        this.size = size;
    }

    public BoxCollider(RigidBody body, Vector3 center, Vector3 size) {
        super(body);
        this.center = center;
        this.size = size;
    }

    public Vector3 getMin() {
        return getPosition().getSubstract(size.getDiv(2));
    }

    public Vector3 getMax() {
        return getPosition().getAdded(size.getDiv(2));
    }

    @Override
    public void calculateBoundingBox() {
        AABB aabb = getBoundingBox();
        aabb.recomputeBoundariesWithHalfSize(getPosition().getAdded(center), size);
    }

    @Override
    public CollisionResult collidesWith(Collider other) {
        if (other instanceof BoxCollider collider) {
            return BoxCollider.boxCollisions(this, collider);
        } else if (other instanceof SphereCollider collider) {
            return BoxCollider.boxSphereCollision(this, collider);
        }
        return CollisionResult.failed();
    }

    public static CollisionResult boxCollisions(BoxCollider a, BoxCollider b) {
        return CollisionResult.failed();
    }

    public static CollisionResult boxSphereCollision(BoxCollider boxCollider, SphereCollider sphereCollider) {
        RigidBody box = boxCollider.getRigidBody();
        RigidBody sphere = sphereCollider.getRigidBody();

        if (box == null || sphere == null)
            return CollisionResult.failed();

        Vector3 sphereMassCenter = sphere.globalCenterOfMass();
        Vector3 boxMassCenter = box.globalCenterOfMass();

        // Calculate the closest point on the box's surface to the center of the sphere
        Vector3 closestPoint = new Vector3(
                Maths.clamp(sphereMassCenter.x, boxCollider.getMin().x, boxCollider.getMax().x),
                Maths.clamp(sphereMassCenter.y, boxCollider.getMin().y, boxCollider.getMax().y),
                Maths.clamp(sphereMassCenter.z, boxCollider.getMin().z, boxCollider.getMax().z));

        // Calculate the vector from the closest point on the box's surface to the
        // center of the sphere
        Vector3 offset = sphereMassCenter.getSubstract(closestPoint);

        // Calculate the distance between the box's surface and the center of the sphere
        float distance = offset.magnitude();

        // Check for collision
        if (distance < sphereCollider.getRadius()) {
            // Calculate the MTD
            Vector3 mtd = offset.getNormalized().mul(sphereCollider.getRadius() - distance);

            return CollisionResult.successCollision(mtd, box, boxMassCenter, sphere, sphereMassCenter);
        }

        return CollisionResult.failed();
    }

    public Vector3 getCenter() {
        return center;
    }

    public void setCenter(Vector3 center) {
        this.center = center;
    }

    public Vector3 getSize() {
        return size;
    }

    public void setSize(Vector3 size) {
        this.size = size;
    }

    public Vector3 getPosition() {
        return super.getPosition().getAdded(center);
    }
}
