package com.vdroog1.shamans.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.vdroog1.shamans.data.ImageCache;

/**
 * Created by kettricken on 18.04.2015.
 */
public class ArrowButton extends Actor {

    public enum Type {
        LEFT("left_arrow"),
        RIGHT("right_arrow");

        private TextureRegion region;

        Type(String texture) {
            region = ImageCache.getTexture(texture);
        }
    }

    Type type;

    public ArrowButton (Type type) {
        super();
        this.type = type;
      //  setRegion(ImageCache.getTexture(type.name()));
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.draw(type.region, getX(), getY());
    }

}