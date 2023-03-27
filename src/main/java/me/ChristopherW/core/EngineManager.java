package me.ChristopherW.core;

import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.test.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;

    private static int fps;
    private static int tps;
    private static float frametime = 1.0f / Constants.FRAMERATE;

    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
    }
    
    public void start() throws Exception {
        init();
        if(isRunning)
            return;
        run();
    }
    public void run() throws Exception {
        this.isRunning = true;
        int totalFrames = 0;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while(isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            while(unprocessedTime > frametime) {
                render = true;
                unprocessedTime -= frametime;

                if(window.windowShouldClose())
                    stop();

                if(frameCounter >= NANOSECOND) {
                    setFps(frames);
                    window.setTitle(Constants.TITLE + " - FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if(render) {
                update(frametime);
                render();


                input(getFps() == 0 ? 0 : 1f / getFps(), totalFrames);
                frames++;
                totalFrames++;
            }
        }
        cleanup();
    }

    private void stop() {
        if(!isRunning)
            return;
        isRunning = false;
    }

    private void input(double deltaTime, int frame) {
        gameLogic.input(mouseInput, deltaTime, frame);
        mouseInput.input();
    }

    private void render() throws Exception {
        gameLogic.render();
        window.update();
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        gameLogic.cleanup();
        window.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
    public static int getTps() {
        return tps;
    }

    public static void setTps(int tps) {
        EngineManager.tps = tps;
    }
}
