package me.ChristopherW.core;

import java.util.Random;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.SolverType;
import com.jme3.bullet.collision.ManifoldPoints;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.test.Launcher;

public class GolfPhysics extends PhysicsSpace {
    private Random random;

    public GolfPhysics(BroadphaseType type) {
        super(type);
        random = new Random();
    }

    public GolfPhysics(Vector3f worldMin, Vector3f worldMax, BroadphaseType broadphaseType, SolverType solverType) {
        super(worldMin, worldMax, broadphaseType, solverType);
        random = new Random();
    }
    
    @Override
    public void onContactProcessed(PhysicsCollisionObject pcoA, PhysicsCollisionObject pcoB, long pointId) {
        Entity a = null;
        Entity b = null;

        // get Entities from the given RigidBodies
        for(String key : Launcher.getGame().entities.keySet()) {
            Entity e = Launcher.getGame().entities.get(key);
            if(pcoA instanceof PhysicsRigidBody) {
                PhysicsRigidBody rb = (PhysicsRigidBody) pcoA;
                if(e.getRigidBody() == null)
                    continue;
                if(e.getRigidBody().equals(rb))
                    a = e;
            }
            if(pcoB instanceof PhysicsRigidBody) {
                PhysicsRigidBody rb = (PhysicsRigidBody) pcoB;
                if(e.getRigidBody() == null)
                    continue;
                if(e.getRigidBody().equals(rb))
                    b = e;
            }
        }

        // ignore collisions with no entities found
        if(b == null)
            return;
        
        // get the velocity of the second entity
        Vector3f velocityB = b.getRigidBody().getLinearVelocity(null);
   
        // if either a or b is the wall
        if(a.getName().startsWith("Wall_") || b.getName().startsWith("Wall_")) {
            // check the dot product of the velocity and the normal of the wall to check 
            // if realistically a bounce sound would be played
            Vector3f out = new Vector3f(0, 0,0);
            ManifoldPoints.getNormalWorldOnB(pointId, out);
            if(out.y > 0.1f || out.y < -0.1f)
                return;
            float angleOfCollision = out.dot(velocityB);
            if(angleOfCollision < 0.2f)
                return;

            // play a random bounce sound
            int r = random.nextInt(3) + 1;
            if(!Launcher.getGame().audioSources.get("bounce" + r).isPlaying())
                Launcher.getGame().audioSources.get("bounce" + r).play();
        }
        super.onContactProcessed(pcoA, pcoB, pointId);
    }

    @Override
    public void onContactEnded(long manifoldId) {
        super.onContactEnded(manifoldId);
    }

    @Override
    public void onContactStarted(long manifoldId) {
        super.onContactStarted(manifoldId);
    }
}
