package com.electrodiux.physics;

import com.electrodiux.math.Vector3;

public record CollisionResult(boolean collides, Vector3 body1Translation, Vector3 body2Translation, Vector3 mtd) {

    private static final CollisionResult FAILED_COLLISION_RESULT = new CollisionResult(false, new Vector3(),
            new Vector3(), new Vector3());

    public static CollisionResult failed() {
        return FAILED_COLLISION_RESULT;
    }

}
