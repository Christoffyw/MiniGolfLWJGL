package me.ChristopherW.core.custom;

import org.lwjgl.glfw.GLFW;

import imgui.*;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.test.Launcher;

public class GUIManager {
    private String holeText;
    private String playerText;
    private String strokeText;
    private int playerID;
    Texture[] ballTextures;
    ImInt playerCount = new ImInt(Constants.PLAYER_COUNT);

    public void render() {
        ImBoolean p_open = new ImBoolean();
        ImGui.pushFont(font);

        if(Constants.mainMenu) {
            ImGui.setNextWindowBgAlpha(0.4f);
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
                playerCount.set(Math.min(Math.max(playerCount.get(), 2), 12));
                Constants.PLAYER_COUNT = playerCount.get();
                ImGui.popItemWidth();
                ImGui.dummy(0, 10);
                
                ImVec2 buttonSize = new ImVec2(200, 50);
                ImVec2 buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());

                ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
                if(ImGui.button("Play", buttonSize.x, buttonSize.y)) {
                    Constants.mainMenu = false;
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
                if(ImGui.button("Exit", buttonSize.x, buttonSize.y)) {
                    ImGui.popFont();
                    ImGui.end();
                    Launcher.getGame().cleanup();
                    Launcher.getWindow().cleanup();
                }
            }
            ImGui.popFont();
            ImGui.end();
        } else {
            if (ImGui.begin("UI", p_open, window_flags)) {
                ImGui.image(ballTextures[playerID].getId(), 64, 64);
                ImGui.text(playerText);
                ImGui.dummy(100, 50);
                ImGui.text(holeText);
                ImGui.text(strokeText);
            }
            ImGui.end();
            ImGui.popFont();
            ImGui.pushFont(fontSmall);
            int scoreboardWidth = window.getWidth() / 3;
            int scoreboardHeight = window.getHeight() / 5;
            ImGui.setNextWindowBgAlpha(0.4f);
            ImGui.setNextWindowSize(0, 0);
            ImGui.setNextWindowPos(window.getWidth(),0, 0, 1,0);
            if (ImGui.begin("Scoreboard", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav)) {
                if (ImGui.beginTable("table1", courseManager.GetHoleCount() + 3))
                {
                    for (int row = 0; row < courseManager.GetBallCount() + 1; row++)
                    {
                        ImGui.tableNextRow();
                        for (int column = 0; column < courseManager.GetHoleCount() + 3; column++)
                        {
                            ImGui.tableSetColumnIndex(column);
                            if(row == 0) {
                                if(column == 0)
                                    ImGui.text("");
                                else if (column == 1)
                                    ImGui.text("Hole");
                                else if (column == courseManager.GetHoleCount() + 2)
                                    ImGui.text("Total");
                                else
                                    ImGui.text(String.format("%d", column - 1));
                            } else {
                                if (column == 0)
                                    ImGui.image(ballTextures[row - 1].getId(), 16, 16);
                                else if (column == 1)
                                    ImGui.text(String.format("Player  %d    ", row));
                                else if (column == courseManager.GetHoleCount() + 2)
                                    ImGui.text("" + courseManager.GetBall(row - 1).getTotalScore());
                                else {
                                    int score = courseManager.GetBall(row - 1).getScore(column - 2);
                                    ImGui.text("" + score);
                                }
                            }
                        }
                    }
                    ImGui.endTable();
                }
            }
            ImGui.popFont();
            ImGui.end();
        }
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

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    ImGuiIO io;
    int window_flags;
    int location = 0;
    ImFontAtlas fontAtlas;
    ImFontConfig fontConfig;
    ImFont font;
    ImFont fontSmall;
    ObjectLoader loader;
    WindowManager window;
    CourseManager courseManager;
    public GUIManager(WindowManager window) {
        this.window = window;
        window_flags = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav;
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
        fontSmall = fontAtlas.addFontFromFileTTF("assets/fonts/mont.otf", 17f, fontConfig);
        fontAtlas.build();
        fontConfig.destroy();
        try {
            for(int i = 0; i < 12; i++) {
                ballTextures[i] = new Texture(loader.loadTexture(String.format("assets/textures/icons/Ball_%d.png", i)));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
