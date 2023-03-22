package me.ChristopherW.core.custom;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Utils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseManager {
    HashMap<String, Vector3f> vectorRotationLookup = new HashMap<>();
    ArrayList<Hole> holes = new ArrayList<>();
    int activeHole;
    ArrayList<GolfBall> balls = new ArrayList<GolfBall>();
    GolfBall activeBall = null;
    public CourseManager() {
        activeHole = -1;
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
                Vector3f prevLoc = new Vector3f(holes.get(holeID - 1).getPosition());
                Vector3f holeDir = new Vector3f(holes.get(holeID).getHoleDirection());
                Vector3f newLoc = prevLoc.add(holeDir.mul(30).add(new Vector3f(holes.get(holeID - 1).getHoleDirection()).mul(-5)));
                hole.setPosition(newLoc);
                hole.setStartPos(new Vector3f(newLoc.x + holes.get(holeID).getHoleDirection().x, hole.getStartPos().y, newLoc.z + holes.get(holeID).getHoleDirection().z));
            }
            hole.setRotation(vectorRotationLookup.get(holes.get(holeID).getHoleDirection().toString()));
            holeEntities.put("Ground_" + holeID, hole.getGroundEntity());
            holeEntities.put("Wall_" + holeID, hole.getWallEntity());
        }
        return holeEntities;
    }
    public void NextHole() {
        activeHole++;
    }
    public void SetHole(int id) {
        activeHole = id;
    }
    public void AddHole(Hole hole) {
        if(holes.isEmpty())
            activeHole = 0;
        holes.add(hole);
    }
    public void RemoveHole(int id) {
        holes.remove(id);
        if(holes.isEmpty())
            activeHole = -1;
    }
    public Hole GetActiveHole() {
        return holes.get(activeHole);
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
        for(int i = 0; i < balls.size(); i++) {
            GolfBall ball = balls.get(i);
            ballEntities.put("Ball_" + i, ball);
        }
        return ballEntities;
    }

    public void AddBall(Texture texture, ObjectLoader loader, PhysicsSpace space) {
        Model ballModel = loader.loadModel("assets/models/ball.obj");
        ballModel.getMaterial().setTexture(texture);
        ballModel.getMaterial().setReflectability(0f);
        float ballScale = 0.0795f;
        GolfBall ballEntity = new GolfBall(ballModel, new Vector3f(0,0.4f,-1f), new Vector3f(0,0,0), ballScale);
        CollisionShape ballShape = new SphereCollisionShape(ballScale);
        float ballMass = 1f;
        PhysicsRigidBody ballRigidBody = new PhysicsRigidBody(ballShape, ballMass);
        ballRigidBody.setPhysicsLocation(Utils.convert(ballEntity.getPosition()));
        ballRigidBody.setRestitution(0.9f);
        ballRigidBody.setCcdMotionThreshold(0.015f);
        ballRigidBody.setCcdSweptSphereRadius(0.01f);
        ballEntity.setRigidBody(ballRigidBody);
        balls.add(ballEntity);
        space.addCollisionObject(ballRigidBody);
        if(balls.size() == 1)
            activeBall = balls.get(0);
    }
    public GolfBall GetBall(int id) {
        return balls.get(id);
    }
    public int GetBallCount() {
        return balls.size();
    }
    public GolfBall GetActiveBall() {
        return activeBall;
    }
    public void NextBall() {
        int ballID = balls.indexOf(activeBall);
        if(ballID == balls.size() - 1)
            activeBall = balls.get(0);
        else
            activeBall = balls.get(ballID + 1);
    }
}
