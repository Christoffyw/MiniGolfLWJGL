package me.ChristopherW.core.fonts.renderer;

import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.fonts.loader.FontType;
import me.ChristopherW.core.fonts.loader.GUIText;
import me.ChristopherW.core.fonts.loader.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {
    private static ObjectLoader loader;
    private static Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(ObjectLoader objectLoader) throws Exception {
        renderer = new FontRenderer();
        loader = objectLoader;
    }

    public static void render() throws Exception {
        renderer.render(texts);
    }

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadModel(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if(textBatch == null) {
            textBatch = new ArrayList<GUIText>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if(textBatch.isEmpty()) {
            texts.remove(text.getFont());
        }
    }

    public static void cleanup() {
        renderer.cleanup();
    }
}
