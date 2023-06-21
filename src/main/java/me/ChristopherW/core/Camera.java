package me.ChristopherW.core;

import org.joml.Vector3f;

public class Camera {
    private Vector3f position, rotation;

    public Camera() {
        position = new Vector3f(0,0,0);
        rotation = new Vector3f(0,0,0);
    }
    public Camera(Vector3f pos, Vector3f rot) {
        this.position = pos;
        this.rotation = rot;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    public void setPosition(Vector3f vector3f) {
        setPosition(vector3f.x, vector3f.y, vector3f.z);
    }


    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    public void rotate(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
