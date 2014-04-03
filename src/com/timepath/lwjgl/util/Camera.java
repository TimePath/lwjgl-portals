package com.timepath.lwjgl.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * First Person Camera Controller
 */
public class Camera {

    /**
     * 3d vector to store the camera's position in
     */
    private final Vector3f position;

    /**
     * the rotation around the Y axis of the camera
     */
    private float yaw = 0.0f;

    /**
     * the rotation around the X axis of the camera
     */
    private float pitch = 0.0f;

    public float getX() {
        return position.x;
    }

    public float getZ() {
        return position.z;
    }

    // TODO: 
    public Matrix4f viewMat() {
        return new Matrix4f();
    }

    public Matrix4f projMat() {
        return new Matrix4f();
    }

    /**
     * Constructor that takes the starting x, y, z location of the camera
     *
     * @param x
     * @param y
     * @param z
     */
    public Camera(float x, float y, float z) {
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
    }

    public void add(float x, float y, float z) {
        position.set(position.x + x, position.y + y, position.z + z);
    }

    /**
     * increment the camera's current yaw rotation
     *
     * @param amount
     */
    public void yaw(float amount) {
        //increment the yaw by the amount param
        yaw += amount;
    }

    public float getYaw() {
        return yaw;
    }

    /**
     * increment the camera's current yaw rotation
     *
     * @param amount
     */
    public void pitch(float amount) {
        //increment the pitch by the amount param
        pitch += amount;

    }

    /**
     * moves the camera forward relative to its current rotation (yaw)
     *
     * @param distance
     */
    public void walkForward(float distance) {
        position.x += distance * (float) Math.sin(Math.toRadians(yaw));
        position.z -= distance * (float) Math.cos(Math.toRadians(yaw));
    }

    /**
     * Moves the camera backward relative to its current rotation (yaw)
     *
     * @param distance
     */
    public void walkBackwards(float distance) {
        position.x -= distance * (float) Math.sin(Math.toRadians(yaw));
        position.z += distance * (float) Math.cos(Math.toRadians(yaw));
    }

    /**
     * Strafes the camera left relative to its current rotation (yaw)
     *
     * @param distance
     */
    public void strafeLeft(float distance) {
        position.x += distance * (float) Math.sin(Math.toRadians(yaw - 90));
        position.z -= distance * (float) Math.cos(Math.toRadians(yaw - 90));
    }

    /**
     * Strafes the camera right relative to its current rotation (yaw)
     *
     * @param distance
     */
    public void strafeRight(float distance) {
        position.x += distance * (float) Math.sin(Math.toRadians(yaw + 90));
        position.z -= distance * (float) Math.cos(Math.toRadians(yaw + 90));
    }

    /**
     * Translates and rotate the matrix so that it looks through the camera.
     * This does basically what gluLookAt() does
     */
    public void lookThrough() {
        GL11.glRotatef(getPitch(), 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-position.x, -position.y, -position.z);
    }

    public void rise(float f) {
        position.y += f;
    }

    /**
     * @return the pitch
     */
    public float getPitch() {
        return pitch;
    }

    public Vector3f pos() {
        return new Vector3f(position);
    }

}
