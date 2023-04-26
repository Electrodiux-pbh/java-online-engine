package com.electrodiux.event;

import java.io.Serial;
import java.util.UUID;

import com.electrodiux.Player;

public abstract class PlayerEvent extends EntityEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    public PlayerEvent(UUID playerUuid) {
        super(playerUuid);
    }

    public PlayerEvent(Player player) {
        super(player);
    }

    public UUID getPlayerUUID() {
        return super.getEntityUUID();
    }
}
