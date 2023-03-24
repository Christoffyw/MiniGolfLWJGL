package me.ChristopherW.test;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.NativeLibrary;
import me.ChristopherW.core.EngineManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.utils.Constants;

import com.jme3.system.NativeLibraryLoader;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher{
    private static WindowManager window;


    private static TestGame game;
    public static TestGame getGame() {
        return game;
    }

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger(PhysicsRigidBody.class.getName());
        logger.setLevel(Level.OFF);
        logger = Logger.getLogger(PhysicsSpace.class.getName());
        logger.setLevel(Level.OFF);
        logger = Logger.getLogger(NativeLibraryLoader.class.getName());
        logger.setLevel(Level.OFF);
        NativeLibraryLoader.loadLibbulletjme(true, new File("natives/"), "Release", "Sp");
        NativeLibrary.setStartupMessageEnabled(false);
        NativeLibrary.logger.setLevel(Level.OFF);
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