package com.vdroog1.shamans.data;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by st on 7/29/14.
 */
public class ImageCache {
    
    private static final String DATA_DIR = "image/";
    private static final String DATA_FILE = "pack.atlas";
    
    private static TextureAtlas atlas;
    
    public static void load() {
        atlas = new TextureAtlas(DATA_DIR + DATA_FILE);
    }

    public static TextureRegion getTexture(String name) {
        return atlas.findRegion(name);
    }
    
    public static TextureRegion getFrame(String name, int index) {
        return atlas.findRegion(name, index);
    }

    public static TextureRegion[] getFrames(String name, int offset, int count) {
        TextureRegion[] regions = new TextureRegion[count];
        for (int i = offset, j = 0; i < count + offset; i++, j++) {
            regions[j] = getFrame(name, i);
        }
        return regions;
    }

}
