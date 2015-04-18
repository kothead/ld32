package com.vdroog1.shamans.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.vdroog1.shamans.ShamanGame;
import com.vdroog1.shamans.data.Configuration;

import java.io.File;
import java.io.FileFilter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = Configuration.GAME_WIDTH;
        config.height = Configuration.GAME_HEIGHT;
        config.samples = 8;
		new LwjglApplication(new ShamanGame(), config);
	}

    private static void packAssets() {
        File dir = new File("../../images");

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.edgePadding = true;
        settings.duplicatePadding = true;
        settings.paddingX = 4;
        settings.paddingY = 4;

        for (File childDir: dir.listFiles(filter)) {
            TexturePacker.process(settings, childDir.getPath(), "data", childDir.getName());
        }
    }
}
