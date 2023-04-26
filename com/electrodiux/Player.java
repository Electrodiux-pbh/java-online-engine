package com.electrodiux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.electrodiux.entity.Entity;
import com.electrodiux.event.Event;
import com.electrodiux.physics.SphereCollider;

public class Player extends Entity {

    private Collection<Event> events = new ArrayList<>();

    private String name;

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
        getRigidBody().setCollider(new SphereCollider(1));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    synchronized void addEvent(Event event) {
        events.add(event);
    }

    public synchronized Event[] clearEvents() {
        Event[] events = this.events.toArray(new Event[this.events.size()]);
        this.events.clear();
        return events;
    }

}
