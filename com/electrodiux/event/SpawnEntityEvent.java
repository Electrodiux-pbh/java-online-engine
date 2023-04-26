package com.electrodiux.event;

import com.electrodiux.entity.Entity;

public class SpawnEntityEvent extends EntityEvent {

    private Entity entity;

    public SpawnEntityEvent(Entity entity) {
        super(entity.getUUID());
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
