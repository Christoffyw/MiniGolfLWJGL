package me.ChristopherW.core.custom;

import com.jme3.bullet.objects.PhysicsRigidBody;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import org.joml.Vector3f;

public class GolfBall extends Entity {
    private int currentHoleID;

    private Vector3f velocity;
    private float friction;
    private long startTime;
    private long endTime;
    private float shotStrength;
    public GolfBall(Model model, Vector3f position, Vector3f rotation, float scale) {
        super(model, position, rotation, scale);
        this.velocity = new Vector3f(0,0,0);
        this.friction = 0.975f;
        this.startTime = 0;
        this.endTime = 0;
        this.shotStrength = 0;
    }

    public int getCurrentHoleID() {
        return currentHoleID;
    }

    public void setCurrentHoleID(int currentHoleID) {
        this.currentHoleID = currentHoleID;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public Vector3f getVelocity() {
        return velocity;
    }
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    public void addVelocity(Vector3f velocity) {
        this.velocity = this.velocity.add(velocity);
    }
    public long getStartTime() {
        return startTime;
    }
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public float getShotStrength() {
        return shotStrength;
    }

    public void setShotStrength(float shotStrength) {
        this.shotStrength = shotStrength;
    }
}
