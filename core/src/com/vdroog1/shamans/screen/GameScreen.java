package com.vdroog1.shamans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.data.SkinCache;
import com.vdroog1.shamans.interfaces.AIController;
import com.vdroog1.shamans.interfaces.InputController;
import com.vdroog1.shamans.model.Player;
import com.vdroog1.shamans.view.Message;

/**
 * Created by st on 4/18/15.
 */
public class GameScreen extends BaseScreen {

    public static final int UNIT_SCALE = 4;
    private TiledMap map;
    private float mapWidth, mapHeight;
    private OrthogonalTiledMapRenderer renderer;

    Player player;
    Player bot;




    boolean gameOver = false;

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

        Label label = new Label(null, SkinCache.getDefaultSkin(), "message");
        stage().addActor(label);
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        player = new Player(this, tileLayer);
        player.setIsPlayer(false);
        player.setMovementController(new InputController());
        player.setPosition(50, 5 * tileLayer.getTileHeight() * UNIT_SCALE);
        player.setMessgae(new Message(label, player, player.getSpellCasing()));

        label = new Label(null, SkinCache.getDefaultSkin(), "message");
        stage().addActor(label);
        bot = new Player(this, tileLayer);
        bot.setIsPlayer(false);
        bot.setMovementController(new AIController(bot));
        bot.setPosition(150, 5 * tileLayer.getTileHeight() * UNIT_SCALE);
        bot.setMessgae(new Message(label, bot, bot.getSpellCasing()));

        mapHeight = tileLayer.getHeight() * tileLayer.getTileHeight() * UNIT_SCALE;
        mapWidth = tileLayer.getWidth() * tileLayer.getTileWidth() * UNIT_SCALE;

        Gdx.input.setInputProcessor((InputController) player.getMovementController());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl20.glClearColor(0.8f, 0.8f, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera(bot);
    //    player.update(delta);
        bot.update(delta);


        renderer.setView(getCamera());
        renderer.render();

        renderer.getBatch().begin();
    //    player.draw(renderer.getBatch());
        bot.draw(renderer.getBatch());
        renderer.getBatch().end();

        stage().act(delta);
        stage().draw();
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
        fromPlayer.strike();
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
}
