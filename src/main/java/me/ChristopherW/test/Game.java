package me.ChristopherW.test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;

import com.jme3.bullet.PhysicsSpace;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import me.ChristopherW.core.Camera;
import me.ChristopherW.core.GolfPhysics;
import me.ChristopherW.core.ILogic;
import me.ChristopherW.core.MouseInput;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.RenderManager;
import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.CourseManager;
import me.ChristopherW.core.custom.GolfBall;
import me.ChristopherW.core.custom.Hole;
import me.ChristopherW.core.custom.UIScreens.SInGame;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.sound.SoundBuffer;
import me.ChristopherW.core.sound.SoundListener;
import me.ChristopherW.core.sound.SoundManager;
import me.ChristopherW.core.sound.SoundSource;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Utils;

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

    public Game() throws Exception {
        // create new instances for these things
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        courseManager = new CourseManager();

        // setup sound system
        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);

        // reference the course manager from the gui manager
        window.guiManager.cm = courseManager;

        // create new physics space with custom collision callbacks
        physicsSpace = new GolfPhysics(PhysicsSpace.BroadphaseType.DBVT);
        physicsSpace.getSolverInfo().setSplitImpulseEnabled(true);
        physicsSpace.setGravity(new com.jme3.math.Vector3f(0, GlobalVariables.GRAVITY, 0));

        // create new camera
        camera = new Camera();

        // set setup the sound listener to be at the world origin and load the audio sounds
        soundManager.setListener(new SoundListener(new Vector3f(0, 0, 0)));
        loadSounds();   
    }

    void loadSounds() {
        try {
            // load the sound file to a buffer, then create a new audio source at the world origin with the buffer attached
            // store that sound source to a map of sounds
            // repeat this for each sound file
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

            golfHit1Source.setGain( 0.4f);
            golfHit2Source.setGain( 0.4f);
            golfHit3Source.setGain( 0.4f);
            bounce1Source.setGain(  0.4f);
            bounce2Source.setGain(  0.4f);
            bounce3Source.setGain(  0.4f);
            menuClickSource.setGain(0.4f);
            menuMusicSource.setGain(1f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

        // load all the textures
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

        // initialize entities map
        entities = new HashMap<>();

        // load all the course holes
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

    public void startGame() throws Exception {
        // load some more textures
        Texture shotMeterHeadTexture = new Texture(loader.loadTexture("assets/textures/Arrow.png"));
        Texture shotMeterBaseTexture = new Texture(loader.loadTexture("assets/textures/ArrowBase.png"));
        Texture ballTexture = new Texture(loader.loadTexture("assets/textures/GolfBall.png"));

        // for each player, set the color to the default ball colors and create a new colored texture with that color
        for(int i = 0; i < GlobalVariables.PLAYER_COUNT; i++) {
        Random rand = new Random();
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            Color color = (i < 12 ? GlobalVariables.DEFAULT_BALL_COLORS[i] : new Color(r,g,b));
            Texture texture = new Texture(loader.loadTextureColor(color));
            GolfBall ball = courseManager.AddBall(color, texture, loader, physicsSpace);
            ball.setEnabled(false);
        }

        // set the first player's ball to enabled
        courseManager.GetBall(0).setEnabled(true);

        // update GUI elements
        ((SInGame) window.guiManager.screens.get("InGame")).setHoleText(String.format("Hole %d", 1));
        ((SInGame) window.guiManager.screens.get("InGame")).setPlayerText(String.format("Player %d", 1));
        ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", 0));

        // put all the balls into the entities map
        entities.putAll(courseManager.InitBalls());

        // create shotmeter entities
        Entity arrowBaseEntity = new Entity(loader.loadModel("assets/models/quad.obj", shotMeterBaseTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), new Vector3f(0.4f,1,1), physicsSpace);
        Entity arrowHeadEntity = new Entity(loader.loadModel("assets/models/quad.obj", shotMeterHeadTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), new Vector3f(0.1f,1,0.1f), physicsSpace);
        Entity previewEntity = new Entity(loader.loadModel("assets/models/ball.obj", ballTexture), new Vector3f(0,0.4f,0), new Vector3f(0,0,0), new Vector3f(0.05f,0.05f,0.05f), physicsSpace);
        previewEntity.setVisible(false);
        arrowHeadEntity.setVisible(false);
        arrowBaseEntity.setVisible(false);
        
        // setup shotmeter shaders
        arrowBaseEntity.getModel().setShader(new ShaderManager("/shaders/vertex.glsl", "/shaders/shotmeterFrag.glsl"));
        arrowBaseEntity.getModel().getShader().start();
        arrowBaseEntity.getModel().getShader().createUniform("power");
        arrowBaseEntity.setName("ShotmeterBase");
        arrowHeadEntity.getModel().setShader(new ShaderManager("/shaders/vertex.glsl", "/shaders/shotmeterFrag.glsl"));
        arrowHeadEntity.getModel().getShader().start();
        arrowHeadEntity.getModel().getShader().createUniform("power");
        arrowHeadEntity.setName("ShotmeterHead");

        // add the shotmeter and its components to the map of entities
        entities.put("Preview", previewEntity);
        entities.put("ShotmeterHead", arrowHeadEntity);
        entities.put("ShotmeterBase", arrowBaseEntity);
    }

    final float DIST = 500f; // MAX POWER
    public float dist = 0; // CURRENT POWER 
    float minVel = 0.25f; // VELOCITY THRESHOLD TO ROUND TO ZERO
    float friction = 1f; // FRICTION MULTIPLIER
    public float rotation = 0; // CAMERA ROTATION
    Vector3f start = null; // START POINT FOR A BALL'S JOURNEY

    @Override
    public void input(MouseInput input, double deltaTime, int frame) {
        // if the game hasn't started yet, don't do game inputs
        if(!GlobalVariables.inGame || window.guiManager.currentScreen == "Options" || window.guiManager.currentScreen == "GameOver") {
            return;
        }

        // rotate the camera based on the mouse input
        rotation -= input.getDisplVec().y * GlobalVariables.MOUSE_SENSITIVITY_X;

        // get the current player whos turn it is
        GolfBall activeBall = courseManager.GetActiveBall();
        // get the shot meter entity
        Entity preview = entities.get("Preview");
        Entity shotMeterHead = entities.get("ShotmeterHead");
        Entity shotMeterBase = entities.get("ShotmeterBase");

        // check if the ball is stationary
        if(Math.abs(activeBall.getRigidBody().getLinearVelocity(null).x) < minVel && Math.abs(activeBall.getRigidBody().getLinearVelocity(null).z) < minVel) {
            
            // fully stop the ball
            activeBall.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
            
            // check if the ball just stopped rolling
            if(start != null) {
                start = null;

                // next players turn
                if(!courseManager.finishedBalls.contains(courseManager.GetBallID(activeBall)))
                    courseManager.NextBall();
                activeBall = courseManager.GetActiveBall();
                if(activeBall.isFirstShot()) {
                    activeBall.setEnabled(true);
                    activeBall.setFirstShot(false);
                }

                // update GUI elements
                ((SInGame) window.guiManager.screens.get("InGame")).setHoleText(String.format("Hole %d", activeBall.getCurrentHoleID() + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerText(String.format("Player %d", courseManager.GetBallID(activeBall) + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerID(courseManager.GetBallID(activeBall));
                
                return;
            }
            
            // if mouse button is down, activate the shot meter
            if(input.isLeftButtonPress()) {
                // move an invisible point to where the ball will travel towards
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

                // scale the shot meter based on poewr
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
                // hide the shot meter
                shotMeterHead.setVisible(false);
                shotMeterBase.setVisible(false);

                // if the shot meter had power
                if(dist > 0) {

                    // clamp the power to the max power (500)
                    dist = Utils.clamp(dist, 0, DIST);
                    
                    // save the ball start position
                    preview.localTranslate(0, 0, -0.01f * dist, camera.getRotation());
                    start = new Vector3f(activeBall.getPosition());

                    // calculate the velocity that will be applied to the ball
                    Vector3f vel = preview.getPosition().sub(activeBall.getPosition()).mul(4f);
                    vel.y = 0;
                    activeBall.getRigidBody().applyCentralImpulse(Utils.convert(vel));
                    activeBall.setStartTime(System.nanoTime());

                    // store the shot strength
                    activeBall.setShotStrength(dist / DIST);

                    // update GUI element
                    activeBall.setScore(activeBall.getCurrentHoleID(), activeBall.getScore(activeBall.getCurrentHoleID()) + 1);
                    ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));

                    // play a randomized hit sound
                    Random random = new Random();
                    audioSources.get(String.format("golfHit%d", random.nextInt(3) + 1)).play();
                }
                // reset the power to 0
                dist = 0;
            }
        }

        // for each ball on the field, do some checks
        for(int i = 0; i < courseManager.GetBallCount(); i++) {
            GolfBall ball = courseManager.GetBall(i);

            // ignore disabled balls
            if(!ball.isEnabled())
                continue;

            // if the ball is out of bounds, reset the ball and add a stroke
            if(ball.getPosition().y < -2) {
                ball.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
                ball.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);
                ball.setPosition(courseManager.GetHole(ball.getCurrentHoleID()).getStartPos());
                ball.setScore(ball.getCurrentHoleID(), ball.getScore(ball.getCurrentHoleID()) + 1);
            }

            // check if the ball is in the hole
            if(ball.getPosition().distance(courseManager.GetHole(ball.getCurrentHoleID()).getHolePosition()) < 0.25f) {
                // stop the ball
                ball.getRigidBody().setLinearVelocity(com.jme3.math.Vector3f.ZERO);
                ball.getRigidBody().setAngularVelocity(com.jme3.math.Vector3f.ZERO);

                // if there are no players left for that hole, clear the finished players list
                // then check if the last hole was completed, showing the game over screen 
                if(courseManager.currentBalls.isEmpty()) {
                    courseManager.currentBalls = new ArrayList<>(courseManager.finishedBalls);
                    courseManager.finishedBalls.clear();
                    boolean gameFinished = true;
                    for(int ballIndex : courseManager.currentBalls) {
                        if(courseManager.GetBall(ballIndex).getCurrentHoleID() < courseManager.GetHoleCount() - 1)
                            gameFinished = false;
                    }
                    courseManager.SetActiveBall(courseManager.GetBalls().get(courseManager.currentBalls.get(0)));
                    if(gameFinished) {
                        window.guiManager.currentScreen = "GameOver";
                        GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                    }
                }

                // if there is following hole, move the ball to that hole
                if(ball.getCurrentHoleID() < courseManager.GetHoleCount() - 1) {
                    ball.setCurrentHoleID(ball.getCurrentHoleID() + 1);
                    ball.setPosition(courseManager.GetHole(ball.getCurrentHoleID()).getStartPos());
                }

                // hide the ball 
                ball.setEnabled(false);
                ball.setFirstShot(true);

                // if the ball is my ball, go to the next ball
                if(activeBall == ball)
                    courseManager.NextBall();
                // if the ball isn't in the list of finished players, add it to that list
                if(!courseManager.finishedBalls.contains(courseManager.GetBalls().indexOf(ball)))
                    courseManager.BallFinish(ball);

                // get the new ball of whoever's turn it is
                activeBall = courseManager.GetActiveBall();

                // update GUI elements
                ((SInGame) window.guiManager.screens.get("InGame")).setHoleText(String.format("Hole %d", activeBall.getCurrentHoleID() + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerText(String.format("Player %d", courseManager.GetBallID(activeBall) + 1));
                ((SInGame) window.guiManager.screens.get("InGame")).setPlayerID(courseManager.GetBallID(activeBall));
                ((SInGame) window.guiManager.screens.get("InGame")).setStrokeText(String.format("Strokes: %d", activeBall.getScore(activeBall.getCurrentHoleID())));
                
                // clear the start position
                start = null;

                // show the new player's ball if it is their first time shooting
                if(activeBall.isFirstShot()) {
                    activeBall.setEnabled(true);
                    activeBall.setFirstShot(false);
                }
            }
            // get current velocity
            com.jme3.math.Vector3f temp = ball.getRigidBody().getLinearVelocity(null);
            // use that velocity to apply friction
            ball.getRigidBody().setLinearVelocity(new com.jme3.math.Vector3f((float) (temp.x * (1 / (1 + (deltaTime * friction)))), temp.y, (float) (temp.z * (1 / (1 + (deltaTime * friction))))));
        }


        // for each entity in the world
        // sync the visual rotation and positions with the physics rotations and positions
        for(Entity entity : entities.values()) {
            if(entity.getRigidBody() != null) {
                entity.setPosition(Utils.convert(entity.getRigidBody().getPhysicsLocation(null)));

                if(entity instanceof GolfBall)
                    entity.setRotation(Utils.convert(entity.getRigidBody().getLinearVelocity(null)).cross(new Vector3f(0, 1, 0)).mul(200));
            }
        }

        // update the physics world
        physicsSpace.update((float) deltaTime, 2, false, true, false);
    }


    float radius = 7.5f;
    float theta = 0.0f;
    @Override
    public void update(float inteveral, MouseInput mouseInput) {
        
        // if we are in game
        if(GlobalVariables.inGame) {
            // orbit the camera around the active ball
            Vector3f orbitVec = new Vector3f();
            orbitVec.x = (float) (radius * Math.sin(Math.toRadians(rotation))) + courseManager.GetActiveBall().getPosition().x;
            orbitVec.y = radius;
            orbitVec.z = (float) (radius * Math.cos(Math.toRadians(rotation))) + courseManager.GetActiveBall().getPosition().z;
            camera.setPosition(orbitVec);
            camera.setRotation(30f, -rotation, camera.getRotation().z);
        } else {
            // slowly rotate the camera in the main menu, similar to Minecraft's splash screen
            float speedMultiplier = 2.0f;
            if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) // little easter egg/debug to view all the holes
                speedMultiplier = 1000.0f;
            Vector3f orbitVec = new Vector3f();
            orbitVec.x = (float) (25 * Math.sin(Math.toRadians(theta))) + 20;
            orbitVec.y = 25;
            orbitVec.z = (float) (25 * Math.cos(Math.toRadians(theta))) - 20;
            camera.setPosition(orbitVec);
            camera.setRotation(30f, -theta, camera.getRotation().z);
            theta += inteveral * speedMultiplier;
        }

        // for each visible entity in the world, process its data before rendered
        for(Entity entity : entities.values()) {
            if(entity.isVisible())
                renderer.processEntity(entity);
        }
    } 

    @Override
    public void render() throws Exception {
        // if the window was resized, update the OpenGL viewport to match
        if(window.isResize()) {
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        // set the clear color to the sky color
        GL11.glClearColor(GlobalVariables.BG_COLOR.x, GlobalVariables.BG_COLOR.y, GlobalVariables.BG_COLOR.z, GlobalVariables.BG_COLOR.w);
        
        // render to the OpenGL viewport from the perspective of the camera
        renderer.render(camera);

        // update the render of the ImGui frame
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
