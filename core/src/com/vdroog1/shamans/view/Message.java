package com.vdroog1.shamans.view;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.data.ImageCache;
import com.vdroog1.shamans.model.ArrowButton;
import com.vdroog1.shamans.model.Player;

/**
 * Created by kettricken on 18.04.2015.
 */
public class Message {

    private static final float TIME_FOR_MESSAGE = 5f;

    private static final float MESSAGE_HORIZONTAL_PADDING = -20;
    private static final float MESSAGE_VERTICAL_PADDING = 10;

    private SpriteDrawable left;
    private SpriteDrawable right;
    private Player player;

    private float timer;

    Array<ArrowButton.Type> spellCasting;
    Array<Image> images;

    Stage stage;

    public Message(Stage stage, Player player, Array<ArrowButton.Type> spellCasting) {
        this.stage = stage;
        this.player = player;
        this.spellCasting = spellCasting;
        left = new SpriteDrawable(new Sprite(ImageCache.getTexture("left")));
        right = new SpriteDrawable(new Sprite(ImageCache.getTexture("right")));
        images = new Array<Image>(){{
            add(new Image(left));
            add(new Image(left));
            add(new Image(left));
        }};
    }

    public boolean process(float delta) {
        for (int i = 0; i < images.size; i++) {
            Image image = images.get(i);
            if (i < spellCasting.size) {
                SpriteDrawable drawable;
                if (spellCasting.get(i) == ArrowButton.Type.LEFT) {
                    drawable = left;
                } else {
                    drawable = right;
                }
                image.setDrawable(drawable);
                image.setPosition(player.getX() + i * drawable.getSprite().getWidth(),
                        player.getY() + player.getHeight() + MESSAGE_VERTICAL_PADDING);
                stage.addActor(image);
            } else {
                image.remove();
            }
        }

        timer += delta;
        if (timer > TIME_FOR_MESSAGE) {
            timer = 0;
            return false;
        }

        return true;
    }

    public void reset() {
        timer = 0;
    }
}
