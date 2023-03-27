package me.ChristopherW.core;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.test.TestGame;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.Assimp.aiProcess_PreTransformVertices;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public IndexedMesh loadIndexedMesh(Model model, Vector3f scale) {

        com.jme3.math.Vector3f[] verticiesArr = new com.jme3.math.Vector3f[model.getVertices().length / 3];
        for(int i = 0; i < model.getVertices().length; i += 3) {
            Vector3f vertex = new Vector3f(model.getVertices()[i], model.getVertices()[i + 1], model.getVertices()[i + 2]);
            verticiesArr[i / 3] = new com.jme3.math.Vector3f(vertex.x * scale.x, vertex.y * scale.y, vertex.z * scale.z);
        }
        return new IndexedMesh(verticiesArr, model.getIndicies());
    }
    public int loadModel(float[] vertices, float[] textureCoords) {
        int id = createVAO();
        storeDataInAttribList(0, 2, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        unbind();
        return id;
    }
    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indicies, Texture texture, String path) {
        int id = createVAO();
        storeInticiesBuffer(indicies);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);
        unbind();
        Model model = new Model(id, indicies.length, path);
        model.setVertices(vertices);
        model.setTextureCoords(textureCoords);
        model.setNormals(normals);
        model.setIndicies(indicies);
        model.getMaterial().setTexture(texture);
        return model;
    }
    public Vector3f getHoleLocation(Model model) {
        Vector3f holePosition = new Vector3f(0,0,0);
        String modelPath = model.getPath();
        try {
            Scanner scanner = new Scanner(new File(modelPath));
            while(scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                if(tokens[0].equalsIgnoreCase("h")) {
                    holePosition = new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3]));
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return holePosition;
    }
    public Model loadModel(String modelPath) {
        return loadModel(modelPath, TestGame.defaultTexture);
    }
    public Model loadModel(String modelPath, Texture texture) {
        File file = new File(modelPath);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + modelPath + "]");
        }
        String modelDir = file.getParent();

        AIScene aiScene = aiImportFile(modelPath, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            float[] vertices = processVertices(aiMesh);
            float[] normals = processNormals(aiMesh);
            float[] textCoords = processTextCoords(aiMesh);
            int[] indices = processIndices(aiMesh);

            // Texture coordinates may not have been populated. We need at least the empty slots
            if (textCoords.length == 0) {
                int numElements = (vertices.length / 3) * 2;
                textCoords = new float[numElements];
            }
            return loadModel(vertices, textCoords, normals, indices, texture, modelPath);
        }

        return null;
    }

    private Model loadModel(float[] vertices, float[] textCoords, float[] normals, int[] indices) {
        return loadModel(vertices, textCoords, normals, indices, TestGame.defaultTexture, "");
    }

    private static float[] processNormals(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }
    private static int[] processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
    private static float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    private static float[] processVertices(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();
        }
        return data;
    }

    public int loadTextureColor(Color color) {
        int width = 16, height = 16;
        try {
            BufferedImage img = ImageIO.read(new File("assets/textures/DefaultTexture.png"));
            // Obtain the Graphics2D context associated with the BufferedImage.
            Graphics2D g = img.createGraphics();

            g.setColor(color);
            g.fillRect(0, 0, width, height);

            // Clean up -- dispose the graphics context that was created.
            ImageIO.write(img, "png", new File("assets/textures/temp.png"));
            g.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load("assets/textures/temp.png", w, h, c, 4);
            if(buffer == null)
                throw new Exception("Texture file" + "assets/textures/temp.png" + " not loaded. " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        return id;
    }

    public GLFWImage loadtextureBuffer(String iconPath) {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(iconPath, w, h, c, 4);
            if(buffer == null)
                throw new Exception("Texture file " + iconPath + " not loaded. " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GLFWImage image = GLFWImage.malloc();
        image.set(width, height, buffer);
        return image;
    }
    public int loadTexture(String file) throws Exception {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(file, w, h, c, 4);
            if(buffer == null)
                throw new Exception("Texture file" + file + " not loaded. " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }
    private void storeInticiesBuffer(int[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }
    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for(int vao: vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo: vbos)
            GL30.glDeleteBuffers(vbo);
        for(int texture: textures)
            GL11.glDeleteTextures(texture);
    }
}