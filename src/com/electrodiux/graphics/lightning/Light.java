package com.electrodiux.graphics.lightning;

import com.electrodiux.graphics.Color;
import com.electrodiux.math.Vector3;

public class Light {

    private Vector3 lightPos;
    private Vector3 attenuation;
    private Color color;

    public Light(Vector3 lightPos, Color color, Vector3 attenuation) {
        this.lightPos = new Vector3(lightPos);
        this.color = new Color(color);
        this.attenuation = new Vector3(attenuation);
    }

    public Light(Vector3 lightPos, Color color) {
        this(lightPos, color, Vector3.RIGHT);
    }

    public Light() {
        this(new Vector3(), Color.WHITE, Vector3.RIGHT);
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

    public Vector3 getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3 attenuation) {
        this.attenuation = attenuation;
    }

}
