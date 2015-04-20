package com.vdroog1.shamans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.data.SkinCache;
import com.vdroog1.shamans.interfaces.AIController;
import com.vdroog1.shamans.interfaces.InputController;
import com.vdroog1.shamans.model.LightningController;
import com.vdroog1.shamans.model.Player;
import com.vdroog1.shamans.view.Message;

/**
 * Created by st on 4/18/15.
 */
public class GameScreen extends BaseScreen {

    public static final int UNIT_SCALE = 4;
    private TiledMap map;
    private float mapWidth, mapHeight;
    private float tileWidth, tileHeight;
    private OrthogonalTiledMapRenderer renderer;
    private LightningController lightnings;

    Array<Player> players = new Array<Player>();

    boolean gameOver = false;

    public GameScreen(ShamanGame game) {
        super(game);
        lightnings = new LightningController(this);
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
        tileWidth = tileLayer.getTileWidth() * UNIT_SCALE;
        tileHeight = tileLayer.getTileHeight() * UNIT_SCALE;
        mapWidth = tileLayer.getWidth() * tileWidth;
        mapHeight = tileLayer.getHeight() * tileHeight;

        generatePlayers(tileLayer);

        Gdx.input.setInputProcessor((InputController) players.get(0).getMovementController());
    }

    private void generatePlayers(TiledMapTileLayer tileLayer) {
        for (int i = 0; i < PLAYERS_NUM(); i++) {
            Label label = new Label(null, SkinCache.getDefaultSkin(), "message");
            stage().addActor(label);

            Player player = new Player(this, tileLayer);
            player.setIsPlayer(i == 0);
            player.setMovementController(i ==0 ? new InputController() : new AIController(player));
            float x = MathUtils.random(0, getWorldWidth() - player.getWidth());
            player.setPosition(x, 5 * getTileHeight());
            player.setMessage(new Message(label, player, player.getSpellCasing()));
            players.add(player);
        }
    }

    private int PLAYERS_NUM() {
        return 3;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl20.glClearColor(0.8f, 0.8f, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera(players.get(0));
        for (Player player : players) {
            Player closestPlayer = getClosestPlayer(player);
            player.update(delta, closestPlayer);
        }

        renderer.setView(getCamera());
        renderer.render();

        renderer.getBatch().begin();
        lightnings.process(renderer.getBatch());
        for (Player player : players) {
            player.draw(renderer.getBatch());
        }
        renderer.getBatch().end();

        stage().act(delta);
        stage().draw();
    }

    private Player getClosestPlayer(Player player) {
        float distance = Float.MAX_VALUE;
        Player closestPlayer = null;
        for (int i = 0; i< players.size; i++) {
            Player p = players.get(i);
            if (p == player) continue;
            float d = Math.abs(player.getY() - p.getY());
            if (d < distance) {
                distance = d;
                closestPlayer = p;
            }
        }
        return closestPlayer;
    }

    private void moveCamera(Player player) {
        float positionX = player.getX() + player.getWidth() / 2;
        float positionY = player.getY() + player.getHeight() / 2;

        if (positionX < getWorldWidth() / 2f) {
            positionX = getWorldWidth() / 2f;
        } else if (positionX > mapWidth - getWorldWidth() / 2f ) {
            positionX = mapWidth - getWorldWidth() / 2f;
        }

        if (positionY < getWorldHeight() / 2f) {
            positionY = getWorldHeight() / 2f;
        } else if (positionY > mapHeight - getWorldHeight() / 2f) {
            positionY = mapHeight - getWorldHeight() / 2f;
        }

        getCamera().position.set(positionX, positionY, 0);
        getCamera().update();

    }

    public void castSpell(Player fromPlayer) {
        Player closestPlayer = getClosestPlayer(fromPlayer);
        strike(fromPlayer, closestPlayer);
    }

    private void strike(Player from, Player to) {
        if (to.strike(from))
            lightnings.strike(to);
    }

    public void gameOver(boolean victory) {
        Label label = new Label(null, SkinCache.getDefaultSkin(), "message");
        String message = "";
        if (victory)
            message = "VICTORY!!! YOU ARE THE BOSS!!!";
        else
            message = "GAME OVER, LOSER!!!";
        label.setText(message);

        label.setPosition(getCamera().position.x - label.getTextBounds().x, getCamera().position.y);
        stage().addActor(label);
        gameOver = true;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }
}
