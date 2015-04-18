package com.vdroog1.shamans.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.data.Configuration;
import com.vdroog1.shamans.util.Utils;

/**
 * Created by st on 10/24/14.
 */
public abstract class BaseScreen extends ScreenAdapter {

    private ShamanGame game;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private float worldWidth, worldHeight;

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private Stage stage;

    public BaseScreen(ShamanGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        calcWorldSize();
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);

        batch = new SpriteBatch();
        stage = new Stage(viewport);
        shapes = new ShapeRenderer();
    }

    protected abstract void layoutViewsLandscape(int width, int height);

    protected abstract void layoutViewsPortrait(int width, int height);

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        updateGraphics(width, height);
        layoutViews(width, height);
    }

    public ShamanGame getGame() {
        return game;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public SpriteBatch batch() {
        return batch;
    }

    public ShapeRenderer shapes() {
        return shapes;
    }

    public Stage stage() {
        return stage;
    }

    public Viewport getViewport() {
        return viewport;
    }

    private void updateGraphics(int width, int height) {
        calcWorldSize();

        viewport.setMinWorldWidth(worldWidth);
        viewport.setMinWorldHeight(worldHeight);
        viewport.update(width, height, true);

        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        batch.setProjectionMatrix(getCamera().combined);
        shapes.setProjectionMatrix(getCamera().combined);
    }

    private void calcWorldSize() {
        worldWidth = Configuration.GAME_WIDTH * Configuration.SCALE_FACTOR;
        worldHeight = Configuration.GAME_HEIGHT * Configuration.SCALE_FACTOR;

        if (Utils.isLandscape()) {
            float temp = worldHeight;
            worldHeight = worldWidth;
            worldWidth = temp;
        }
    }

    private void layoutViews(int width, int height) {
        if (Utils.isLandscape()) {
            layoutViewsLandscape(width, height);
        } else {
            layoutViewsPortrait(width, height);
        }
    }
}
