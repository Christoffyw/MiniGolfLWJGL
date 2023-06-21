package me.ChristopherW.core.custom.UIScreens;

import java.util.Currency;

import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.IGUIScreen;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.test.Launcher;

public class SMainMenu implements IGUIScreen {
    ImInt playerCount = new ImInt(GlobalVariables.PLAYER_COUNT);
    ObjectLoader loader = null;
    Texture logoTexture = null;

    @Override
    public void start() {
        Launcher.getGame().audioSources.get("menuMusic").play();
        loader = new ObjectLoader();
        try {
            logoTexture = new Texture(loader.loadTexture("assets/textures/icons/logo.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
            ImGui.setNextWindowSize(0, 0);
            ImGui.setNextWindowPos(Launcher.getWindow().getWidth()/2, Launcher.getWindow().getHeight()/2, 0, 0.5f,0.5f);
            if (ImGui.begin("Main Menu", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoMove)) {
                ImVec2 windowSize = ImGui.getWindowSize();
                if(logoTexture != null) {
                    ImGui.image(logoTexture.getId(), 821, 243);
                } else {
                    String title = "Minigolf";
                    float textWidth = ImGui.calcTextSize(title).x;
                    ImVec2 textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
                    ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
                    ImGui.text(title);
                }
                ImGui.dummy(0, 50);
                ImGui.pushItemWidth(130);
                ImVec2 buttonPosition = new ImVec2((windowSize.x - 130) * 0.5f, ImGui.getCursorPosY());
                String label = "Player Count";
                float labelWidth = ImGui.calcTextSize(label).x;
                ImGui.setCursorPos(buttonPosition.x - labelWidth/2, buttonPosition.y);
                ImGui.alignTextToFramePadding();
                ImGui.text(label);
                ImGui.sameLine();
                ImGui.inputScalar(" ", ImGuiDataType.S32, playerCount, 1,1);
                playerCount.set(Math.min(Math.max(playerCount.get(),1), 12));
                GlobalVariables.PLAYER_COUNT = playerCount.get();
                ImGui.popItemWidth();
                ImGui.dummy(0, 10);
                
                ImVec2 buttonSize = new ImVec2(200, 50);
                buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());

                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Play", buttonSize.x, buttonSize.y)) {
                    Launcher.getGame().audioSources.get("menuClick").play();
                    Launcher.getGame().audioSources.get("menuMusic").stop();

                    GlobalVariables.inGame = true;
                    gm.currentScreen = "InGame";
                    SInGame ig = (SInGame)gm.screens.get(gm.currentScreen);
                    ig.setPlayerID(0);
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
                    Launcher.getGame().audioSources.get("menuClick").play();
                    gm.currentScreen = "Options";
                }
                ImGui.dummy(0, 10);
                buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Credits", buttonSize.x, buttonSize.y)) {
                    Launcher.getGame().audioSources.get("menuClick").play();
                    gm.currentScreen = "Credits";
                }
                ImGui.dummy(0, 10);
                buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Exit", buttonSize.x, buttonSize.y)) {
                    Launcher.getGame().audioSources.get("menuClick").play();
                    ImGui.popFont();
                    ImGui.end();
                    Launcher.getEngine().stop();
                    return;
                }
            }
            ImGui.popFont();
            ImGui.end();
    }
    
}
