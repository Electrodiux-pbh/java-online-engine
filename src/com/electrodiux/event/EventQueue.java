package com.electrodiux.event;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class EventQueue {

    private Queue<Event> events;

    public EventQueue() {
        events = new LinkedList<>();
    }

    public void add(Event event) {
        events.add(event);
    }

    public void addAll(Collection<? extends Event> events) {
        this.events.addAll(events);
    }

    public Event[] toEventArray() {
        return events.toArray(new Event[events.size()]);
    }

    public void clear() {
        events.clear();
    }

    public int size() {
        return events.size();
    }

    public Queue<Event> getQueue() {
        return events;
    }

}
