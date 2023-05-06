package com.electrodiux.physics;

import java.util.ArrayList;
import java.util.List;

import com.electrodiux.math.Vector3;
import com.electrodiux.terrain.Chunk;

public final class PhysicsSystem {

    private List<RigidBody> rigidBodies;

    private Vector3 gravity;

    public PhysicsSystem(Vector3 gravity) {
        this.rigidBodies = new ArrayList<>();
        this.gravity = new Vector3(gravity);
    }

    private Chunk chunk;

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public synchronized void updateSimulation() {
        // Update bodies
        for (RigidBody body : rigidBodies) {
            if (body.isKinematic()) {
                body.addForce(gravity, ForceMode.ACCELERATION);

                // Update translations
                body.updateBodyTranslation();
            }

            // Re compute bounding box
            Collider collider = body.getCollider();
            if (collider != null) {
                collider.calculateBoundingBox();
            }
        }

        // Collisions
        checkCollisions();
    }

    private void floorCollision(RigidBody body) {
        float height = 0;
        if (chunk != null) {
            height = chunk.getHeightAt(body.position().x(), body.position().z());
        }

        if (height == 0) {
            return;
        }

        float yOffset = 0;

        if (body.getCollider() instanceof SphereCollider sphereCollider) {
            yOffset = sphereCollider.getRadius();
        }

        float totalHeight = height + yOffset;

        if (body.position().y() <= totalHeight) {
            Vector3 slope = chunk.getSlopeAt(body.position().x(), body.position().z());
            body.velocity().add(slope.x(), 0, slope.z());
            body.position().y(totalHeight);
            body.friction = 1;
        } else {
            body.friction = 0.5f;
        }
    }

    private void checkCollisions() {
        body1: for (int i = 0; i < rigidBodies.size(); i++) {
            RigidBody body1 = rigidBodies.get(i);
            Collider collider1 = body1.getCollider();

            if (collider1 == null)
                continue body1;

            body2: for (int j = i + 1; j < rigidBodies.size(); j++) {
                RigidBody body2 = rigidBodies.get(j);
                Collider collider2 = body2.getCollider();

                if (collider2 == null)
                    continue body2;

                if (!AABB.collides(collider1.getBoundingBox(), collider2.getBoundingBox())) {
                    continue;
                }

                CollisionResult result = collider1.collidesWith(collider2);
                calculateCollisionInteraction(body1, body2, result != null ? result : CollisionResult.failed());
            }

            floorCollision(body1);
        }
    }

    private static void calculateCollisionInteraction(RigidBody body1, CollisionResult result) {
        if (!result.collides())
            return;

        Vector3 mtd = result.mtd();

        body1.position().add(result.body1Translation());

        Vector3 normal = new Vector3(mtd).normalize();

        // #region Calculate the impulse
        float velocityAlongNormal = Vector3.dot(normal, body1.velocity());

        final float coefficientOfResitution = 0.8f;
        float totalImpulse = (-1.0f - coefficientOfResitution) * velocityAlongNormal;

        Vector3 impulse = Vector3.mul(normal, totalImpulse);
        body1.velocity().add(impulse);
        // #endregion

        // #region Calculate position Correction
        float penetrationDepth = mtd.magnitude();

        final float percent = 0.01f; // usually 20% to 80%
        final float slop = 0.1f; // usually 0.01 to 0.1

        Vector3 correction = normal.getMul(percent * Math.max(penetrationDepth - slop, 0.0f));

        body1.position().subtract(correction);
        // #endregion
    }

    private static void calculateCollisionInteraction(RigidBody body1, RigidBody body2, CollisionResult result) {
        if (!result.collides())
            return;

        Vector3 mtd = result.mtd();

        body1.position().add(result.body1Translation());
        body2.position().add(result.body2Translation());

        float body1InverseMass = 1.0f / body1.mass();
        float body2InverseMass = 1.0f / body2.mass();
        float sumInverseMass = body1InverseMass + body2InverseMass;

        if (!body1.isKinematic())
            body1InverseMass = 0;

        if (!body2.isKinematic())
            body2InverseMass = 0;

        Vector3 normal = new Vector3(mtd).normalize();

        // #region Calculate the impulse
        float velocityAlongNormal = Vector3.dot(normal, Vector3.subtract(body2.velocity(), body1.velocity()));

        final float coefficientOfResitution = 0.8f;
        float totalImpulse = ((-1.0f - coefficientOfResitution) * velocityAlongNormal) / sumInverseMass;

        Vector3 impulse = Vector3.mul(normal, totalImpulse);
        body1.velocity().subtract(
                impulse.x * body1InverseMass,
                impulse.y * body1InverseMass,
                impulse.z * body1InverseMass);
        body2.velocity().add(
                impulse.x * body2InverseMass,
                impulse.y * body2InverseMass,
                impulse.z * body2InverseMass);
        // #endregion

        // #region Calculate position Correction
        float penetrationDepth = mtd.magnitude();

        final float percent = 0.01f; // usually 20% to 80%
        final float slop = 0.1f; // usually 0.01 to 0.1

        Vector3 correction = normal.getMul(percent * (Math.max(penetrationDepth - slop, 0.0f) / sumInverseMass));

        body1.position().subtract(
                correction.x * body1InverseMass,
                correction.y * body1InverseMass,
                correction.z * body1InverseMass);
        body2.position().subtract(
                correction.x * body2InverseMass,
                correction.y * body2InverseMass,
                correction.z * body2InverseMass);
        // #endregion
    }

    public static Vector3 getTranslationVectors(Vector3 mdt, Vector3 mainPolyCenterPt, Vector3 otherPolyCenterPt,
            Vector3 mainPolyVelocity, Vector3 otherPolyVelocity) {

        if (mainPolyVelocity.x() == 0 && mainPolyVelocity.y() == 0 && mainPolyVelocity.z() == 0)
            return new Vector3(0, 0, 0);

        Vector3 translationVector = new Vector3(mdt);

        Vector3 displacementBetweenPolygons = mainPolyCenterPt.getSubstract(otherPolyCenterPt);

        if (displacementBetweenPolygons.dot(mdt) < 0) {
            translationVector.inverseSign();
        }

        float curLength = translationVector.magnitude();
        float lengthOfMainVelocity = mainPolyVelocity.magnitude();
        float lengthOfOtherVelocity = otherPolyVelocity.magnitude();
        float newLength = curLength * (lengthOfMainVelocity / (lengthOfMainVelocity + lengthOfOtherVelocity));

        translationVector.magnitude(newLength);

        if (Float.isNaN(translationVector.x()) && Float.isNaN(translationVector.y())
                && Float.isNaN(translationVector.z()))
            translationVector.set(0, 0, 0);

        return translationVector;
    }

    public synchronized void addRigidBody(RigidBody body) {
        this.rigidBodies.add(body);
    }

    public synchronized void removeRigidBody(RigidBody body) {
        this.rigidBodies.remove(body);
    }

    public void setGravity(Vector3 gravity) {
        this.gravity.set(gravity);
    }

}
