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

    public void setSourceToEvents(Object source) {
        for (int i = 0; i < events.length; i++) {
            Event e = events[i];
            if (e != null) {
                e.setSource(source);
            }
        }
    }
}