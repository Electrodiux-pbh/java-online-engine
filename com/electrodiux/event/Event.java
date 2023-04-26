package com.electrodiux.event;

import java.io.Serializable;

public abstract class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Object source;

    public Event() {
    }

    public Event(int source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}