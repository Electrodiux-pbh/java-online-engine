package com.electrodiux.graphics.lightning;

import com.electrodiux.graphics.Color;
import com.electrodiux.math.Vector3;

public class GlobalLight {

    private Vector3 lightDirection;
    private Color color;

    public GlobalLight(Vector3 lightDirection, Color color) {
        this.lightDirection = lightDirection;
        this.color = new Color(color);
    }

    public GlobalLight() {
        this(new Vector3(0, -1, 0), Color.WHITE);
    }

    public Vector3 getLightDirection() {
        return lightDirection;
    }

    public void setLightDirection(Vector3 lightDirection) {
        this.lightDirection = lightDirection;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
