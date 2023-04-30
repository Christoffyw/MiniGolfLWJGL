package me.ChristopherW.core;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.utils.Transformation;
import me.ChristopherW.test.Launcher;
import me.ChristopherW.test.Game;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderManager {
    private final WindowManager window;
    private ShaderManager shader;
    public Matrix4f viewMatrix;
    private Map<Model, List<Entity>> entities = new HashMap<>();

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager("/shaders/vertex.glsl", "/shaders/fragment.glsl");
        shader.init();
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("m3x3InvTrans");
        //shader.createUniform("ambientLight");
        //shader.createMaterialUniform("material");
    }

    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        if(model.getMaterial().hasTexture()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMaterial().getTexture().getId());
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, Game.defaultTexture.getId());
        }
    }

    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void prepare(Entity entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
        Matrix4f modelMatrix = Transformation.createTransformationMatrix(entity);
        shader.setUniform("transformationMatrix", modelMatrix);
        shader.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        shader.setUniform("m3x3InvTrans", Transformation.createInvTransMatrix(modelMatrix));
    }

    public void render(Camera camera) {
        clear();
        shader.bind();
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());

        for(Model model : entities.keySet()) {
            bind(model);
            List<Entity> entityList = entities.get(model);
            for(Entity entity : entityList) {
                prepare(entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        entities.clear();
        shader.unbind();
    }
    public void processEntity(Entity entity) {
        List<Entity> entityList = entities.get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
        else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entities.put(entity.getModel(), newEntityList);
        }
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
