package com.electrodiux.event;

public interface EventProcessor<E extends Event> {

    void processEvent(E event);

}
