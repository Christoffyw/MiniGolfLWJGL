package me.ChristopherW.test;

import me.ChristopherW.core.EngineManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.utils.Constants;

import com.jme3.system.NativeLibraryLoader;

import java.io.File;

public class Launcher {
    private static WindowManager window;


    private static TestGame game;
    public static TestGame getGame() {
        return game;
    }

    public static void main(String[] args) throws Exception {
        NativeLibraryLoader.loadLibbulletjme(true, new File("natives/"), "Debug", "Sp");
        window = new WindowManager(Constants.title, Constants.WIDTH, Constants.HEIGHT, Constants.VSYNC);
        game = new TestGame();
        EngineManager engine = new EngineManager();


        try {
            engine.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }
}