package com.vdroog1.shamans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.data.ImageCache;
import com.vdroog1.shamans.data.MusicCache;
import com.vdroog1.shamans.data.SkinCache;
import com.vdroog1.shamans.data.SoundCache;
import com.vdroog1.shamans.interfaces.AIController;
import com.vdroog1.shamans.interfaces.InputController;
import com.vdroog1.shamans.model.LightningController;
import com.vdroog1.shamans.model.Player;
import com.vdroog1.shamans.view.Message;

/**
 * Created by st on 4/18/15.
 */
public class GameScreen extends BaseScreen {

    public static final int MIN_PLAYER_NUM = 2;
    public static final int MAX_PLAYER_NUM = 10;

    public static final int UNIT_SCALE = 4;
    private TiledMap map;
    private float mapWidth, mapHeight;
    private float tileWidth, tileHeight;
    private OrthogonalTiledMapRenderer renderer;
    private LightningController lightnings;

    Array<Player> players = new Array<Player>();

    boolean gameOver = false;
    boolean gamePaused = false;

    Image restart;
    Image play;

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

        initGui();

        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage());
        inputMultiplexer.addProcessor((InputController) players.get(0).getMovementController());
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (isPlayable()) {
                        pauseGame();
                    } else {
                        unpauseGame();
                    }
                }
                return super.keyUp(keycode);
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);

        MusicCache.dispose();
        MusicCache.play(MusicCache.MAIN_GAMEPLAY);
    }

    private void pauseGame() {
        gamePaused = true;

        restart.setPosition(getCamera().position.x - restart.getWidth() / 2f + 100, getCamera().position.y - restart.getHeight()  / 2f);
        play.setPosition(getCamera().position.x - play.getWidth() / 2f - 100, getCamera().position.y - play.getHeight() / 2f);

        stage().addActor(restart);
        stage().addActor(play);
    }

    private void unpauseGame() {
        gamePaused = false;
        stage().clear();
    }

    private void initGui() {
        restart = new Image(ImageCache.getTexture("restart"));
        play = new Image(ImageCache.getTexture("play"));

        restart.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                getGame().setGameScreen();
            }
        });

        play.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                gamePaused = false;
                stage().clear();
            }
        });
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        super.dispose();
    }

    private void generatePlayers(TiledMapTileLayer tileLayer) {
        int playerNum = (int) (Math.random() * (MAX_PLAYER_NUM - MIN_PLAYER_NUM)) + MIN_PLAYER_NUM;
        for (int i = 0; i < playerNum; i++) {
            Label label = new Label(null, SkinCache.getDefaultSkin(), "message");
            stage().addActor(label);

            Player player = new Player(this, tileLayer);
            player.setIsPlayer(i == 0);
            player.setMovementController(i ==0 ? new InputController() : new AIController(player));
            float x = MathUtils.random(0, getWorldWidth() - player.getWidth());
            player.setPosition(x, 4 * getTileHeight());
            player.setMessage(new Message(stage(), player, player.getSpellCasing()));
            players.add(player);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl20.glClearColor(0.8f, 0.8f, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera(players.get(0));

        if (isPlayable()) {
            for (Player player : players) {
                Player closestPlayer = getClosestPlayer(player);
                player.update(delta, closestPlayer);
            }
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
        if (to != null && to.strike(from))
            lightnings.strike(to);
    }

    public void gameOver(boolean victory) {
        MusicCache.dispose();
        SoundCache.play(SoundCache.SOUND_WIN);

        Image result;
        if (victory)
            result = new Image(ImageCache.getTexture("victory"));
        else
            result = new Image(ImageCache.getTexture("game-over"));

        result.setPosition(getCamera().position.x - result.getWidth() / 2, getCamera().position.y);
        restart.setPosition(getCamera().position.x - restart.getWidth() / 2, result.getY() - restart.getHeight() - 50);
        stage().addActor(result);
        stage().addActor(restart);
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

    public boolean isPlayable() {
        return !gamePaused && !gameOver;
    }
}
