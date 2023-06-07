package me.ChristopherW.core.custom.UIScreens;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.IGUIScreen;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.test.Launcher;

public class SOptionsMenu implements IGUIScreen {

    long monitor = GLFW.glfwGetPrimaryMonitor();
    GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

    int resolutionSelected = 0;
    Resolution[] resolutionOptions = {
        new Resolution(640, 360),
        new Resolution(1280, 720),
        new Resolution(1280, 800),
        new Resolution(1440, 900),
        new Resolution(1600, 900),
        new Resolution(1920, 1080),
        new Resolution(1920, 1200),
        new Resolution(2560, 1440),
        new Resolution(2560, 1600),
        new Resolution(3840, 2160)
    };
    boolean[] resolutionValues = {false, true, false, false, false, false, false, false, false, false};

    int maxFramerateSelected = 0;
    int[] maxFramerateOptions = {30, 60, 75, 120, 144, 165, 240, 9999};
    boolean[] maxFramerateValues = {false, true, false, false, false, false, false, false, false};

    int indexOf(int[] array, int check) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == check)
                return i;
        }
        return -1;
    }

    int getResolutionIndex(Resolution[] array, Resolution check) {
        for(int i = 0; i < array.length; i++) {
            if(array[i].height == check.height)
                if(array[i].width == check.width)
                    return i;
        }
        return -1;
    }

    <T> int indexOf(T[] array, T check) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == check)
                return i;
        }
        return -1;
    }

    @Override
    public void start() {
        maxFramerateSelected = indexOf(maxFramerateOptions, Launcher.getWindow().monitorRefreshRate);
        if(maxFramerateSelected == -1)
            maxFramerateSelected = 1;

        resolutionSelected = getResolutionIndex(resolutionOptions, new Resolution(Launcher.getWindow().getWidth(), Launcher.getWindow().getHeight()));
        if(resolutionSelected == -1);
            resolutionSelected = 1;
        GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
        
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        ImGui.setNextWindowSize(0, 0);
        ImGui.setNextWindowPos(gm.window.getWidth()/2, gm.window.getHeight()/2, 0, 0.5f,0.5f);
        if (ImGui.begin("Options", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoMove)) {
            ImVec2 windowSize = ImGui.getWindowSize();
            String title = "Options";
            float textWidth = ImGui.calcTextSize(title).x;
            ImVec2 textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
            ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
            ImGui.text(title);
            ImGui.dummy(0, 50);
            ImGui.pushItemWidth(250);
            ImGui.setNextWindowBgAlpha(0.4f);
            if(ImGui.beginCombo("Resolution", String.format("%dx%d", resolutionOptions[resolutionSelected].width, resolutionOptions[resolutionSelected].height))) {
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                for(int i = 0; i < resolutionOptions.length; i++) {
                    if(ImGui.selectable(String.format("%dx%d", resolutionOptions[i].width, resolutionOptions[i].height))) {
                        resolutionSelected = i;
                        ImGui.setItemDefaultFocus();
                        if(GlobalVariables.FULLSCREEN) {
                            GLFW.glfwSetWindowMonitor(gm.window.getWindow(), monitor, 0, 0, resolutionOptions[resolutionSelected].width, resolutionOptions[resolutionSelected].height, mode.refreshRate());
                        } else {
                            IntBuffer xbuf = BufferUtils.createIntBuffer(1);
                            IntBuffer ybuf = BufferUtils.createIntBuffer(1);
                            GLFW.glfwGetWindowPos(gm.window.getWindow(), xbuf, ybuf);
                            gm.window.winPos = new Vector2i(xbuf.get(0), ybuf.get(0));
                            GLFW.glfwSetWindowMonitor(gm.window.getWindow(), MemoryUtil.NULL, gm.window.winPos.x, gm.window.winPos.y, resolutionOptions[resolutionSelected].width, resolutionOptions[resolutionSelected].height, 0);
                        }
                        TriggerUpdate();
                    }
                }
                ImGui.endCombo();
            }
            if(ImGui.isItemHovered()) {
                ImGui.setNextWindowBgAlpha(0.4f);
                ImGui.pushStyleVar(ImGuiStyleVar.PopupBorderSize, 0.0f);
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                ImGui.setTooltip("Changes the resolution of the window");
                ImGui.popFont();
                ImGui.pushFont(gm.font);
                ImGui.popStyleVar();
            }
            ImGui.popFont();
            ImGui.pushFont(gm.font);
            ImGui.dummy(0,10);
            if(GlobalVariables.VSYNC)
                ImGui.beginDisabled();
            ImGui.pushItemWidth(maxFramerateSelected == maxFramerateOptions.length - 1 ? 250 : 125);
            ImGui.setNextWindowBgAlpha(0.4f);
            if(ImGui.beginCombo("Max Framerate", maxFramerateSelected == maxFramerateOptions.length - 1 ? "Unlimited" : String.valueOf(maxFramerateOptions[maxFramerateSelected]))) {
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                for(int i = 0; i < maxFramerateOptions.length; i++) {
                    if(ImGui.selectable(i == maxFramerateOptions.length - 1 ? "Unlimited" : String.valueOf(maxFramerateOptions[i]), i == maxFramerateSelected)) {
                        maxFramerateSelected = i;
                        GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
                        ImGui.setItemDefaultFocus();
                        
                        TriggerUpdate();
                    }
                }
                ImGui.endCombo();
            }
            if(ImGui.isItemHovered()) {
                ImGui.setNextWindowBgAlpha(0.4f);
                ImGui.pushStyleVar(ImGuiStyleVar.PopupBorderSize, 0.0f);
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                ImGui.setTooltip("Limits the framerate of the game");
                ImGui.popFont();
                ImGui.pushFont(gm.font);
                ImGui.popStyleVar();
            }
            if(GlobalVariables.VSYNC)
                ImGui.endDisabled();
            ImGui.popItemWidth();
            ImGui.popFont();
            ImGui.pushFont(gm.font);
            ImGui.dummy(0, 10);
            ImVec2 buttonSize = new ImVec2(200, 50);
            ImVec2 buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.checkbox("Fullscreen", GlobalVariables.FULLSCREEN)) {
                GlobalVariables.FULLSCREEN = !GlobalVariables.FULLSCREEN;
                if(GlobalVariables.FULLSCREEN) {
                    gm.window.winSize = new Vector2i(GlobalVariables.WIDTH, GlobalVariables.HEIGHT);
                    IntBuffer xbuf = BufferUtils.createIntBuffer(1);
                    IntBuffer ybuf = BufferUtils.createIntBuffer(1);
                    GLFW.glfwGetWindowPos(gm.window.getWindow(), xbuf, ybuf);
                    gm.window.winPos = new Vector2i(xbuf.get(0), ybuf.get(0));
                    GLFW.glfwSetWindowMonitor(gm.window.getWindow(), monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                    resolutionSelected = getResolutionIndex(resolutionOptions, new Resolution(mode.width(), mode.height()));
                } else {
                    GLFW.glfwSetWindowMonitor(gm.window.getWindow(), MemoryUtil.NULL, gm.window.winPos.x, gm.window.winPos.y, gm.window.winSize.x(), gm.window.winSize.y(), 0);
                    resolutionSelected = getResolutionIndex(resolutionOptions, new Resolution(gm.window.winSize.x(), gm.window.winSize.y()));
                }
            }
            if(ImGui.isItemHovered()) {
                ImGui.pushStyleVar(ImGuiStyleVar.PopupBorderSize, 0.0f);
                ImGui.setNextWindowBgAlpha(0.4f);
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                ImGui.setTooltip("Maximizes the window to take up the entire screen");
                ImGui.popFont();
                ImGui.pushFont(gm.font);
                ImGui.popStyleVar();
            }
            ImGui.dummy(0, 10);
            buttonSize = new ImVec2(200, 50);
            buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.checkbox("Show FPS", GlobalVariables.SHOW_FPS)) {
                GlobalVariables.SHOW_FPS = !GlobalVariables.SHOW_FPS;
            }
            if(ImGui.isItemHovered()) {
                ImGui.pushStyleVar(ImGuiStyleVar.PopupBorderSize, 0.0f);
                ImGui.setNextWindowBgAlpha(0.4f);
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                ImGui.setTooltip("Displays the games current frames per second in the top left of the screen");
                ImGui.popFont();
                ImGui.pushFont(gm.font);
                ImGui.popStyleVar();
            }
            ImGui.dummy(0, 10);
            buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.checkbox("VSync", GlobalVariables.VSYNC)) {
                GlobalVariables.VSYNC = !GlobalVariables.VSYNC;
                GLFW.glfwSwapInterval(GlobalVariables.VSYNC ? 1 : 0);
                GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
                TriggerUpdate();
            }
            if(ImGui.isItemHovered()) {
                ImGui.pushStyleVar(ImGuiStyleVar.PopupBorderSize, 0.0f);
                ImGui.setNextWindowBgAlpha(0.4f);
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                ImGui.setTooltip("Syncs the framerate to the your monitor's refresh rate.\nNote: this setting overrides your framerate limit");
                ImGui.popFont();
                ImGui.pushFont(gm.font);
                ImGui.popStyleVar();
            }
            ImGui.dummy(0, 10);
            buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.button("Back", buttonSize.x, buttonSize.y)) {
                if(GlobalVariables.inGame) {
                    gm.currentScreen = "InGame";
                    GLFW.glfwSetInputMode(gm.window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                }
                else
                    gm.currentScreen = "MainMenu";
            }
        }
        ImGui.popFont();
        ImGui.end();
    }
    
    void TriggerUpdate() {
        //System.out.println("VSYNC: " + (GlobalVariables.VSYNC ? "ON" : "OFF"));
        //System.out.println("MAX FRAMERATE: " + String.valueOf(maxFramerateOptions[maxFramerateSelected]));
        //System.out.println("REAL FRAMERATE: " + String.valueOf(GlobalVariables.FRAMERATE));
        Launcher.getEngine().ForceUpdateFramerate();
    }

}
