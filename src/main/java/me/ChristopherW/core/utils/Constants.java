package me.ChristopherW.core.utils;

import java.awt.Color;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Constants {
    public static final String TITLE = "Minigolf";
    public static final String ICON_PATH = "assets/textures/icons/Ball_1.png";
    public static final Vector4f BG_COLOR = new Vector4f(0.53f, 0.81f,0.92f, 1f);

    public static final float GRAVITY = -39.2f;
    public static final boolean FULLSCREEN = true;
    public static final int WIDTH = 1280, HEIGHT = 720;
    public static float FRAMERATE = 60; // ABOVE 999, PHYSICS BREAKS
    public static final boolean VSYNC = true;

    public static final float FOV = (float) Math.toRadians(50);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float CAMERA_MOVE_SPEED = 0.05f;
    public static final float MOUSE_SENSITIVITY_X = 5f;
    public static final float MOUSE_SENSITIVITY_Y = 0.25f;

    public static int PLAYER_COUNT = 2; // MAX 12
    public static boolean RANDOM_COLORS = false;
    public static Color[] DEFAULT_BALL_COLORS = {
        new Color(0,100,255),   // Blue
        new Color(255,35,35),   // Red
        new Color(255,185,35),  // Yellow
        new Color(0,200,85),    // Light Green
        new Color(255,85,35),   // Orange
        new Color(35,200,255),  // Light Blue
        new Color(0, 115, 102), // Green
        new Color(255,50,100),  // Hot Pink
        new Color(155,125,255), // Lavender
        new Color(85,35,255),   // Purple
        new Color(35,35,35),    // Black
        new Color(255,255,255)  // White
    };

    public static boolean mainMenu = true;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f ,0.3f, 0.3f);
    public static final float SPECULAR_POWER = 10f;
}
