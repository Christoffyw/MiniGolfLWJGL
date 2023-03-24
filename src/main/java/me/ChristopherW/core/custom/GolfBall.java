package me.ChristopherW.core.custom;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashMap;

public class GolfBall extends Entity {
    private int currentHoleID;
    private boolean firstShot;
    private Vector3f velocity;
    private float friction;
    private long startTime;
    private long endTime;
    private float shotStrength;
    private Color color;
    private HashMap<Integer, Integer> scores;
    PhysicsSpace physicsSpace;
    public GolfBall(Model model, Vector3f position, Vector3f rotation, Vector3f scale, Color color, PhysicsSpace space) {
        super(model, position, rotation, scale, space);
        this.velocity = new Vector3f(0,0,0);
        this.friction = 0.975f;
        this.startTime = 0;
        this.endTime = 0;
        this.shotStrength = 0;
        this.currentHoleID = 0;
        this.physicsSpace = space;
        this.firstShot = true;
        this.color = color;
        this.scores = new HashMap<>();
    }
    public int getTotalScore() {
        int totalScore = 0;
        for(int i : scores.values()) {
            totalScore += i;
        }
        return totalScore;
    }
    public void setScore(int holeID, int score) {
        scores.put(holeID, score);
    }
    public int getScore(int holeID) {
        return scores.get(holeID);
    }
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public void teleportHole(int id, CourseManager courseManager, WindowManager window) {
        this.setCurrentHoleID(id);
        this.setPosition(courseManager.GetHole(this.getCurrentHoleID()).getStartPos());
        this.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
        this.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
        window.guiManager.setHoleText(String.format("Hole %d", this.getCurrentHoleID() + 1));
        window.guiManager.setPlayerText(String.format("Player %d", courseManager.GetBallID(this) + 1));
        window.guiManager.setStrokeText(String.format("Strokes: %d", this.getScore(this.getCurrentHoleID())));
    }
    public boolean isFirstShot() {
        return firstShot;
    }

    public void setFirstShot(boolean firstShot) {
        this.firstShot = firstShot;
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
