package com.tona.cursorbrowser;

import android.graphics.Point;

public class Cursor {
	public static final float DEFAULT_WIDTH = 39;
	public static final float DEFAULT_HEIGHT = 48;

	private float x, y;
	private float downX, downY;
	private float velocity;
	private float sizeRate;
	private float width, height;
	private Point displaySize;
	public static float defaultX, defaultY;
	private String padRange;

	public Cursor(int displayWidth, int displayHeight) {
		setDisplaySize(new Point(displayWidth, displayHeight));
		defaultX = displayWidth / 2;
		defaultY = displayHeight / 2;
		x = displayWidth / 2;
		y = displayHeight / 2;
		setVelocity(1.0f);
		setWidth(DEFAULT_WIDTH);
		setHeight(DEFAULT_HEIGHT);
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getVelocity() {
		return velocity;
	}
	public void setVelocity(float v) {
		this.velocity = v;
	}
	public double getSizeRate() {
		return sizeRate;
	}
	public void setSizeRate(float sizeRate) {
		this.sizeRate = sizeRate;
		setWidth(DEFAULT_WIDTH * sizeRate);
		setHeight(DEFAULT_HEIGHT * sizeRate);
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public Point getDisplaySize() {
		return displaySize;
	}
	public void setDisplaySize(Point displaySize) {
		this.displaySize = displaySize;
	}
	public float getDownX() {
		return downX;
	}
	public void setDownX(float downX) {
		this.downX = downX;
	}
	public float getDownY() {
		return downY;
	}
	public void setDownY(float downY) {
		this.downY = downY;
	}
	public String getOperationRange() {
		return padRange;
	}
	public void setOperationRange(String operationrange) {
		padRange = operationrange;
	}
}
