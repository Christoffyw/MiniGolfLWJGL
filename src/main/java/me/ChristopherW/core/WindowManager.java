package me.ChristopherW.core;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.UIScreens.Resolution;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.test.Launcher;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.rpmalloc.RPmallocThreadStatistics.size_use;

public class WindowManager {
    public final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    public GUIManager guiManager;
    private String glslVersion = null;
    private String title;

    private int width, height;
    private long window;

    public Resolution monitorResolution = null;
    public int monitorRefreshRate = -1;
    public Vector2i winPos;
    public Vector2i winSize;

    private boolean resize, vSync;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        projectionMatrix = new Matrix4f();
        guiManager = new GUIManager(this);
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glslVersion = "#version 410";

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);


        monitorResolution = new Resolution(mode.width(), mode.height());
        monitorRefreshRate = mode.refreshRate();
        boolean maximized = false;
        if(width == 0 || height == 0 || GlobalVariables.FULLSCREEN) {
            width = mode.width();
            height = mode.height();
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, mode.redBits());
            GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, mode.greenBits());
            GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, mode.blueBits());
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, mode.refreshRate());
            maximized = true;
        }
        if(maximized) {
            window = GLFW.glfwCreateWindow(mode.width(), mode.height(), title, monitor, MemoryUtil.NULL);
            GLFW.glfwMaximizeWindow(window);
        }
        else {
            window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
            GLFW.glfwSetWindowPos(window, (mode.width() - width) / 2, (mode.height() - height) / 2);
        }
        if(window == MemoryUtil.NULL)
            throw new IllegalStateException("Failed to create GLFW window");

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });

        ObjectLoader loader = new ObjectLoader();
        GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
        imagebf.put(0, loader.loadtextureBuffer(GlobalVariables.ICON_PATH));
        GLFW.glfwSetWindowIcon(window, imagebf);
        GLFW.glfwMakeContextCurrent(window);

        if(vSync) {
            GLFW.glfwSwapInterval(1);
            GlobalVariables.FRAMERATE = mode.refreshRate();
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
                if(!GlobalVariables.inGame)
                    return;
                if(guiManager.currentScreen == "Options") {
                    guiManager.currentScreen = "InGame";
                    GLFW.glfwSetInputMode(getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                } else {
                    guiManager.currentScreen = "Options";
                    GLFW.glfwSetInputMode(getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
                }
    
            }
        });

        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        //Callback glDebugCallback = GLUtil.setupDebugMessageCallback();

        GL11.glClearColor(0f,0f,0f,0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void initImGui() {
        ImGui.createContext();
        guiManager.init();
    }

    public void init() {
        initWindow();
        initImGui();
        imGuiGlfw.init(window, true);
        imGuiGl3.init(glslVersion);
    }

    public void update() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void cleanup() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        GLFW.glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }
    public boolean isKeyReleased(int keycode) {
        return GLFW.glfwGetKey(window, keycode) == GLFW.GLFW_RELEASE;
    }
    public boolean isKeyPressed(int keycode) {
        return GLFW.glfwGetKey(window, keycode) == GLFW.GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(window, title);
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / height;
        return projectionMatrix.setPerspective(GlobalVariables.FOV, aspectRatio, GlobalVariables.Z_NEAR, GlobalVariables.Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / height;
        return matrix.setPerspective(GlobalVariables.FOV, aspectRatio, GlobalVariables.Z_NEAR, GlobalVariables.Z_FAR);
    }
}
