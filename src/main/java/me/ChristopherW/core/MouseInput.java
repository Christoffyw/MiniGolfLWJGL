package me.ChristopherW.core;

import me.ChristopherW.core.utils.Constants;
import me.ChristopherW.test.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;

    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;

    public MouseInput() {
        this.previousPos = new Vector2d(Constants.WIDTH/2, Constants.HEIGHT/2);
        this.currentPos = new Vector2d(Constants.WIDTH/2,Constants.HEIGHT/2);
        this.displVec = new Vector2f();
    }

    public void init() {
        GLFW.glfwSetCursorPos(Launcher.getWindow().getWindow(), Constants.WIDTH/2, Constants.HEIGHT/2);
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            this.currentPos.x = xpos;
            this.currentPos.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> {
            this.inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mods) -> {
            this.leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            this.rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        this.displVec.x = 0;
        this.displVec.y = 0;
        double x = this.currentPos.x - this.previousPos.x;
        double y = this.currentPos.y - this.previousPos.y;
        boolean rotateX = x != 0;
        boolean rotateY = y != 0;
        if (rotateX)
            this.displVec.y = (float) x;
        if (rotateY)
            this.displVec.x = (float) y;
        this.previousPos.x = this.currentPos.x;
        this.previousPos.y = this.currentPos.y;
    }
    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }
}
