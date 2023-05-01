package com.electrodiux.physics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.electrodiux.math.Vector3;

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

    public Vector3 getPosition() {
        Vector3 position = getRigidBody().position();
        if (position == null)
            return Vector3.zeroVector();

        return position;
    }

    public Vector3 getRotation() {
        Vector3 rotation = getRigidBody().rotation();
        if (rotation == null)
            return Vector3.zeroVector();

        return rotation;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeTransients();
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
}
