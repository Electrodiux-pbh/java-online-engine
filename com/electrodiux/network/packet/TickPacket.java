package com.electrodiux.network.packet;

import com.electrodiux.event.Event;

public class TickPacket {

    private Event[] events;
    private long tick;

    public TickPacket(long tick, Event[] events) {
        this.events = events;
    }

    public Event[] getEvents() {
        return events;
    }

    public long getTick() {
        return tick;
    }
}
