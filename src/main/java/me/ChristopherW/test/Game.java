package me.ChristopherW.test;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import me.ChristopherW.core.*;
import me.ChristopherW.core.custom.Hole;
import me.ChristopherW.core.custom.CourseManager;
import me.ChristopherW.core.custom.GolfBall;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.core.utils.Utils;

import com.jme3.bullet.PhysicsSpace;

import org.joml.Vector3f;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;

public class Game implements ILogic {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final CourseManager courseManager;
    PhysicsSpace physicsSpace;


    private Map<String, Entity> entities;
    private Camera camera;
    public static Texture defaultTexture;

    Vector3f cameraInc;

    public Game() throws Exception {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        courseManager = new CourseManager();
        window.guiManager.setCourseManager(courseManager);
        physicsSpace = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        physicsSpace.getSolverInfo().setSplitImpulseEnabled(true);
        physicsSpace.setGravity(new com.jme3.math.Vector3f(0, Constants.GRAVITY, 0));

        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        defaultTexture = new Texture(loader.loadTexture("assets/textures/DefaultTexture.png"));
        Texture courseTexture1 = new Texture(loader.loadTexture("assets/textures/GolfCourse.png"));
        Texture courseTexture2 = new Texture(loader.loadTexture("assets/textures/GolfCourse2.png"));
        Texture courseTexture3 = new Texture(loader.loadTexture("assets/textures/GolfCourse3.png"));
        Texture courseTexture4 = new Texture(loader.loadTexture("assets/textures/GolfCourse4.png"));
        Texture courseTexture5 = new Texture(loader.loadTexture("assets/textures/GolfCourse5.png"));
        Texture courseTexture6 = new Texture(loader.loadTexture("assets/textures/GolfCourse6.png"));
        Texture shotMeterHeadTexture = new Texture(loader.loadTexture("assets/textures/Arrow.png"));
        Texture shotMeterBaseTexture = new Texture(loader.loadTexture("assets/textures/ArrowBase.png"));
        Texture ballTexture = new Texture(loader.loadTexture("assets/textures/GolfBall.png"));

        entities = new HashMap<>();

        courseManager.AddHole(new Hole(new Vector3f(0, 0, -1), loader.loadModel("assets/models/ground.obj", courseTexture1), loader.loadModel("assets/models/walls.obj", courseTexture1), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(0, 0, -1), loader.loadModel("assets/models/ground2.obj", courseTexture2), loader.loadModel("assets/models/walls2.obj", courseTexture2), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(1, 0, 0), loader.loadModel("assets/models/ground3.obj", courseTexture3), loader.loadModel("assets/models/walls3.obj", courseTexture3), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(1, 0, 0), loader.loadModel("assets/models/ground4.obj", courseTexture4), loader.loadModel("assets/models/walls4.obj", courseTexture4), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(0, 0, 1), loader.loadModel("assets/models/ground5.obj", courseTexture5), loader.loadModel("assets/models/walls5.obj", courseTexture5), loader, physicsSpace));
        courseManager.AddHole(new Hole(2.1f, new Vector3f(-1, 0, 0), loader.loadModel("assets/models/ground6.obj", courseTexture6), loader.loadModel("assets/models/walls6.obj", courseTexture6), loader, physicsSpace));
        courseManager.AddHole(new Hole(2.1f, new Vector3f(-1, 0, 0), loader.loadModel("assets/models/ground6.obj", courseTexture6), loader.loadModel("assets/models/walls6.obj", courseTexture6), loader, physicsSpace));

        entities.putAll(courseManager.InitHoles());

        for(int i = 0; i < Constants.PLAYER_COUNT; i++) {
            Color color = new Color(255,255,255);
            if(Constants.RANDOM_COLORS || i >= 12) {
                Random rand = new Random();
                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();
                color = new Color(r,g,b);
            } else {
                color = Constants.DEFAULT_BALL_COLORS[i];
            }
            Texture texture = new Texture(loader.loadTextureColor(color));
            GolfBall ball = courseManager.AddBall(color, texture, loader, physicsSpace);
            ball.setEnabled(false);
        }

        courseManager.GetBall(0).setEnabled(true);
        window.guiManager.setHoleText(String.format("Hole %d", 1));
        window.guiManager.setPlayerText(String.format("Player %d", 1));
        window.guiManager.setStrokeText(String.format("Strokes: %d", 0));

        entities.putAll(courseManager.InitBalls());

        Entity arrowBaseEntity = new Entity(loader.loadModel("assets/models/quad.obj", shotMeterBaseTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), new Vector3f(0.4f,1,1), physicsSpace);
        Entity arrowHeadEntity = new Entity(loader.loadModel("assets/models/quad.obj", shotMeterHeadTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), new Vector3f(0.1f,1,0.1f), physicsSpace);
        Entity previewEntity = new Entity(loader.loadModel("assets/models/ball.obj", ballTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), new Vector3f(0.05f,0.05f,0.05f), physicsSpace);
        previewEntity.setVisible(false);
        arrowHeadEntity.setVisible(false);
        arrowBaseEntity.setVisible(false);
        entities.put("Preview", previewEntity);
        entities.put("ShotmeterHead", arrowHeadEntity);
        entities.put("ShotmeterBase", arrowBaseEntity);
    }

