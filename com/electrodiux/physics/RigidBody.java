package com.electrodiux.physics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.electrodiux.Time;
import com.electrodiux.math.Vector3;

public class RigidBody implements Serializable {

    private Vector3 position;
    private Vector3 rotation;

    private float mass;
    private Vector3 centerOfMass;

    private transient Vector3 velocity;
    private transient Vector3 angularVelocity;

    private float drag;
    private float angularDrag;

    public float friction;

    private boolean isKinematic;

    private Collider collider;

    public RigidBody() {
        this.position = new Vector3();
        this.rotation = new Vector3();

        this.angularDrag = 0.5f;
        this.drag = 1.0f;

        this.mass = 1.0f;
        this.centerOfMass = new Vector3();

        this.isKinematic = true;

        initializeTransients();
    }

    private void initializeTransients() {
        this.velocity = new Vector3();
        this.angularVelocity = new Vector3();
    }

    public void resetCenterOfMass() {
        this.centerOfMass = new Vector3(0, 0, 0);
    }

    void updateBodyTranslation() {
        if (!isKinematic)
            return;

        velocity.addScaled(velocity, -(drag + friction) * Time.deltaTime());
        angularVelocity.addScaled(angularVelocity, -(angularDrag + friction) * Time.deltaTime());

        position.addScaled(velocity, Time.deltaTime());
        rotation.addScaled(angularVelocity, Time.deltaTime());
    }

    // #region Forces

    public void movePosition(Vector3 p) {
        position.set(p);
    }

    public void moveRotation(Vector3 r) {
        rotation.set(r);
    }

    public void addForce(Vector3 force, ForceMode forceMode) {
        modifyVelocity(velocity, force, forceMode);
    }

    public void addTorque(Vector3 torque, ForceMode forceMode) {
        modifyVelocity(angularVelocity, torque, forceMode);
    }

    private void modifyVelocity(Vector3 velocity, Vector3 force, ForceMode forceMode) {
        switch (forceMode) {
            case ACCELERATION:
                vAceleration(velocity, force);
                break;
            case FORCE:
                vForce(velocity, force);
                break;
            case IMPULSE:
                vImpulse(velocity, force);
                break;
            case VELOCITY_CHANGE:
                vVelocityChange(velocity, force);
                break;
            default:
                break;
        }
    }

    private void vForce(Vector3 velocity, Vector3 force) {
        float coefficient = Time.deltaTime() / mass;
        velocity.add(force.x() * coefficient, force.y() * coefficient, force.z() * coefficient);
    }

    private void vAceleration(Vector3 velocity, Vector3 force) {
        velocity.add(force.x() * Time.deltaTime(), force.y() * Time.deltaTime(), force.z() * Time.deltaTime());
    }

    private void vImpulse(Vector3 velocity, Vector3 force) {
        velocity.add(force.x() / mass, force.y() / mass, force.z() / mass);
    }

    private void vVelocityChange(Vector3 velocity, Vector3 force) {
        velocity.add(force);
    }

    // #endregion

    // #region Serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(position);
        out.writeObject(rotation);

        out.writeFloat(mass);
        out.writeObject(centerOfMass);

        out.writeFloat(drag);
        out.writeFloat(angularDrag);

        out.writeBoolean(isKinematic);

        out.writeObject(collider);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        position = (Vector3) in.readObject();
        rotation = (Vector3) in.readObject();

        mass = in.readFloat();
        centerOfMass = (Vector3) in.readObject();

        drag = in.readFloat();
        angularDrag = in.readFloat();

        isKinematic = in.readBoolean();

        setCollider((Collider) in.readObject());

        initializeTransients();
    }

    // #endregion

    // #region Getters and Setters

    public Vector3 position() {
        return position;
    }

    public void position(Vector3 position) {
        this.position.set(position);
    }

    public Vector3 rotation() {
        return rotation;
    }

    public void rotation(Vector3 rotation) {
        this.rotation.set(rotation);
    }

    public Vector3 velocity() {
        return velocity;
    }

    public void velocity(Vector3 velocity) {
        this.velocity.set(velocity);
    }

    public Vector3 angularVelocity() {
        return angularVelocity;
    }

    public void angularVelocity(Vector3 angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public float drag() {
        return drag;
    }

    public void drag(float drag) {
        this.drag = drag;
    }

    public float mass() {
        return mass;
    }

    public void mass(float mass) {
        this.mass = mass;
    }

    public Vector3 centerOfMass() {
        return centerOfMass;
    }

    public void centerOfMass(Vector3 centerOfMass) {
        this.centerOfMass.set(centerOfMass);
    }

    public Vector3 globalCenterOfMass() {
        return Vector3.add(position, centerOfMass);
    }

    public boolean isKinematic() {
        return isKinematic;
    }

    public void setKinematic(boolean isKinematic) {
        this.isKinematic = isKinematic;
    }

    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        if (collider == null)
            return;

        collider.setRigidBody(this);
        this.collider = collider;
    }

    // #endregion

}
