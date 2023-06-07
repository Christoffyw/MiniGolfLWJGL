package me.ChristopherW.core.custom.UIScreens;

public class Resolution {
    public int width;
    public int height;
    public float aspectRatio;
    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspectRatio = (float)width/(float)height;
    }
}
