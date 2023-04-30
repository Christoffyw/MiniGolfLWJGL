package me.ChristopherW.core.fonts.renderer;

import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.fonts.loader.FontType;
import me.ChristopherW.core.fonts.loader.GUIText;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class FontRenderer {

	private ShaderManager shader;

	public FontRenderer() throws Exception {
		shader = new ShaderManager("/shaders/fontVertex.glsl", "/shaders/fontFragment.glsl");
	}

	public void render(Map<FontType, List<GUIText>> texts) throws Exception {
		prepare();
		for(FontType font : texts.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas().getId());
			for(GUIText text : texts.get(font)) {
				renderText(text);
			}
		}
		endRendering();
	}

	public void cleanup(){
		shader.cleanup();
	}
	
	private void prepare() throws Exception {
		shader.init();
		shader.link();
		shader.createUniform("translation");
		shader.createUniform("color");
		shader.createUniform("fontAtlas");
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private void renderText(GUIText text){
		shader.bind();
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.setUniform("translation", text.getPosition());
		shader.setUniform("color", text.getColor());
		shader.setUniform("fontAtlas", text.getFont().getTextureAtlas().getId());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.unbind();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}
