package me.ChristopherW.core.custom;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.bullet.objects.PhysicsRigidBody;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.utils.Utils;
import org.joml.Vector3f;

public class Hole {
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f startPos;
    private Vector3f holeDirection;
    private Vector3f holePosition;
    private Entity groundEntity;
    private Entity wallEntity;
    public Hole(Vector3f holeDirection, Model groundModel, Model wallModel, ObjectLoader loader, PhysicsSpace space) {
        this(0, holeDirection, groundModel, wallModel, loader, space);
    }
    public Hole(float startHeightOffset, Vector3f holeDirection, Model groundModel, Model wallModel, ObjectLoader loader, PhysicsSpace space) {
        this.startPos = new Vector3f(0, 0.4f + startHeightOffset, -1);
        this.position = new Vector3f(0,0,0);
        this.rotation = new Vector3f(0,0,0);
        this.holeDirection = holeDirection;
        this.holePosition = loader.getHoleLocation(groundModel);
        groundEntity = new Entity(groundModel, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(1,1,1), space);
        IndexedMesh golfGroundMesh = loader.loadIndexedMesh(groundModel, new Vector3f(1,1,1));
        CollisionShape golfGroundShape = new GImpactCollisionShape(golfGroundMesh);
        golfGroundShape.setMargin(0.00001f);
        PhysicsRigidBody golfGroundPhysicsObject = new PhysicsRigidBody(golfGroundShape, 0);
        golfGroundPhysicsObject.setPhysicsLocation(Utils.convert(groundEntity.getPosition()));
        golfGroundPhysicsObject.setRestitution(0);
        groundEntity.setRigidBody(golfGroundPhysicsObject);

        wallEntity = new Entity(wallModel, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(1,1,1), space);
        IndexedMesh wallMesh = loader.loadIndexedMesh(wallModel, new Vector3f(1,1,1));
        CollisionShape wallShape = new GImpactCollisionShape(wallMesh);
        wallShape.setMargin(0.00001f);
        PhysicsRigidBody golfPhysicsObject = new PhysicsRigidBody(wallShape, 0);
        golfPhysicsObject.setPhysicsLocation(Utils.convert(wallEntity.getPosition()));
        golfPhysicsObject.setRestitution(0.9f);
        wallEntity.setRigidBody(golfPhysicsObject);

        space.addCollisionObject(groundEntity.getRigidBody());
        space.addCollisionObject(wallEntity.getRigidBody());
    }
    public Hole(Vector3f holeDirection, String groundPath, String wallPath, ObjectLoader loader, PhysicsSpace space) {
        this(holeDirection, loader.loadModel(groundPath), loader.loadModel(wallPath), loader, space);
    }

    public Vector3f getStartPos() {
        return startPos;
    }

    public void setStartPos(Vector3f startPos) {
        this.startPos = startPos;
    }

    public Vector3f getHolePosition() {
        return holePosition;
    }

    public void setHolePosition(Vector3f holePosition) {
        this.holePosition = holePosition;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        groundEntity.setPosition(position);
        wallEntity.setPosition(position);
    }

    public void SetActive(boolean value, PhysicsSpace space) {
        if(value) {
            space.addCollisionObject(groundEntity.getRigidBody());
            space.addCollisionObject(wallEntity.getRigidBody());
        } else {
            space.removeCollisionObject(groundEntity.getRigidBody());
            space.removeCollisionObject(wallEntity.getRigidBody());
        }
    }

    public Vector3f getHoleDirection() {
        return holeDirection;
    }

    public void setHoleDirection(Vector3f holeDirection) {
        this.holeDirection = holeDirection;
    }

    public Entity getGroundEntity() {
        return groundEntity;
    }

    public Entity getWallEntity() {
        return wallEntity;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        groundEntity.setRotation(rotation);
        wallEntity.setRotation(rotation);
    }
    public Vector3f getRotation() {
        return rotation;
    }
}
