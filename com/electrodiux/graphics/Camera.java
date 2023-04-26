package com.electrodiux.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.electrodiux.math.Vector3;

public class Camera {

	public static final float FOV = 70;
	public static final float MIN_FOV = 10;
	public static final float MAX_FOV = 150;
	public static final float NEAR_PLANE = 0.1F;
	public static final float FAR_PLANE = 100;

	protected transient Matrix4f projectionMatrix, viewMatrix;
	protected Vector3 position;
	protected Vector3 rotation;
	protected boolean isOrtho = false;

	protected float aspectRatio;
	protected float zFar;
	protected float zNear;
	protected float fov;

	protected Color bg;

	protected transient Vector2f projectionSize = new Vector2f(6, 3);

	public Camera() {
		this(16f / 9f);
	}

	public Camera(float aspectRatio) {
		this(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
	}

	public Camera(float aspectRatio, boolean isOrtho) {
		this(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
		this.isOrtho = isOrtho;
		if (isOrtho) {
			makeProjection();
		}
	}

	public Camera(float fov, float aspectRatio, float nearPlane, float farPlane) {
		this(new Vector3(0, 0, 0), new Vector3(0, 0, 0), fov, aspectRatio, nearPlane, farPlane);
	}

	public Camera(Vector3 position, Vector3 rotation, float fov, float aspectRatio, float nearPlane, float farPlane) {
		this.position = position;
		this.rotation = rotation;

		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.zNear = nearPlane;
		this.zFar = farPlane;

		this.bg = Color.WHITE;

		this.viewMatrix = new Matrix4f();
		makeProjection();
	}

	public void makeProjection() {
		projectionMatrix = new Matrix4f();
		if (isOrtho()) {
			projectionMatrix.identity();
			projectionMatrix.ortho(0, projectionSize.x, 0, projectionSize.y, 0, 100);
		} else {
			projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
		}
	}

	public Matrix4f getViewMatrix() {
		viewMatrix.identity();

		viewMatrix.rotate(rotation.x(), new Vector3f(1, 0, 0));
		viewMatrix.rotate(rotation.y(), new Vector3f(0, 1, 0));
		viewMatrix.rotate(rotation.z(), new Vector3f(0, 0, 1));

		Vector3f inverseCameraPos = new Vector3f(-position.x(), -position.y(), -position.z());

		viewMatrix.translate(inverseCameraPos);
		return this.viewMatrix;
	}

	public void startFrame() {
		GL11.glClearColor(bg.r(), bg.g(), bg.b(), bg.a());
		// if (framebuffer != null) {
		// framebuffer.bind();
		// }
	}

	public void endFrame() {
		// if (framebuffer != null) {
		// framebuffer.destroy();
		// }
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Vector3 position() {
		return position;
	}

	public Vector3 rotation() {
		return rotation;
	}

	public boolean isOrtho() {
		return isOrtho;
	}

	public void setBackgroundColor(Color white) {
		bg.set(white);
	}

	public Color getBackgroundColor() {
		return bg;
	}

	public void setOrtho(boolean isOrtho) {
		if (this.isOrtho != isOrtho) {
			this.isOrtho = isOrtho;
			makeProjection();
		} else {
			this.isOrtho = isOrtho;
		}
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(float width, float height) {
		float ratio = width / height;
		if (this.aspectRatio != ratio) {
			this.aspectRatio = ratio;
			makeProjection();
		}
	}

	public float getzFar() {
		return zFar;
	}

	public void setzFar(float farPlane) {
		if (this.zFar != farPlane) {
			this.zFar = farPlane;
			makeProjection();
		} else {
			this.zFar = farPlane;
		}
	}

	public float getzNear() {
		return zNear;
	}

	public void setzNear(float nearPlane) {
		if (this.zNear != nearPlane) {
			this.zNear = nearPlane;
			makeProjection();
		} else {
			this.zNear = nearPlane;
		}
	}

	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		if (this.fov != fov) {
			this.fov = fov;
			makeProjection();
		} else {
			this.fov = fov;
		}
	}

}