    final float DIST = 500f;
    float dist = 0;
    float minVel = 0.25f;
    float friction = 1f;
    float rotation = 0;
    Vector3f start = null;
    @Override
    public void input(MouseInput input, double deltaTime, int frame) {
        cameraInc.set(0,0,0);
        GolfBall activeBall = courseManager.GetActiveBall();
        Entity preview = entities.get("Preview");

        rotation -= input.getDisplVec().y * Constants.MOUSE_SENSITIVITY_X * deltaTime;
        if(window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            radius = Utils.clamp(radius -= deltaTime * 10, 1, 100);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            radius = Utils.clamp(radius += deltaTime * 10, 1, 100);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            activeBall.setPosition(courseManager.GetHole(activeBall.getCurrentHoleID()).getStartPos());
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_1))
            activeBall.teleportHole(0, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_2))
            activeBall.teleportHole(1, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_3))
            activeBall.teleportHole(2, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_4))
            activeBall.teleportHole(3, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_5))
            activeBall.teleportHole(4, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_6))
            activeBall.teleportHole(5, courseManager, window);

        Entity shotMeterHead = entities.get("ShotmeterHead");
        Entity shotMeterBase = entities.get("ShotmeterBase");
        if(Math.abs(activeBall.getRigidBody().getLinearVelocity(null).x) < minVel && Math.abs(activeBall.getRigidBody().getLinearVelocity(null).z) < minVel) {
            if(start != null) {
                Vector3f end = activeBall.getPosition();
                float distance = activeBall.getPosition().distance(courseManager.GetHole(activeBall.getCurrentHoleID()).getHolePosition());
                start = null;
                activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
                activeBall.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
                if(!courseManager.finishedBalls.contains(courseManager.GetBallID(activeBall)))
                    courseManager.NextBall();
                System.out.println(courseManager.currentBalls);
                activeBall = courseManager.GetActiveBall();
                window.guiManager.setHoleText(String.format("Hole %d", activeBall.getCurrentHoleID() + 1));
                window.guiManager.setPlayerText(String.format("Player %d", courseManager.GetBallID(activeBall) + 1));
                window.guiManager.setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
                window.guiManager.setPlayerID(courseManager.GetBallID(activeBall));
                if(activeBall.isFirstShot()) {
                    activeBall.setEnabled(true);
                    activeBall.setFirstShot(false);
                }
                return;
            }
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            if(input.isLeftButtonPress()) {
                preview.setPosition(activeBall.getPosition());
                dist += (input.getDisplVec().x * Constants.MOUSE_SENSITIVITY_Y);
                dist = Utils.clamp(dist, 0, DIST);
                Vector3f orbitVec = new Vector3f();
                orbitVec.x = (float) (dist * -0.01 * Math.sin(Math.toRadians(rotation))) + activeBall.getPosition().x;
                orbitVec.y = 0.4f;
                orbitVec.z = (float) (dist * -0.01 * Math.cos(Math.toRadians(rotation))) + activeBall.getPosition().z;
                Vector3f ballLoc = new Vector3f(activeBall.getPosition());
                Vector3f baseOrbit = new Vector3f();
                baseOrbit.x = (float) (dist * -0.005f * Math.sin(Math.toRadians(rotation))) + ballLoc.x;
                baseOrbit.y = ballLoc.y;
                baseOrbit.z = (float) (dist * -0.005f * Math.cos(Math.toRadians(rotation))) + ballLoc.z;
                shotMeterBase.setPosition(baseOrbit);
                shotMeterBase.setRotation(0, rotation, 0);
                shotMeterBase.setScale(0.0795f,1,dist * 0.005f);
                shotMeterBase.setVisible(true);
                Vector3f headOrbit = new Vector3f();
                headOrbit.x = (float) ((dist * -0.01f - 0.15f) * Math.sin(Math.toRadians(rotation))) + ballLoc.x;
                headOrbit.y = ballLoc.y;
                headOrbit.z = (float) ((dist * -0.01f - 0.15f) * Math.cos(Math.toRadians(rotation))) + ballLoc.z;
                shotMeterHead.setPosition(headOrbit);
                shotMeterHead.setRotation(0, rotation, 0);
                if(dist > 0)
                    shotMeterHead.setVisible(true);
                preview.setPosition(orbitVec);
            } else {
                shotMeterHead.setVisible(false);
                shotMeterBase.setVisible(false);
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

                    activeBall.setScore(activeBall.getCurrentHoleID(), activeBall.getScore(activeBall.getCurrentHoleID()) + 1);
                    window.guiManager.setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
                }
                dist = 0;
            }
        }
        for(int i = 0; i < courseManager.GetBallCount(); i++) {
            GolfBall ball = courseManager.GetBall(i);
            if(ball.getPosition().y < -2) {
                ball.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
                ball.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
                ball.setPosition(courseManager.GetHole(ball.getCurrentHoleID()).getStartPos());
                ball.setScore(ball.getCurrentHoleID(), ball.getScore(ball.getCurrentHoleID()) + 1);
            }
            if(ball.getPosition().distance(courseManager.GetHole(ball.getCurrentHoleID()).getHolePosition()) < 0.25f) {
                ball.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
                ball.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
                if(ball.getCurrentHoleID() < courseManager.GetHoleCount() - 1) {
                    ball.setPosition(courseManager.GetHole(ball.getCurrentHoleID()).getStartPos());
                }
                ball.setCurrentHoleID(ball.getCurrentHoleID() + 1);
                ball.setEnabled(false);
                ball.setFirstShot(true);
                System.out.println(courseManager.currentBalls);
                courseManager.NextBall();
                if(!courseManager.finishedBalls.contains(courseManager.GetBalls().indexOf(ball)))
                    courseManager.BallFinish(ball);
                if(courseManager.currentBalls.isEmpty()) {
                    courseManager.currentBalls = new ArrayList<>(courseManager.finishedBalls);
                    courseManager.finishedBalls.clear();
                    courseManager.SetActiveBall(courseManager.GetBalls().get(courseManager.currentBalls.get(0)));
                }
                activeBall = courseManager.GetActiveBall();
                window.guiManager.setHoleText(String.format("Hole %d", activeBall.getCurrentHoleID() + 1));
                window.guiManager.setPlayerText(String.format("Player %d", courseManager.GetBallID(activeBall) + 1));
                window.guiManager.setPlayerID(courseManager.GetBallID(activeBall));
                window.guiManager.setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
                start = null;
                if(activeBall.isFirstShot()) {
                    activeBall.setEnabled(true);
                    activeBall.setFirstShot(false);
                }
            }
            com.jme3.math.Vector3f temp = ball.getRigidBody().getLinearVelocity(null);
            ball.getRigidBody().setLinearVelocity(new com.jme3.math.Vector3f((float) (temp.x * (1 / (1 + (deltaTime * friction)))), temp.y, (float) (temp.z * (1 / (1 + (deltaTime * friction))))));
        }

        for(Entity entity : entities.values()) {
            if(entity.getRigidBody() != null) {
                entity.setPosition(Utils.convert(entity.getRigidBody().getPhysicsLocation(null)));

                if(entity instanceof GolfBall)
                    entity.setRotation(Utils.convert(entity.getRigidBody().getLinearVelocity(null)).cross(new Vector3f(0, 1, 0)).mul((float) (1/deltaTime)));
            }
        }
        physicsSpace.update((float) deltaTime, 2);
    }
    float radius = 7.5f;
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
        window.imGuiGlfw.newFrame();
        ImGui.newFrame();
        window.guiManager.render();
        ImGui.render();
        window.imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
        GLFW.glfwPollEvents();
    }

    @Override
    public void cleanup() {
        //TextMaster.cleanup();
        renderer.cleanup();
        loader.cleanup();
    }
}