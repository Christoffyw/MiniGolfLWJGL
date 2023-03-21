package me.ChristopherW.core.entity;

import org.joml.Vector4f;

import static me.ChristopherW.core.utils.Constants.DEFAULT_COLOR;

public class Material {
    private Vector4f ambientColor, diffuseColor, specularColor;
    private float reflectability;
    private Texture texture;

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
        this.reflectability = 0;
        this.texture = null;
    }

    public Material(Vector4f color, float reflectability) {
        this(color, color, color, reflectability, null);
    }
    public Material(Vector4f color, float reflectability, Texture texture) {
        this(color, color, color, reflectability, texture);
    }
    public Material(Texture texture) {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, 0, texture);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectability, Texture texture) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectability = reflectability;
        this.texture = texture;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public float getReflectability() {
        return reflectability;
    }

    public void setReflectability(float reflectability) {
        this.reflectability = reflectability;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }
}
