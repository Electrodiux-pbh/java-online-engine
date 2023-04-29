package com.electrodiux.physics;

import com.electrodiux.math.Vector3;

public record CollisionResult(boolean collides, Vector3 body1Translation, Vector3 body2Translation,
                // the vector mtd stands for "Minimum Translation Distance"
                Vector3 mtd) {

        private static final CollisionResult FAILED_COLLISION_RESULT = new CollisionResult(false, new Vector3(),
                        new Vector3(), new Vector3());

        public static CollisionResult failed() {
                return FAILED_COLLISION_RESULT;
        }

        public static CollisionResult successCollision(Vector3 mtd, RigidBody body1, Vector3 body1MassCenter,
                        RigidBody body2, Vector3 body2MassCenter) {

                Vector3 body1Translation = Collider.getTranslationVectors(mtd, body1MassCenter, body2MassCenter,
                                body1.velocity(), body2.velocity());
                Vector3 body2Translation = Collider.getTranslationVectors(mtd, body2MassCenter, body1MassCenter,
                                body2.velocity(), body1.velocity());

                return new CollisionResult(true, body1Translation, body2Translation, mtd);

        }

}
