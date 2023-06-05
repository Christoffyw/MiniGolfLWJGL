package me.ChristopherW.core.custom.UIScreens;

import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.IGUIScreen;
import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.test.Launcher;

public class SMainMenu implements IGUIScreen {
    ImInt playerCount = new ImInt(Constants.PLAYER_COUNT);


    @Override
    public void start() {
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
            ImGui.setNextWindowSize(0, 0);
            ImGui.setNextWindowPos(Launcher.getWindow().getWidth()/2, Launcher.getWindow().getHeight()/2, 0, 0.5f,0.5f);
            if (ImGui.begin("Main Menu", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoMove)) {
                ImVec2 windowSize = ImGui.getWindowSize();
                String title = "Minigolf";
                float textWidth = ImGui.calcTextSize(title).x;
                ImVec2 textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
                ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
                ImGui.text(title);
                ImGui.dummy(0, 50);
                ImGui.pushItemWidth(130);
                ImGui.alignTextToFramePadding();
                ImGui.text("Player Count");
                ImGui.sameLine();
                ImGui.inputScalar(" ", ImGuiDataType.S32, playerCount, 1,1);
                playerCount.set(Math.min(Math.max(playerCount.get(),1), 12));
                Constants.PLAYER_COUNT = playerCount.get();
                ImGui.popItemWidth();
                ImGui.dummy(0, 10);
                
                ImVec2 buttonSize = new ImVec2(200, 50);
                ImVec2 buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());

                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Play", buttonSize.x, buttonSize.y)) {
                    Constants.inGame = true;
                    gm.currentScreen = "InGame";
                    try {
                        Launcher.getGame().startGame();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    GLFW.glfwSetInputMode(Launcher.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
                }
                ImGui.dummy(0, 10);
                buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Options", buttonSize.x, buttonSize.y)) {
                    gm.currentScreen = "Options";
                }
                ImGui.dummy(0, 10);
                buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Exit", buttonSize.x, buttonSize.y)) {
                    ImGui.popFont();
                    ImGui.end();
                    Launcher.getGame().cleanup();
                    Launcher.getWindow().cleanup();
                }
            }
            ImGui.popFont();
            ImGui.end();
    }
    
}
