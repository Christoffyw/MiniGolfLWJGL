package me.ChristopherW.core.custom.UIScreens;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.prefs.Preferences;

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
import imgui.flag.ImGuiSliderFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.IGUIScreen;
import me.ChristopherW.core.sound.SoundSource;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.test.Launcher;

public class SOptionsMenu implements IGUIScreen {

    Preferences preferences = Preferences.userRoot().node(Launcher.class.getName());
    final String ID_MASTER_VOLUME = "MasterVolume";
    final String ID_RESOLUTION = "DisplayResolution";
    final String ID_FRAMERATE = "MaximumFramerate";
    final String ID_FULLSCREEN = "Fullscreen";
    final String ID_SHOW_FPS = "ShowFPS";
    final String ID_VSYNC = "Vsync";

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

    float[] masterVolume = new float[]{100f};
    float[] defaultGains = new float[Launcher.getGame().audioSources.keySet().size()];

    @Override
    public void start() {
        masterVolume[0] = preferences.getFloat(ID_MASTER_VOLUME, 100f);

        maxFramerateSelected = Utils.indexOf(maxFramerateOptions, preferences.getInt(ID_FRAMERATE, Launcher.getWindow().monitorRefreshRate));
        if(maxFramerateSelected == -1)
            maxFramerateSelected = 1;

        String detectedResolutionString = String.format("%dx%d", Launcher.getWindow().getWidth(), Launcher.getWindow().getHeight());
        resolutionSelected = Utils.getResolutionIndex(resolutionOptions, GlobalVariables.FULLSCREEN ? Launcher.getWindow().monitorResolution : new Resolution(preferences.get(ID_RESOLUTION, detectedResolutionString)));
        if(resolutionSelected < 0) {
            System.out.println("Failed to find matching resolution");
            resolutionSelected = -1;
        }

        GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
        
        GlobalVariables.SHOW_FPS = preferences.getBoolean(ID_SHOW_FPS, false);
        GlobalVariables.VSYNC = preferences.getBoolean(ID_VSYNC, false);
        if(GlobalVariables.VSYNC) {
            GLFW.glfwSwapInterval(GlobalVariables.VSYNC ? 1 : 0);
            GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
            Launcher.getEngine().ForceUpdateFramerate();
        }

        int i = 0;
        for(String key : Launcher.getGame().audioSources.keySet()) {
            SoundSource src = Launcher.getGame().audioSources.get(key);
            defaultGains[i] = src.getGain();
            src.setGain(defaultGains[i] * masterVolume[0]/100);
            i++;
        }
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
            ImGui.popFont();
            ImGui.pushFont(gm.fontSmall);
            title = "Sound";
            textWidth = ImGui.calcTextSize(title).x;
            textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
            ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
            ImGui.text(title);
            ImGui.dummy(0, 20);
            ImGui.popFont();
            ImGui.pushFont(gm.font);
            ImGui.pushItemWidth(200);
            if(ImGui.sliderFloat("Master Volume", masterVolume, 0.0f, 100.0f, "%.1f%%", ImGuiSliderFlags.AlwaysClamp)) {
                Launcher.getGame().audioSources.get("menuMusic").setGain(masterVolume[0]/100 * 0.1f);
                int i = 0;
                for(String key : Launcher.getGame().audioSources.keySet()) {
                    SoundSource src = Launcher.getGame().audioSources.get(key);
                    src.setGain(defaultGains[i] * masterVolume[0]/100);
                    i++;
                }
                UpdateConfiguration();
            }
            if(ImGui.isItemHovered()) {
                ImGui.setNextWindowBgAlpha(0.4f);
                ImGui.pushStyleVar(ImGuiStyleVar.PopupBorderSize, 0.0f);
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                ImGui.setTooltip("Adjusts the volume of all game audio");
                ImGui.popFont();
                ImGui.pushFont(gm.font);
                ImGui.popStyleVar();
            }
            ImGui.popFont();
            ImGui.pushFont(gm.font);
            ImGui.dummy(0, 10);
            ImGui.popFont();
            ImGui.pushFont(gm.fontSmall);
            title = "Display";
            textWidth = ImGui.calcTextSize(title).x;
            textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
            ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
            ImGui.text(title);
            ImGui.dummy(0, 20);
            ImGui.popFont();
            ImGui.pushFont(gm.font);
            ImGui.pushItemWidth(250);
            ImGui.setNextWindowBgAlpha(0.4f);
            if(ImGui.beginCombo("Resolution", resolutionOptions[resolutionSelected].toString())) {
                ImGui.popFont();
                ImGui.pushFont(gm.fontSmall);
                for(int i = 0; i < resolutionOptions.length; i++) {
                    if(ImGui.selectable(resolutionOptions[i].toString())) {
                        Launcher.getGame().audioSources.get("menuClick").play();
                        resolutionSelected = i;
                        ImGui.setItemDefaultFocus();
                        if(GlobalVariables.FULLSCREEN) {
                            GLFW.glfwSetWindowMonitor(gm.window.getWindow(), monitor, 0, 0, resolutionOptions[resolutionSelected].width, resolutionOptions[resolutionSelected].height, 0);
                        } else {
                            IntBuffer xbuf = BufferUtils.createIntBuffer(1);
                            IntBuffer ybuf = BufferUtils.createIntBuffer(1);
                            GLFW.glfwGetWindowPos(gm.window.getWindow(), xbuf, ybuf);
                            gm.window.winPos = new Vector2i(xbuf.get(0), ybuf.get(0));
                            GLFW.glfwSetWindowMonitor(gm.window.getWindow(), MemoryUtil.NULL, gm.window.winPos.x, gm.window.winPos.y, resolutionOptions[resolutionSelected].width, resolutionOptions[resolutionSelected].height, 0);
                        }
                        UpdateConfiguration();
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
                        Launcher.getGame().audioSources.get("menuClick").play();
                        maxFramerateSelected = i;
                        GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
                        ImGui.setItemDefaultFocus();
                        UpdateConfiguration();
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
                Launcher.getGame().audioSources.get("menuClick").play();
                GlobalVariables.FULLSCREEN = !GlobalVariables.FULLSCREEN;
                if(GlobalVariables.FULLSCREEN) {
                    gm.window.winSize = new Vector2i(GlobalVariables.WIDTH, GlobalVariables.HEIGHT);
                    IntBuffer xbuf = BufferUtils.createIntBuffer(1);
                    IntBuffer ybuf = BufferUtils.createIntBuffer(1);
                    GLFW.glfwGetWindowPos(gm.window.getWindow(), xbuf, ybuf);
                    gm.window.winPos = new Vector2i(xbuf.get(0), ybuf.get(0));
                    GLFW.glfwSetWindowMonitor(gm.window.getWindow(), monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
                    resolutionSelected = Utils.getResolutionIndex(resolutionOptions, new Resolution(mode.width(), mode.height()));
                } else {
                    GLFW.glfwSetWindowMonitor(gm.window.getWindow(), MemoryUtil.NULL, gm.window.winPos.x, gm.window.winPos.y, gm.window.winSize.x(), gm.window.winSize.y(), 0);
                    resolutionSelected = Utils.getResolutionIndex(resolutionOptions, new Resolution(gm.window.winSize.x(), gm.window.winSize.y()));
                }
                UpdateConfiguration();
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
                Launcher.getGame().audioSources.get("menuClick").play();
                GlobalVariables.SHOW_FPS = !GlobalVariables.SHOW_FPS;
                UpdateConfiguration();
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
                Launcher.getGame().audioSources.get("menuClick").play();
                GlobalVariables.VSYNC = !GlobalVariables.VSYNC;
                GLFW.glfwSwapInterval(GlobalVariables.VSYNC ? 1 : 0);
                GlobalVariables.FRAMERATE = GlobalVariables.VSYNC ? mode.refreshRate() : maxFramerateOptions[maxFramerateSelected];
                UpdateConfiguration();
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
                Launcher.getGame().audioSources.get("menuClick").play();
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
    
    void UpdateConfiguration() {
        Launcher.getEngine().ForceUpdateFramerate();

        preferences.putFloat(ID_MASTER_VOLUME, masterVolume[0]);
        preferences.put(ID_RESOLUTION, resolutionOptions[resolutionSelected].toString());
        preferences.putInt(ID_FRAMERATE, maxFramerateOptions[maxFramerateSelected]);
        preferences.putBoolean(ID_FULLSCREEN, GlobalVariables.FULLSCREEN);
        preferences.putBoolean(ID_SHOW_FPS, GlobalVariables.SHOW_FPS);
        preferences.putBoolean(ID_VSYNC, GlobalVariables.VSYNC);
    }

}
