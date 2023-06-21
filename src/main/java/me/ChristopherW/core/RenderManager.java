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
    public Matrix4f viewMatrix;
    private Map<Model, List<Entity>> entities = new HashMap<>();

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        // bind the texture that the model has
        if(model.getMaterial().hasTexture()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMaterial().getTexture().getId());
        } 
        // or use the default texture if none specified
        else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, Game.defaultTexture.getId());
        }
    }

    public void unbind() {
        // unbind the model's VAOs 
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void prepare(ShaderManager shader, Entity entity, Camera camera) {
        // set all the uniform variables to pass to the shader
        shader.setUniform("textureSampler", 0);
        Matrix4f modelMatrix = Transformation.createTransformationMatrix(entity);
        shader.setUniform("transformationMatrix", modelMatrix);
        shader.setUniform("viewMatrix", Transformation.createViewMatrix(camera));
        shader.setUniform("m3x3InvTrans", Transformation.createInvTransMatrix(modelMatrix));

        // if the entity is the shotmeter, set the power uniform of the shader
        if(entity.getName().startsWith("Shotmeter")) {
            shader.setUniform("power", Launcher.getGame().dist);
        }
    }

    public void render(Camera camera) {
        // render the sky
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // for each model in the entities 
        for(Model model : entities.keySet()) {
            // bind the model's shader and set the projectionMatrix uniform data
            model.getShader().bind();
            model.getShader().setUniform("projectionMatrix", window.updateProjectionMatrix());
            
            // bind the model itself
            bind(model);

            // for each entity that uses that model
            List<Entity> entityList = entities.get(model);
            for(Entity entity : entityList) {
                // prepare it to be rendered then draw the triangles to the viewBuffer
                prepare(model.getShader(), entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            
            // unbind the model
            unbind();

            // unbind the shader
            model.getShader().unbind();
        }

        // clear the entities array for that model for the next frame
        entities.clear();
    }
    public void processEntity(Entity entity) {
        // TLDR; bind together the entities that share the same model
        List<Entity> entityList = entities.get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
        else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entities.put(entity.getModel(), newEntityList);
        }
    }

    public void cleanup() {
        // clean up the memory of all the models
        for(Model model : entities.keySet()) {
            model.getShader().cleanup();
        }
    }
}
