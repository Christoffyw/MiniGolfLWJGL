package me.ChristopherW.core.custom;

import me.ChristopherW.core.entity.Entity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

public class CourseManager {
    HashMap<String, Vector3f> vectorRotationLookup = new HashMap<>();
    ArrayList<Hole> holes = new ArrayList<>();
    int activeHole;
    GolfBall ball;
    public CourseManager() {
        activeHole = -1;
        vectorRotationLookup.put(new Vector3f(0,0,1).toString(), new Vector3f(0,180,0));
        vectorRotationLookup.put(new Vector3f(0,0,-1).toString(), new Vector3f(0,0,0));
        vectorRotationLookup.put(new Vector3f(1,0,0).toString(), new Vector3f(0,90,0));
        vectorRotationLookup.put(new Vector3f(-1,0,0).toString(), new Vector3f(0,-90,0));
    }

    public HashMap<String, Entity> InitHoles() {
        HashMap<String, Entity> holeEntities = new HashMap<>();
        int prevID = -1;
        for(int holeID = 0; holeID < holes.size(); holeID++) {
            Hole hole = holes.get(holeID);
            if(prevID != -1) {
                Vector3f prevLoc = new Vector3f(holes.get(prevID).getPosition());
                Vector3f holeDir = new Vector3f(holes.get(prevID).getHoleDirection());
                Vector3f newLoc = prevLoc.add(holeDir.mul(30));
                hole.setPosition(newLoc);
                hole.setStartPos(new Vector3f(newLoc.x, hole.getStartPos().y, newLoc.z - 1));
                hole.setRotation(vectorRotationLookup.get(holes.get(prevID).getHoleDirection().toString()));
            }
            holeEntities.put("Ground_" + holeID, hole.getGroundEntity());
            holeEntities.put("Wall_" + holeID, hole.getWallEntity());
            prevID = holeID;
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
}
