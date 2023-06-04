package me.ChristopherW.core.custom;

import imgui.type.ImBoolean;

public interface IGUIScreen {
    public void start();
    public void render(ImBoolean p_open, GUIManager gm);
}
