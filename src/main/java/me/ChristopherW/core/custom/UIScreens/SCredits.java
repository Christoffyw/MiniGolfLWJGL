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

public class SCredits implements IGUIScreen {

    String[] creditsEntries = {
        "Lead Developer - Christopher Weinhardt",
        "UX Designer - Christopher Weinhardt",
        "Music Composer - Christopher Weinhardt",
        "SFX - Pixabay & SoundBible",
        "Physics Engine - Bullet Real-Time Physics Engine"
    };

    @Override
    public void start() {

    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        ImGui.setNextWindowSize(0, 0);
        ImGui.setNextWindowPos(gm.window.getWidth()/2, gm.window.getHeight()/2, 0, 0.5f,0.5f);
        if (ImGui.begin("Credits", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoMove)) {
            ImVec2 windowSize = ImGui.getWindowSize();
            String title = "Credits";
            float textWidth = ImGui.calcTextSize(title).x;
            ImVec2 textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
            ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
            ImGui.text(title);
            ImGui.dummy(0, 50);
            ImGui.popFont();
            ImGui.pushFont(gm.fontSmall);
            for(int i = 0; i < creditsEntries.length; i++) {
                String entry = creditsEntries[i];
                textWidth = ImGui.calcTextSize(entry).x;
                textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
                ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
                ImGui.text(entry);
                ImGui.dummy(0, 5);
            }
            ImGui.popFont();
            ImGui.pushFont(gm.font);
            ImGui.dummy(0, 20);
            ImVec2 buttonSize = new ImVec2(200, 50);
            ImVec2 buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.button("Back", buttonSize.x, buttonSize.y)) {
                Launcher.getGame().audioSources.get("menuClick").play();
                gm.currentScreen = "MainMenu";
            }
        }
        ImGui.popFont();
        ImGui.end();
    }
    
    void TriggerUpdate() {
        Launcher.getEngine().ForceUpdateFramerate();
    }

}
