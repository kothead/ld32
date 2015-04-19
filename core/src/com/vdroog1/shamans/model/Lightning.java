package com.vdroog1.shamans.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.vdroog1.shamans.data.ImageCache;

/**
 * Created by st on 4/19/15.
 */
public class Lightning extends Sprite implements Poolable {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 1000;

    public static final float ANIMATION_DURATION = 0.1f;
    public static final float TOTAL_DURATION = 3f;

    public static final String TEXTURE = "lightning";

    private static Animation animation;
    private static boolean alive = false;

    static {
        animation = new Animation(ANIMATION_DURATION, ImageCache.getFrames(TEXTURE, 1, 5));
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private float stateTime;

    public void init(float x, float y) {
        setPosition(x - WIDTH / 2f, y);
        setSize(WIDTH, HEIGHT);
        alive = true;
    }

    @Override
    public void reset() {
        stateTime = 0;
        alive = false;
    }

    @Override
    public void draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        if (stateTime > TOTAL_DURATION) alive = false;
        setRegion(animation.getKeyFrame(stateTime));
        super.draw(batch);
    }

    public boolean isAlive() {
        return alive;
    }
}
