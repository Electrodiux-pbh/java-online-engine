package com.electrodiux;

import java.util.ArrayList;
import java.util.Collection;

import com.electrodiux.event.Event;
import com.electrodiux.util.Timer;

public abstract class WorldUpdater {

    protected World world;
    protected Collection<Event> events = new ArrayList<>();

    protected Timer timer;

    protected void start() {
        timer = new Timer(20);
        timer.setHandler(this::_update);

        Time.startTime();
        timer.start();
    }

    private void _update() {
        Time.updateTime();
        Time.setDeltaTime(timer.getDeltaTime());
        Time.setFixedDeltaTime(timer.getFixedDeltaTime());

        update();

        world.getEventsQueue().addAll(events);
        events.clear();

        world.update(timer.getDeltaTime());

        updatePackets();
        world.getProcesedEventsQueue().clear();

        Time.increaseUpdateCount();
    }

    protected abstract void update();

    protected abstract void updatePackets();

    public Collection<Event> getEvents() {
        return events;
    }
}
