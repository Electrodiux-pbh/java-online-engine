package com.electrodiux.event;

import java.io.Serial;
import java.util.UUID;

import com.electrodiux.entity.Entity;
import com.electrodiux.math.Vector3;

public class PositionEvent extends EntityEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private Vector3 position;
    private Vector3 rotation;

    public PositionEvent(UUID uuid, Vector3 position, Vector3 rotation) {
        super(uuid);
        this.position = position;
        this.rotation = rotation;
    }

    public PositionEvent(Entity entity, Vector3 position, Vector3 rotation) {
        this(entity.getUUID(), position, rotation);
    }

    public PositionEvent(Entity entity) {
        this(entity.getUUID(), new Vector3(entity.position()), new Vector3(entity.rotation()));
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

}
