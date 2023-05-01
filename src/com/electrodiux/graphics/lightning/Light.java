package com.electrodiux.graphics.lightning;

import com.electrodiux.graphics.Color;
import com.electrodiux.math.Vector3;

public class Light {

    private Vector3 lightPos;
    private Color color;

    public Light(Vector3 lightPos, Color color) {
        this.lightPos = lightPos;
        this.color = color;
    }

    public Light() {
        this(new Vector3(), Color.WHITE);
    }

    public Vector3 getLightPos() {
        return lightPos;
    }

    public void setLightPos(Vector3 lightPos) {
        this.lightPos = lightPos;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
