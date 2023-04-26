package com.electrodiux.event;

import java.util.UUID;

import com.electrodiux.entity.Entity;

public class DespawnEntityEvent extends EntityEvent {

    public DespawnEntityEvent(Entity entity) {
        super(entity.getUUID());
    }

    public DespawnEntityEvent(UUID uuid) {
        super(uuid);
    }

}
