package me.ChristopherW.test;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import me.ChristopherW.core.*;
import me.ChristopherW.core.custom.Hole;
import me.ChristopherW.core.custom.CourseManager;
import me.ChristopherW.core.custom.GolfBall;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.core.utils.Utils;

import com.jme3.bullet.PhysicsSpace;

import org.joml.Vector3f;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class TestGame implements ILogic {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final CourseManager courseManager;
    PhysicsSpace physicsSpace;


    private Map<String, Entity> entities;
    private Camera camera;
    public static Texture defaultTexture;

    Vector3f cameraInc;

    public TestGame() throws Exception {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        courseManager = new CourseManager();

        physicsSpace = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        physicsSpace.getSolverInfo().setSplitImpulseEnabled(true);
        physicsSpace.setGravity(new com.jme3.math.Vector3f(0, Constants.GRAVITY, 0));
        System.out.println(physicsSpace.getGravity(null));
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        //TextMaster.init(loader);
        //Texture fontAtlas = new Texture(loader.loadTexture("assets/textures/fonts/sourcesanspro.png"));
        //FontType font = new FontType(fontAtlas, new File("assets/textures/fonts/sourcesanspro.fnt"));
        //GUIText text = new GUIText("This is a test text!", 5, font, new Vector2f(0,0), 1f, true);

        defaultTexture = new Texture(loader.loadTexture("assets/textures/default.png"));
        Texture courseTexture1 = new Texture(loader.loadTexture("assets/textures/GolfCourse.png"));
        Texture courseTexture2 = new Texture(loader.loadTexture("assets/textures/GolfCourse2.png"));
        Texture courseTexture3 = new Texture(loader.loadTexture("assets/textures/GolfCourse3.png"));
        Texture ballTexture = new Texture(loader.loadTexture("assets/textures/GolfBall.png"));

        entities = new HashMap<>();

        courseManager.AddHole(new Hole(new Vector3f(0, 0, -1), loader.loadModel("assets/models/ground.obj", courseTexture1), loader.loadModel("assets/models/walls.obj", courseTexture1), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(0, 0, -1), loader.loadModel("assets/models/ground2.obj", courseTexture2), loader.loadModel("assets/models/walls2.obj", courseTexture2), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(1, 0, 0), loader.loadModel("assets/models/ground3.obj", courseTexture3), loader.loadModel("assets/models/walls3.obj", courseTexture3), loader, physicsSpace));
        entities.putAll(courseManager.InitHoles());

        courseManager.AddBall(ballTexture, loader, physicsSpace);
        courseManager.AddBall(defaultTexture, loader, physicsSpace);
        entities.putAll(courseManager.InitBalls());

        Entity previewEntity = new Entity(loader.loadModel("assets/models/ball.obj", ballTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), 0.05f);
        entities.put("Preview", previewEntity);
    }

    final float DIST = 500f;
    float dist = 0;
    float minVel = 0.1f;
    float offset = 1;
    float friction = 1f;
    float rotation = 0;
    Vector3f start = null;
    @Override
    public void input(MouseInput input, double deltaTime, int frame) {
        cameraInc.set(0,0,0);
        GolfBall activeBall = courseManager.GetActiveBall();
        Entity preview = entities.get("Preview");

        rotation -= input.getDisplVec().y * Constants.MOUSE_SENSITIVITY * deltaTime;
        if(window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            radius = Utils.clamp(radius -= 0.0125f, 1, 100);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            radius = Utils.clamp(radius += 0.0125f, 1, 100);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            activeBall.setPosition(courseManager.GetActiveHole().getStartPos());
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_1)) {
            courseManager.SetHole(0);
            activeBall.setPosition(courseManager.GetActiveHole().getStartPos());
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);

        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_2)) {
            courseManager.SetHole(1);
            activeBall.setPosition(courseManager.GetActiveHole().getStartPos());
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_3)) {
            courseManager.SetHole(2);
            activeBall.setPosition(courseManager.GetActiveHole().getStartPos());
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
        }
        if(Math.abs(activeBall.getRigidBody().getLinearVelocity(null).x) < minVel && Math.abs(activeBall.getRigidBody().getLinearVelocity(null).z) < minVel) {
            if(start != null) {
                Vector3f end = activeBall.getPosition();
                float distance = start.distance(end);
                System.out.println(distance);
                start = null;
                courseManager.NextBall();
                return;
            }
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            if(input.isLeftButtonPress()) {
                preview.setPosition(activeBall.getPosition());
                dist += input.getDisplVec().x;
                dist = Utils.clamp(dist, 0, DIST);
                Vector3f orbitVec = new Vector3f();
                orbitVec.x = (float) (dist * -0.01 * Math.sin(Math.toRadians(rotation))) + activeBall.getPosition().x;
                orbitVec.y = 0.4f;
                orbitVec.z = (float) (dist * -0.01 * Math.cos(Math.toRadians(rotation))) + activeBall.getPosition().z;
                preview.setPosition(orbitVec);
                preview.setVisible(true);
            } else {
                preview.setVisible(false);
                if(dist > 0) {
                    dist = Utils.clamp(dist, 0, DIST);
                    preview.localTranslate(0, 0, -0.01f * dist, camera.getRotation());
                    start = new Vector3f(activeBall.getPosition());
                    Vector3f vel = preview.getPosition().sub(activeBall.getPosition()).mul(4f);
                    vel.y = 0;
                    activeBall.getRigidBody().applyCentralImpulse(Utils.convert(vel));
                    activeBall.setStartTime(System.nanoTime());
                    activeBall.setShotStrength(dist / DIST);
                }
                dist = 0;
            }
        }
        if(activeBall.getPosition().y < 0.155f) {
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
            if(courseManager.GetHoleID(courseManager.GetActiveHole()) < courseManager.GetHoleCount() - 1) {
                courseManager.NextHole();
                activeBall.setPosition(courseManager.GetActiveHole().getStartPos());
            }
        }
        for(int i = 0; i < courseManager.GetBallCount(); i++) {
            GolfBall ball = courseManager.GetBall(i);
            com.jme3.math.Vector3f temp = ball.getRigidBody().getLinearVelocity(null);
            ball.getRigidBody().setLinearVelocity(new com.jme3.math.Vector3f((float) (temp.x * (1 / (1 + (deltaTime * friction)))), temp.y, (float) (temp.z * (1 / (1 + (deltaTime * friction))))));
        }

        for(Entity entity : entities.values()) {
            if(entity.getRigidBody() != null) {
                entity.setPosition(Utils.convert(entity.getRigidBody().getPhysicsLocation(null)));

                if(entity instanceof GolfBall)
                    entity.setRotation(Utils.convert(entity.getRigidBody().getLinearVelocity(null)).cross(new Vector3f(0, 1, 0).mul((float) (1/deltaTime))));
            }
        }
        physicsSpace.update((float) deltaTime, 2);
    }
    float radius = 5;
    @Override
    public void update(float inteveral, MouseInput mouseInput) {
        Vector3f orbitVec = new Vector3f();
        orbitVec.x = (float) (radius * Math.sin(Math.toRadians(rotation))) + courseManager.GetActiveBall().getPosition().x;
        orbitVec.y = radius;
        orbitVec.z = (float) (radius * Math.cos(Math.toRadians(rotation))) + courseManager.GetActiveBall().getPosition().z;
        camera.setPosition(orbitVec);
        camera.setRotation(30f, -rotation, camera.getRotation().z);

        for(Entity entity : entities.values()) {
            if(entity.isVisible())
                renderer.processEntity(entity);
        }
    } 

    @Override
    public void render() throws Exception {
        if(window.isResize()) {
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }
        GL11.glClearColor(Constants.BG_COLOR.x, Constants.BG_COLOR.y, Constants.BG_COLOR.z, Constants.BG_COLOR.w);
        renderer.render(camera);

        //TextMaster.render();
    }

    @Override
    public void cleanup() {
        //TextMaster.cleanup();
        renderer.cleanup();
        loader.cleanup();
    }
}
