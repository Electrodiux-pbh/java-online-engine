package com.electrodiux.graphics;

import java.io.Serializable;
import java.util.Random;

import com.electrodiux.math.MathUtils;

public class Color implements Serializable {

	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Color LIME = new Color(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Color GREEN = new Color(0.0f, 0.470588f, 0.0f, 1.0f);
	public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
	public static final Color LIGHT_BLUE = new Color(0.1176f, 0.7529f, 0.8078f, 1.0f);
	public static final Color GRAY = new Color(0.31372f, 0.31372f, 0.31372f, 1.0f);
	public static final Color LIGHT_GRAY = new Color(0.52941f, 0.52941f, 0.52941f, 1.0f);
	public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f, 1.0f);
	public static final Color PINK = new Color(1.0f, 0.0f, 1.0f, 1.0f);
	public static final Color PURPLE = new Color(0.451f, 0.0941f, 0.8902f, 1.0f);

	private float red, green, blue, alpha;

	public Color() {
		this(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public Color(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}

	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color(Color other) {
		this.red = other.red;
		this.green = other.green;
		this.blue = other.blue;
		this.alpha = other.alpha;
	}

	public Color(float[] values) {
		this(values[0], values[1], values[2], values[3]);
	}

	public void set(float r, float g, float b, float a) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
	}

	public void set(Color c) {
		this.red = c.red;
		this.green = c.green;
		this.blue = c.blue;
		this.alpha = c.alpha;
	}

	public float r() {
		return red;
	}

	public float g() {
		return green;
	}

	public float b() {
		return blue;
	}

	public float a() {
		return alpha;
	}

	public Color lerpColors(Color a, Color b, float t) {
		t = MathUtils.clamp(0, t, 1);
		Color c = new Color(
				MathUtils.lerp(a.red, b.red, t),
				MathUtils.lerp(a.green, b.green, t),
				MathUtils.lerp(a.blue, b.blue, t),
				MathUtils.lerp(a.alpha, b.alpha, t));

		return c;
	}

	public static Color randomColor() {
		Random random = new Random();
		return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f);
	}

}
