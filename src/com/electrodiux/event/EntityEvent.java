package com.electrodiux.event;

import java.io.Serial;
import java.util.UUID;

import com.electrodiux.entity.Entity;

public class EntityEvent extends Event {

    @Serial
    private static final long serialVersionUID = 1L;

    protected final UUID id;

    public EntityEvent(UUID id) {
        this.id = id;
    }

    public EntityEvent(Entity entity) {
        this.id = entity.getUUID();
    }

    public UUID getEntityUUID() {
        return id;
    }

}
