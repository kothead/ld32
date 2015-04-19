package com.vdroog1.shamans.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.vdroog1.shamans.screen.GameScreen;
import com.vdroog1.shamans.util.Utils;

import java.util.Iterator;

/**
 * Created by st on 4/19/15.
 */
public class LightningController {

    private Array<Lightning> lightnings;
    private Pool<Lightning> pool;
    private GameScreen gameScreen;

    public LightningController(GameScreen gameScreen) {
        lightnings = new Array<Lightning>();
        pool = Pools.get(Lightning.class);
        this.gameScreen = gameScreen;
    }

    public void process(Batch batch) {
        Iterator<Lightning> iterator = lightnings.iterator();
        while (iterator.hasNext()) {
            Lightning lightning = iterator.next();
            lightning.draw(batch);

            if (!lightning.isAlive()) {
                iterator.remove();
                pool.free(lightning);
            }
        }
    }

    public void strike(float x, float y) {
        Lightning lightning = pool.obtain();
        lightning.init(x, y);
        lightnings.add(lightning);
    }

    public void strikeCell(int x, int y) {
        float posX = (x + 0.5f) * gameScreen.getTileWidth();
        float posY = y * gameScreen.getTileHeight();
        strike(posX, posY);
    }

    public void strike(Player player) {
        float posX = player.getX() + player.getWidth() / 2f;
        float posY = player.getCellY() * gameScreen.getTileHeight();
        strike(posX, posY);
    }
}
