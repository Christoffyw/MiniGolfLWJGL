package me.ChristopherW.core.utils;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.joml.Quaternionf;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }
    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static String loadResource(String filename) throws Exception {
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(filename);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static org.joml.Quaternionf ToQuaternion(org.joml.Vector3f vector) {
        double cr = Math.cos(vector.x * 0.5);
        double sr = Math.sin(vector.x * 0.5);
        double cp = Math.cos(vector.y * 0.5);
        double sp = Math.sin(vector.y * 0.5);
        double cy = Math.cos(vector.z * 0.5);
        double sy = Math.sin(vector.z * 0.5);

        org.joml.Quaternionf q = new Quaternionf();
        q.w = (float) (cr * cp * cy + sr * sp * sy);
        q.x = (float) (sr * cp * cy - cr * sp * sy);
        q.y = (float) (cr * sp * cy + sr * cp * sy);
        q.z = (float) (cr * cp * sy - sr * sp * cy);

        return q;
    }

    public static Vector3f convert(org.joml.Vector3f in) {
        return new Vector3f(in.x, in.y, in.z);
    }
    public static org.joml.Vector3f convert(Vector3f in) {
        return new org.joml.Vector3f(in.x, in.y, in.z);
    }
    public static Quaternion convert(org.joml.Quaternionf in) {
        return new Quaternion(in.x, in.y, in.z, in.w);
    }
    public static org.joml.Quaternionf convert(Quaternion in) {
        return new org.joml.Quaternionf(in.getX(), in.getY(), in.getZ(), in.getW());
    }
}
