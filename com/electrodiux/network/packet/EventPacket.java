package com.electrodiux.network.packet;

import com.electrodiux.event.Event;

public class EventPacket extends Packet {

    private Event[] events;

    public EventPacket(Event[] events) {
        super(PacketType.EVENT);
        this.events = events;
    }

    public Event[] getEvents() {
        return events;
    }
}