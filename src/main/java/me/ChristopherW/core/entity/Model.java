package me.ChristopherW.core.entity;

import me.ChristopherW.core.ShaderManager;

public class Model {
    private String path = "generated";
    private int id;
    private int vertexCount;
    private Material material;
    private ShaderManager shader;
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indicies;

    public void init() {
        try {
            this.shader = new ShaderManager("/shaders/vertex.glsl", "/shaders/fragment.glsl");
            shader.start();
        
        
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
        init();
    }

    public Model(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
        init();
    }
    public Model(int id, int vertexCount, Texture texture, String path) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
        this.path = path;
        init();
    }
    public Model(int id, int vertexCount, String path) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
        this.path = path;
        init();
    }

    public Model(Model model, Material material) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = material;
        init();
    }

    public Model(Model model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
        this.material.setTexture(texture);
        init();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public void setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public int[] getIndicies() {
        return indicies;
    }

    public void setIndicies(int[] indicies) {
        this.indicies = indicies;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setShader(ShaderManager shader) {
        this.shader = shader;
    }
    public ShaderManager getShader() {
        return shader;
    }
}
