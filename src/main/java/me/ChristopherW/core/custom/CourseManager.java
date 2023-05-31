package me.ChristopherW.core.custom;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Transformation;
import me.ChristopherW.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseManager {
    HashMap<String, Vector3f> vectorRotationLookup = new HashMap<>();
    ArrayList<Hole> holes = new ArrayList<>();
    ArrayList<GolfBall> balls = new ArrayList<GolfBall>();
    public ArrayList<Integer> currentBalls;
    public ArrayList<Integer> finishedBalls;
    GolfBall activeBall = null;
    public CourseManager() {
        vectorRotationLookup.put(new Vector3f(0,0,1).toString(), new Vector3f(0,180,0));
        vectorRotationLookup.put(new Vector3f(0,0,-1).toString(), new Vector3f(0,0,0));
        vectorRotationLookup.put(new Vector3f(1,0,0).toString(), new Vector3f(0,-90,0));
        vectorRotationLookup.put(new Vector3f(-1,0,0).toString(), new Vector3f(0,90,0));
    }

    public HashMap<String, Entity> InitHoles() {
        HashMap<String, Entity> holeEntities = new HashMap<>();
        for(int holeID = 0; holeID < holes.size(); holeID++) {
            Hole hole = holes.get(holeID);
            if(holeID > 0) {
                Matrix4f rotationMatrix = Transformation.createRotationMatrix(vectorRotationLookup.get(holes.get(holeID).getHoleDirection().toString()));
                Hole prevHole = holes.get(holeID - 1);
                Vector3f prevLoc = new Vector3f(prevHole.getHolePosition().x, prevHole.getPosition().y, prevHole.getHolePosition().z);
                Vector3f holeDir = new Vector3f(holes.get(holeID).getHoleDirection());
                Vector3f newLoc = prevLoc.add(holeDir.mul(10).add(new Vector3f(prevHole.getHoleDirection())));
                hole.setPosition(newLoc);
                hole.setStartPos(new Vector3f(newLoc.x + holes.get(holeID).getHoleDirection().x, hole.getStartPos().y, newLoc.z + holes.get(holeID).getHoleDirection().z));
                Vector4f newHoleRot = new Vector4f(hole.getHolePosition(), 1);
                newHoleRot.mul(rotationMatrix);
                hole.setHolePosition(new Vector3f(newHoleRot.x, newHoleRot.y, newHoleRot.z));
                hole.getHolePosition().add(newLoc);
            }
            hole.setRotation(vectorRotationLookup.get(holes.get(holeID).getHoleDirection().toString()));
            holeEntities.put("Ground_" + holeID, hole.getGroundEntity());
            holeEntities.put("Wall_" + holeID, hole.getWallEntity());
        }
        return holeEntities;
    }
    public void AddHole(Hole hole) {
        holes.add(hole);
    }
    public void RemoveHole(int id) {
        holes.remove(id);
    }
    public Hole GetHole(int id) {
        return holes.get(id);
    }
    public int GetHoleID(Hole hole) {
        return holes.indexOf(hole);
    }
    public int GetHoleCount() {
        return holes.size();
    }

    public HashMap<String, Entity> InitBalls() {
        HashMap<String, Entity> ballEntities = new HashMap<>();
        currentBalls = new ArrayList<>();
        finishedBalls = new ArrayList<>();
        for(int i = 0; i < balls.size(); i++) {
            GolfBall ball = balls.get(i);
            for(int holeID = 0; holeID < holes.size(); holeID++) {
                ball.setScore(holeID, 0);
            }
            currentBalls.add(i);
            ballEntities.put("Ball_" + i, ball);
        }

        return ballEntities;
    }

    public GolfBall AddBall(Color color, Texture texture, ObjectLoader loader, PhysicsSpace space) {
        Model ballModel = loader.loadModel("assets/models/ball.obj");
        ballModel.getMaterial().setTexture(texture);
        ballModel.getMaterial().setReflectability(0f);
        float ballScale = 0.0795f;
        GolfBall ballEntity = new GolfBall(ballModel, new Vector3f(0,0.4f,-1f), new Vector3f(0,0,0), new Vector3f(ballScale, ballScale, ballScale), color, space);
        CollisionShape ballShape = new SphereCollisionShape(ballScale);
        float ballMass = 1f;
        PhysicsRigidBody ballRigidBody = new PhysicsRigidBody(ballShape, ballMass);
        ballRigidBody.setPhysicsLocation(Utils.convert(ballEntity.getPosition()));
        ballRigidBody.setRestitution(0.9f);
        ballRigidBody.setCcdMotionThreshold(0.015f);
        ballRigidBody.setCcdSweptSphereRadius(0.01f);
        ballEntity.setRigidBody(ballRigidBody);
        balls.add(ballEntity);
        if(balls.size() == 1)
            activeBall = balls.get(0);
        return ballEntity;
    }
    public GolfBall GetBall(int id) {
        return balls.get(id);
    }
    public int GetBallCount() {
        return balls.size();
    }
    public void SetActiveBall(GolfBall activeBall) {
        this.activeBall = activeBall;
    }
    public GolfBall GetActiveBall() {
        return activeBall;
    }
    public void NextBall() {
        int ballID = balls.indexOf(activeBall);
        if(currentBalls.size() == 0) {
            for(int i = 0; i < finishedBalls.size(); i++) {
                currentBalls.add(finishedBalls.get(i));
            }
            activeBall = balls.get(currentBalls.get(0));
            return;
        }

        if (currentBalls.indexOf(ballID) == currentBalls.size() - 1)
            activeBall = balls.get(currentBalls.get(0));
        else
            activeBall = balls.get(currentBalls.get(currentBalls.indexOf(ballID) + 1));
    }

    public void BallFinish(GolfBall ball) {
        finishedBalls.add(currentBalls.get(currentBalls.indexOf(balls.indexOf(ball))));
        currentBalls.remove(currentBalls.get(currentBalls.indexOf(balls.indexOf(ball))));
    }

    public ArrayList<GolfBall> GetBalls() {
        return balls;
    }

    public int GetBallID(GolfBall getActiveBall) {
        return balls.indexOf(getActiveBall);
    }
}
