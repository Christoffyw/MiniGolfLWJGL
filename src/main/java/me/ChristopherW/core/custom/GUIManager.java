package me.ChristopherW.core.custom;

import imgui.*;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Constants;

public class GUIManager {
    private String holeText;
    private String playerText;
    private String strokeText;
    private int playerID;
    Texture[] ballTextures;
    public void render() {
        ImBoolean p_open = new ImBoolean();
        ImGui.pushFont(font);
        if (ImGui.begin("UI", p_open, window_flags))
        {
            ImGui.image(ballTextures[playerID].getId(), 64, 64);
            ImGui.text(playerText);
            ImGui.dummy(100,50);
            ImGui.text(holeText);
            ImGui.text(strokeText);
        }
        ImGui.popFont();
        ImGui.end();
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getHoleText() {
        return holeText;
    }

    public void setHoleText(String holeText) {
        this.holeText = holeText;
    }

    public String getPlayerText() {
        return playerText;
    }

    public void setPlayerText(String playerText) {
        this.playerText = playerText;
    }

    public String getStrokeText() {
        return strokeText;
    }

    public void setStrokeText(String strokeText) {
        this.strokeText = strokeText;
    }

    ImGuiIO io;
    int window_flags;
    int location = 0;
    ImFontAtlas fontAtlas;
    ImFontConfig fontConfig;
    ImFont font;
    ObjectLoader loader;
    public GUIManager() {
        window_flags = ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav;
    }
    public void init() {
        loader = new ObjectLoader();
        fontConfig = new ImFontConfig();
        ballTextures = new Texture[12];
        io = ImGui.getIO();
        fontAtlas = io.getFonts();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontDefault();
        font = fontAtlas.addFontFromFileTTF("assets/fonts/mont.otf", 34f, fontConfig);
        fontAtlas.build();
        fontConfig.destroy();
        try {
            for(int i = 0; i < Constants.PLAYER_COUNT; i++) {
                ballTextures[i] = new Texture(loader.loadTexture(String.format("assets/textures/icons/Ball_%d.png", i)));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
