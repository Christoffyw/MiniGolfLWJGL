package me.ChristopherW.core.custom.UIScreens;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.custom.GUIManager;
import me.ChristopherW.core.custom.IGUIScreen;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.test.Launcher;

public class SGameOver implements IGUIScreen {

    Texture[] ballTextures;

    @Override
    public void start() {
        ballTextures = new Texture[12];
        ObjectLoader loader = new ObjectLoader();
        try {
            for(int i = 0; i < 12; i++) {
                ballTextures[i] = new Texture(loader.loadTexture(String.format("assets/textures/icons/Ball_%d.png", i)));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        ImGui.setNextWindowSize(0, 0);
        ImGui.setNextWindowPos(gm.window.getWidth()/2, gm.window.getHeight()/2, 0, 0.5f,0.5f);
        if (ImGui.begin("GameOver", p_open, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoMove)) {
            ImVec2 windowSize = ImGui.getWindowSize();
            String title = "Player ??? Wins";
            float textWidth = ImGui.calcTextSize(title).x;
            ImVec2 textPosition = new ImVec2((windowSize.x - textWidth) * 0.5f, windowSize.y * 0.5f);
            ImGui.setCursorPos(textPosition.x, ImGui.getCursorPosY());
            ImGui.text(title);
            ImGui.dummy(0, 50);
            if (ImGui.beginTable("table1", gm.cm.GetHoleCount() + 3))
            {
                for (int row = 0; row < gm.cm.GetBallCount() + 1; row++)
                {
                    ImGui.tableNextRow();
                    for (int column = 0; column < gm.cm.GetHoleCount() + 3; column++)
                    {
                        ImGui.tableSetColumnIndex(column);
                        if(row == 0) {
                            if(column == 0)
                                ImGui.text("");
                            else if (column == 1)
                                ImGui.text("Hole");
                            else if (column == gm.cm.GetHoleCount() + 2)
                                ImGui.text("Total");
                            else
                                ImGui.text(String.format("%d", column - 1));
                        } else {
                            if (column == 0)
                                ImGui.image(ballTextures[row - 1].getId(), 32, 32);
                            else if (column == 1)
                                ImGui.text(String.format("Player  %d    ", row));
                            else if (column == gm.cm.GetHoleCount() + 2)
                                ImGui.text("" + gm.cm.GetBall(row - 1).getTotalScore());
                            else {
                                int score = gm.cm.GetBall(row - 1).getScore(column - 2);
                                ImGui.text("" + score);
                            }
                        }
                    }
                }
                ImGui.endTable();
            }
            ImGui.dummy(0, 20);
            ImVec2 buttonSize = new ImVec2(200, 50);
            ImVec2 buttonPosition = new ImVec2((windowSize.x - buttonSize.x) * 0.5f, ImGui.getCursorPosY());
            ImGui.setCursorPos(buttonPosition.x, buttonPosition.y);
            if(ImGui.button("Back", buttonSize.x, buttonSize.y)) {
                Launcher.getGame().audioSources.get("menuClick").play();
                GlobalVariables.inGame = false;
                gm.currentScreen = "MainMenu";
                gm.screens.get(gm.currentScreen).start();
                gm.cm.GetBalls().clear();
                Launcher.getGame().rotation = 0;
            }
        }
        ImGui.popFont();
        ImGui.end();
    }
}
