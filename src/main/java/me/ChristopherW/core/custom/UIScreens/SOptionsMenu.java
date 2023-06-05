package me.ChristopherW.core.custom.UIScreens;

import java.util.Arrays;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.IGUIScreen;
import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.test.Launcher;

public class SOptionsMenu implements IGUIScreen {

    long monitor = GLFW.glfwGetPrimaryMonitor();
    GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
    Vector2i windowedSize = new Vector2i(0, 0);

    int maxFramerateSelected = 0;
    int[] maxFramerateOptions = {30, 60, 75, 120, 144, 165, 240, 999};
    boolean[] maxFramerateValues = {false, true, false, false, false, false, false, false, false};

    @Override
    public void start() {
        maxFramerateSelected = Arrays.binarySearch(maxFramerateOptions, Launcher.getWindow().monitorRefreshRate);
        System.out.println("MONITOR FRAMERATE: " + Launcher.getWindow().monitorRefreshRate);
        if(maxFramerateSelected == -1)
            maxFramerateSelected = 1;
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        ImGui.setNextWindowSize(0, 0);
        ImGui.setNextWindowPos(Launcher.getWindow().getWidth()/2, Launcher.getWindow().getHeight()/2, 0, 0.5f,0.5f);
        if (ImGui.begin("Options", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoMove)) {
            ImVec2 windowSize = ImGui.getWindowSize();
            String title = "Options";
            float textWidth = ImGui.calcTextSize(title).x;
            ImVec2 textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
            ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
            ImGui.text(title);
            ImGui.dummy(0, 50);
            ImGui.setCursorPosX(0);
            ImGui.pushItemWidth(120);
            if(ImGui.beginCombo("Max FPS", String.valueOf(maxFramerateOptions[maxFramerateSelected]))) {
                for(int i = 0; i < maxFramerateOptions.length; i++) {
                    if(ImGui.selectable(String.valueOf(maxFramerateOptions[i]), i == maxFramerateSelected)) {
                        maxFramerateSelected = i;
                        Constants.FRAMERATE = Constants.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
                        ImGui.setItemDefaultFocus();
                        
                        LogSettings();
                    }
                }
                ImGui.endCombo();
            }
            ImGui.popItemWidth();
            ImGui.dummy(0, 10);
            ImVec2 buttonSize = new ImVec2(200, 50);
            ImVec2 buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.checkbox("Fullscreen", Constants.FULLSCREEN)) {
                Constants.FULLSCREEN = !Constants.FULLSCREEN;
                if(Constants.FULLSCREEN) {
                    windowedSize = new Vector2i(Constants.WIDTH, Constants.HEIGHT);
                    GLFW.glfwSetWindowMonitor(Launcher.getWindow().getWindow(), monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                } else {
                    GLFW.glfwSetWindowMonitor(Launcher.getWindow().getWindow(), MemoryUtil.NULL, 0, 0, windowedSize.x(), windowedSize.y(), 0);
                }
            }
            ImGui.dummy(0, 10);
            buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.checkbox("VSync", Constants.VSYNC)) {
                Constants.VSYNC = !Constants.VSYNC;
                GLFW.glfwSwapInterval(Constants.VSYNC ? 1 : 0);
                Constants.FRAMERATE = Constants.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
                LogSettings();
            }
            ImGui.dummy(0, 10);
            buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.button("Back", buttonSize.x, buttonSize.y)) {
                if(Constants.inGame) {
                    gm.currentScreen = "InGame";
                    GLFW.glfwSetInputMode(Launcher.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                }
                else
                    gm.currentScreen = "MainMenu";
            }
        }
        ImGui.popFont();
        ImGui.end();
    }
    
    void LogSettings() {
        System.out.println("VSYNC: " + (Constants.VSYNC ? "ON" : "OFF"));
        System.out.println("MAX FRAMERATE: " + String.valueOf(maxFramerateOptions[maxFramerateSelected]));
        System.out.println("REAL FRAMERATE: " + String.valueOf(Constants.FRAMERATE));
        Launcher.getEngine().ForceUpdateFramerate();
    }

}
