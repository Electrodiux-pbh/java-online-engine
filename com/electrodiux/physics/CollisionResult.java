package com.electrodiux.physics;

import com.electrodiux.math.Vector3;

public record CollisionResult(boolean collides, Vector3 body1Translation, Vector3 body2Translation, Vector3 mtd) {

}
