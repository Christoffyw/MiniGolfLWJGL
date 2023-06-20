package me.ChristopherW.core.custom;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import imgui.*;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.UIScreens.SCredits;
import me.ChristopherW.core.custom.UIScreens.SGameOver;
import me.ChristopherW.core.custom.UIScreens.SInGame;
import me.ChristopherW.core.custom.UIScreens.SMainMenu;
import me.ChristopherW.core.custom.UIScreens.SOptionsMenu;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.test.Launcher;

public class GUIManager {

    public HashMap<String, IGUIScreen> screens = new HashMap<String, IGUIScreen>();
    public static String currentScreen = "";
    ImFontAtlas fontAtlas;
    ImFontConfig fontConfig;
    public ImFont font;
    public ImFont fontSmall;
    ImGuiIO io;
    public int window_flags;
    int location = 0;
    public WindowManager window;
    public CourseManager cm;

    public void render() {
        ImBoolean p_open = new ImBoolean();
        ImGui.pushFont(fontSmall);
        if(GlobalVariables.SHOW_FPS) {
            ImGui.setNextWindowPos(0, 0);
            if (ImGui.begin("FPS", p_open, window_flags)) {
                ImGui.text(String.valueOf(Launcher.getEngine().getFps()));
            }
            ImGui.end();
        }
        ImGui.popFont();
        ImGui.pushFont(font);
        if(currentScreen != "None")
            screens.get(currentScreen).render(p_open, this); 
    }

    public GUIManager(WindowManager window) {
        this.window = window;
        window_flags = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav;
    }
    public void init() {
        fontConfig = new ImFontConfig();
        io = ImGui.getIO();
        fontAtlas = io.getFonts();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontDefault();
        font = fontAtlas.addFontFromFileTTF("assets/fonts/mont-heavy.ttf", 34f, fontConfig);
        fontSmall = fontAtlas.addFontFromFileTTF("assets/fonts/mont-heavy.ttf", 17f, fontConfig);
        fontAtlas.build();
        fontConfig.destroy();

        // initialize screens

        screens.put("MainMenu", new SMainMenu());
        screens.put("Options", new SOptionsMenu());
        screens.put("Credits", new SCredits());
        screens.put("GameOver", new SGameOver());
        SInGame sig = new SInGame();
        sig.setCourseManager(cm);
        screens.put("InGame", sig);
        currentScreen = "MainMenu";

        for(IGUIScreen screen : screens.values()) {
            screen.start();
        }
    }
}
