package com.vdroog1.shamans;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.vdroog1.shamans.data.ImageCache;
import com.vdroog1.shamans.data.MusicCache;
import com.vdroog1.shamans.data.SkinCache;
import com.vdroog1.shamans.data.SoundCache;
import com.vdroog1.shamans.screen.GameScreen;
import com.vdroog1.shamans.screen.MenuScreen;

public class ShamanGame extends Game {

	@Override
	public void create () {
        ImageCache.load();
        SkinCache.load();
        SoundCache.load();
        Gdx.input.setCatchBackKey(true);
        setMenuScreen();
    }

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void dispose() {
        Screen screen = getScreen();
        if (screen != null) screen.dispose();
        super.dispose();
    }

    @Override
    public void setScreen(Screen screen) {
        Screen old = getScreen();
        super.setScreen(screen);
        if (old != null) old.dispose();
    }

    public void setGameScreen() {
        setScreen(new GameScreen(this));
    }
    public void setMenuScreen() {
        setScreen(new MenuScreen(this));
    }
}
