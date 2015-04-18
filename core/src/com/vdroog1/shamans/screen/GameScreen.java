package com.vdroog1.shamans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.interfaces.InputController;
import com.vdroog1.shamans.model.Player;

/**
 * Created by st on 4/18/15.
 */
public class GameScreen extends BaseScreen {

    public static final int UNIT_SCALE = 4;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    Player player;

    public GameScreen(ShamanGame game) {
        super(game);
    }

    @Override
    protected void layoutViewsLandscape(int width, int height) {

    }

    @Override
    protected void layoutViewsPortrait(int width, int height) {

    }

    @Override
    public void show() {
        super.show();

        map = new TmxMapLoader().load("map/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);

        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        player = new Player(tileLayer);
        player.setMovementController(new InputController());
        player.setPosition(50, 5 * tileLayer.getTileHeight() * UNIT_SCALE);

        Gdx.input.setInputProcessor((InputController) player.getMovementController());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl20.glClearColor(0.8f,0.8f,1,1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);

        renderer.setView(getCamera());
        renderer.render();

        renderer.getBatch().begin();
        player.draw(renderer.getBatch());
        renderer.getBatch().end();
    }
}
