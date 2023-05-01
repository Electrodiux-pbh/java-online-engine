package com.electrodiux.event;

import java.io.Serial;
import java.util.UUID;

import com.electrodiux.math.Vector3;

public class MoveEvent extends EntityEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private Vector3 moveVector;
    private Vector3 rotationVector;
    private boolean jump;
    private boolean spawn;

    public MoveEvent(UUID playerUuid, Vector3 moveVector, Vector3 rotationVector, boolean jump, boolean spawn) {
        super(playerUuid);
        this.moveVector = moveVector;
        this.rotationVector = rotationVector;
        this.jump = jump;
        this.spawn = spawn;
    }

    public Vector3 getMoveVector() {
        return moveVector;
    }

    public Vector3 getRotationVector() {
        return rotationVector;
    }

    public boolean isJumping() {
        return jump;
    }

    public boolean isSpawning() {
        return spawn;
    }

}
