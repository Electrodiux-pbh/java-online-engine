package com.electrodiux.physics;

public enum ForceMode {
    /**
     * Adds a continuous force to the RigidBody, using its mass (N)
     */
    FORCE,
    /**
     * Adds a continuous acceleration to the RigidBody, ignoring its mass (m/s^2)
     */
    ACCELERATION,
    /**
     * Adds an instant force impulse to the RigidBody, using its mass (N)
     */
    IMPULSE,
    /**
     * Adds an instant velocity change to the RigidBody, ignoring its mass (m/s)
     */
    VELOCITY_CHANGE
}
