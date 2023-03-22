package me.ChristopherW.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Constants {
    public static final String title = "GAME TEST";
    public static final Vector4f BG_COLOR = new Vector4f(0.53f, 0.81f,0.92f, 1f);

    public static final float GRAVITY = -19.6f;
    public static final boolean FULLSCREEN = true;
    public static final int WIDTH = 1280, HEIGHT = 720;
    public static final float FRAMERATE = 999; // ABOVE 999, PHYSICS BREAKS
    public static final boolean VSYNC = true;

    public static final float FOV = (float) Math.toRadians(50);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float CAMERA_MOVE_SPEED = 0.05f;
    public static final float MOUSE_SENSITIVITY = 100f;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f ,0.3f, 0.3f);
    public static final float SPECULAR_POWER = 10f;
}
