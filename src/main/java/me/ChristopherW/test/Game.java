package me.ChristopherW.test;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import me.ChristopherW.core.*;
import me.ChristopherW.core.custom.Hole;
import me.ChristopherW.core.custom.CourseManager;
import me.ChristopherW.core.custom.GolfBall;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.sound.SoundBuffer;
import me.ChristopherW.core.sound.SoundListener;
import me.ChristopherW.core.sound.SoundManager;
import me.ChristopherW.core.sound.SoundSource;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.core.custom.UIScreens.SInGame;

import com.jme3.bullet.PhysicsSpace;

import org.joml.Vector3f;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;

public class Game implements ILogic {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    public final CourseManager courseManager;
    private final SoundManager soundManager;
    PhysicsSpace physicsSpace;

    public HashMap<String, SoundSource> audioSources = new HashMap<>();

    public Map<String, Entity> entities;
    private Camera camera;
    public static Texture defaultTexture;

    Vector3f cameraInc;

    public Game() throws Exception {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        courseManager = new CourseManager();
        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);

        window.guiManager.cm = courseManager;
        physicsSpace = new GolfPhysics(PhysicsSpace.BroadphaseType.DBVT);
        physicsSpace.getSolverInfo().setSplitImpulseEnabled(true);
        physicsSpace.setGravity(new com.jme3.math.Vector3f(0, GlobalVariables.GRAVITY, 0));

        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        soundManager.setListener(new SoundListener(new Vector3f(0, 0, 0)));
        loadSounds();   
    }

    void loadSounds() {
        try {
            SoundBuffer golfHit1Buffer = new SoundBuffer("assets/sounds/golfHit1.ogg");
            soundManager.addSoundBuffer(golfHit1Buffer);
            SoundSource golfHit1Source = new SoundSource(false, false);
            golfHit1Source.setPosition(new Vector3f(0,0,0));
            golfHit1Source.setBuffer(golfHit1Buffer.getBufferId());
            audioSources.put("golfHit1", golfHit1Source);
            soundManager.addSoundSource("golfHit1", golfHit1Source);

            SoundBuffer golfHit2Buffer = new SoundBuffer("assets/sounds/golfHit2.ogg");
            soundManager.addSoundBuffer(golfHit2Buffer);
            SoundSource golfHit2Source = new SoundSource(false, false);
            golfHit2Source.setPosition(new Vector3f(0,0,0));
            golfHit2Source.setBuffer(golfHit2Buffer.getBufferId());
            audioSources.put("golfHit2", golfHit2Source);
            soundManager.addSoundSource("golfHit2", golfHit2Source);

            SoundBuffer golfHit3Buffer = new SoundBuffer("assets/sounds/golfHit3.ogg");
            soundManager.addSoundBuffer(golfHit3Buffer);
            SoundSource golfHit3Source = new SoundSource(false, false);
            golfHit3Source.setPosition(new Vector3f(0,0,0));
            golfHit3Source.setBuffer(golfHit3Buffer.getBufferId());
            audioSources.put("golfHit3", golfHit3Source);
            soundManager.addSoundSource("golfHit3", golfHit3Source);

            SoundBuffer bounce1Buffer = new SoundBuffer("assets/sounds/bounce1.ogg");
            soundManager.addSoundBuffer(bounce1Buffer);
            SoundSource bounce1Source = new SoundSource(false, false);
            golfHit3Source.setPosition(new Vector3f(0,0,0));
            bounce1Source.setBuffer(bounce1Buffer.getBufferId());
            audioSources.put("bounce1", bounce1Source);
            soundManager.addSoundSource("bounce1", bounce1Source);

            SoundBuffer bounce2Buffer = new SoundBuffer("assets/sounds/bounce2.ogg");
            soundManager.addSoundBuffer(bounce2Buffer);
            SoundSource bounce2Source = new SoundSource(false, false);
            golfHit3Source.setPosition(new Vector3f(0,0,0));
            bounce2Source.setBuffer(bounce2Buffer.getBufferId());
            audioSources.put("bounce2", bounce2Source);
            soundManager.addSoundSource("bounce2", bounce2Source);

            SoundBuffer bounce3Buffer = new SoundBuffer("assets/sounds/bounce3.ogg");
            soundManager.addSoundBuffer(bounce3Buffer);
            SoundSource bounce3Source = new SoundSource(false, false);
            golfHit3Source.setPosition(new Vector3f(0,0,0));
            bounce3Source.setBuffer(bounce3Buffer.getBufferId());
            audioSources.put("bounce3", bounce3Source);
            soundManager.addSoundSource("bounce3", bounce3Source);

            SoundBuffer menuClickBuffer = new SoundBuffer("assets/sounds/menuClick.ogg");
            soundManager.addSoundBuffer(menuClickBuffer);
            SoundSource menuClickSource = new SoundSource(false, false);
            menuClickSource.setPosition(new Vector3f(0,0,0));
            menuClickSource.setBuffer(menuClickBuffer.getBufferId());
            audioSources.put("menuClick", menuClickSource);
            soundManager.addSoundSource("menuClick", menuClickSource);

            SoundBuffer menuMusicBuffer = new SoundBuffer("assets/sounds/menuMusic.ogg");
            soundManager.addSoundBuffer(menuMusicBuffer);
            SoundSource menuMusicSource = new SoundSource(true, true);
            menuMusicSource.setPosition(new Vector3f(0,0,0));
            menuMusicSource.setBuffer(menuMusicBuffer.getBufferId());
            audioSources.put("menuMusic", menuMusicSource);
            soundManager.addSoundSource("menuMusic", menuMusicSource);

            golfHit1Source.setGain(0.1f);
            golfHit2Source.setGain(0.1f);
            golfHit3Source.setGain(0.1f);
            bounce1Source.setGain(0.1f);
            bounce2Source.setGain(0.1f);
            bounce3Source.setGain(0.1f);
            menuClickSource.setGain(0.1f);
            menuMusicSource.setGain(0.3f);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Texture courseTexture7 = new Texture(loader.loadTexture("assets/textures/GolfCourse7.png"));
        Texture courseTexture8 = new Texture(loader.loadTexture("assets/textures/GolfCourse8.png"));
        Texture courseTexture9 = new Texture(loader.loadTexture("assets/textures/GolfCourse9.png"));

        entities = new HashMap<>();

        courseManager.AddHole(new Hole(new Vector3f(0, 0, -1), loader.loadModel("assets/models/ground.obj", courseTexture1), loader.loadModel("assets/models/walls.obj", courseTexture1), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(0, 0, -1), loader.loadModel("assets/models/ground2.obj", courseTexture2), loader.loadModel("assets/models/walls2.obj", courseTexture2), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(1, 0, 0), loader.loadModel("assets/models/ground3.obj", courseTexture3), loader.loadModel("assets/models/walls3.obj", courseTexture3), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(1, 0, 0), loader.loadModel("assets/models/ground4.obj", courseTexture4), loader.loadModel("assets/models/walls4.obj", courseTexture4), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(0, 0, 1), loader.loadModel("assets/models/ground5.obj", courseTexture5), loader.loadModel("assets/models/walls5.obj", courseTexture5), loader, physicsSpace));
        courseManager.AddHole(new Hole(2.1f, new Vector3f(-1, 0, 0), loader.loadModel("assets/models/ground6.obj", courseTexture6), loader.loadModel("assets/models/walls6.obj", courseTexture6), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(-1, 0, 0), loader.loadModel("assets/models/ground7.obj", courseTexture7), loader.loadModel("assets/models/walls7.obj", courseTexture7), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(0, 0, 1), loader.loadModel("assets/models/ground8.obj", courseTexture8), loader.loadModel("assets/models/walls8.obj", courseTexture8), loader, physicsSpace));
        courseManager.AddHole(new Hole(new Vector3f(-1, 0, 0), loader.loadModel("assets/models/ground9.obj", courseTexture9), loader.loadModel("assets/models/walls9.obj", courseTexture9), loader, physicsSpace));
        entities.putAll(courseManager.InitHoles());
    }

    SoundSource getRandomHitSound() {
        Random random = new Random();
        switch(random.nextInt(3)) {
            case 0:
                return audioSources.get("golfHit1");
            case 1:
                return audioSources.get("golfHit2");
            case 2:
                return audioSources.get("golfHit3");
        }
        return audioSources.get("golfHit1");
    }

    public void startGame() throws Exception {
        Texture shotMeterHeadTexture = new Texture(loader.loadTexture("assets/textures/Arrow.png"));
        Texture shotMeterBaseTexture = new Texture(loader.loadTexture("assets/textures/ArrowBase.png"));
        Texture ballTexture = new Texture(loader.loadTexture("assets/textures/GolfBall.png"));

        for(int i = 0; i < GlobalVariables.PLAYER_COUNT; i++) {
            Color color = new Color(255,255,255);
            if(GlobalVariables.RANDOM_COLORS || i >= 12) {
                Random rand = new Random();
                float r = rand.nextFloat();
                float g = rand.nextFloat();
                float b = rand.nextFloat();
                color = new Color(r,g,b);
            } else {
                color = GlobalVariables.DEFAULT_BALL_COLORS[i];
            }
            Texture texture = new Texture(loader.loadTextureColor(color));
            GolfBall ball = courseManager.AddBall(color, texture, loader, physicsSpace);
            ball.setEnabled(false);
        }

        courseManager.GetBall(0).setEnabled(true);
        ((SInGame) window.guiManager.screens.get("InGame")).setHoleText(String.format("Hole %d", 1));
        ((SInGame) window.guiManager.screens.get("InGame")).setPlayerText(String.format("Player %d", 1));
        ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", 0));

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
        if(!GlobalVariables.inGame || window.guiManager.currentScreen == "Options") {
            return;
        }

        cameraInc.set(0,0,0);
        GolfBall activeBall = courseManager.GetActiveBall();
        Entity preview = entities.get("Preview");

        rotation -= input.getDisplVec().y * GlobalVariables.MOUSE_SENSITIVITY_X;
        if(window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            radius = Utils.clamp(radius -= deltaTime * 10, 1, 100);
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            radius = Utils.clamp(radius += deltaTime * 10, 1, 100);
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
        if(window.isKeyPressed(GLFW.GLFW_KEY_7))
            activeBall.teleportHole(6, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_8))
            activeBall.teleportHole(7, courseManager, window);
        if(window.isKeyPressed(GLFW.GLFW_KEY_9))
            activeBall.teleportHole(8, courseManager, window);

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
                activeBall = courseManager.GetActiveBall();
                ((SInGame) window.guiManager.screens.get("InGame")).setHoleText(String.format("Hole %d", activeBall.getCurrentHoleID() + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerText(String.format("Player %d", courseManager.GetBallID(activeBall) + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerID(courseManager.GetBallID(activeBall));
                if(activeBall.isFirstShot()) {
                    activeBall.setEnabled(true);
                    activeBall.setFirstShot(false);
                }
                return;
            }
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            if(input.isLeftButtonPress()) {
                preview.setPosition(activeBall.getPosition());
                dist += (input.getDisplVec().x * GlobalVariables.MOUSE_SENSITIVITY_Y);
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
                    ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));

                    getRandomHitSound().play();
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
                    ball.setCurrentHoleID(ball.getCurrentHoleID() + 1);
                    ball.setPosition(courseManager.GetHole(ball.getCurrentHoleID()).getStartPos());
                }
                ball.setEnabled(false);
                ball.setFirstShot(true);
                if(activeBall == ball)
                    courseManager.NextBall();
                if(!courseManager.finishedBalls.contains(courseManager.GetBalls().indexOf(ball)))
                    courseManager.BallFinish(ball);
                if(courseManager.currentBalls.isEmpty()) {
                    courseManager.currentBalls = new ArrayList<>(courseManager.finishedBalls);
                    courseManager.finishedBalls.clear();
                    courseManager.SetActiveBall(courseManager.GetBalls().get(courseManager.currentBalls.get(0)));
                }
                activeBall = courseManager.GetActiveBall();
                ((SInGame) window.guiManager.screens.get("InGame")).setHoleText(String.format("Hole %d", activeBall.getCurrentHoleID() + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerText(String.format("Player %d", courseManager.GetBallID(activeBall) + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerID(courseManager.GetBallID(activeBall));
                ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
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
                    entity.setRotation(Utils.convert(entity.getRigidBody().getLinearVelocity(null)).cross(new Vector3f(0, 1, 0)).mul(200));
            }
        }
        physicsSpace.update((float) deltaTime, 2, false, true, false);
    }
    float radius = 7.5f;
    float theta = 0.0f;
    @Override
    public void update(float inteveral, MouseInput mouseInput) {
        
        if(GlobalVariables.inGame) {
            Vector3f orbitVec = new Vector3f();
            orbitVec.x = (float) (radius * Math.sin(Math.toRadians(rotation))) + courseManager.GetActiveBall().getPosition().x;
            orbitVec.y = radius;
            orbitVec.z = (float) (radius * Math.cos(Math.toRadians(rotation))) + courseManager.GetActiveBall().getPosition().z;
            camera.setPosition(orbitVec);
            camera.setRotation(30f, -rotation, camera.getRotation().z);
        } else {
            float multiplier = 2.0f;
            if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE))
                multiplier = 1000.0f;
            Vector3f orbitVec = new Vector3f();
            orbitVec.x = (float) (25 * Math.sin(Math.toRadians(theta))) + 20;
            orbitVec.y = 25;
            orbitVec.z = (float) (25 * Math.cos(Math.toRadians(theta))) - 20;
            camera.setPosition(orbitVec);
            camera.setRotation(30f, -theta, camera.getRotation().z);
            theta += inteveral * multiplier;
        }

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
        GL11.glClearColor(GlobalVariables.BG_COLOR.x, GlobalVariables.BG_COLOR.y, GlobalVariables.BG_COLOR.z, GlobalVariables.BG_COLOR.w);
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
        renderer.cleanup();
        loader.cleanup();
    }
}
