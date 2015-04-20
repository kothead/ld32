package com.vdroog1.shamans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.data.ImageCache;

/**
 * Created by kettricken on 20.04.2015.
 */
public class MenuScreen extends BaseScreen {

    private static final float CAMERA_SPEED = 100;
    private static final int UNIT_SCALE = 3;

    private TiledMap map;
    private Table table;
    private float mapHeight;
    private OrthogonalTiledMapRenderer renderer;
    private float cameraSpeed = CAMERA_SPEED;

    public MenuScreen(ShamanGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        map = new TmxMapLoader().load("map/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);

        final Image title = new Image(ImageCache.getTexture("title"));
        final Image play = new Image(ImageCache.getTexture("play"));
        play.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                getGame().setGameScreen();
            }
        });

        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        float tileHeight = tileLayer.getTileHeight() * UNIT_SCALE;
        mapHeight = tileLayer.getHeight() * tileHeight;

        table = new Table();
        table.left().top();
        table.setFillParent(true);
        table.padLeft(50);
        table.padTop(100);
        table.add(title).center().left();
        table.row();
        table.add(play);
        stage().addActor(table);

        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage());
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                }
                return super.keyUp(keycode);
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl20.glClearColor(0.8f, 0.8f, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float cameraYPos = calcNewCameraPosition(delta);
        if (cameraYPos > mapHeight - getWorldHeight() / 2f
            || cameraYPos < getWorldHeight() / 2f) {
            cameraSpeed = -cameraSpeed;
            cameraYPos = calcNewCameraPosition(delta);
        }
        table.setY(table.getY() + cameraSpeed * delta);
        getCamera().position.y = cameraYPos;

        renderer.setView(getCamera());
        renderer.render();

        stage().draw();
    }

    private float calcNewCameraPosition(float delta) {
        return getCamera().position.y + cameraSpeed * delta;
    }

    @Override
    protected void layoutViewsLandscape(int width, int height) {

    }

    @Override
    protected void layoutViewsPortrait(int width, int height) {

    }

}
