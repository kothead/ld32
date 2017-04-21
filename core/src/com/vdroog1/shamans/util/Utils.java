package com.vdroog1.shamans.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by st on 4/18/15.
 */
public class Utils {

    private static final String blockedKey = "blocked-top";

    public static boolean isLandscape() {
        return Gdx.input.getNativeOrientation() == Input.Orientation.Landscape
                && (Gdx.input.getRotation() == 0 || Gdx.input.getRotation() == 180)
                || Gdx.input.getNativeOrientation() == Input.Orientation.Portrait
                && (Gdx.input.getRotation() == 90 || Gdx.input.getRotation() == 270);
    }

    public static boolean isCellBlocked(TiledMapTileLayer tiledMap, int x, int y) {
        TiledMapTileLayer.Cell cell = tiledMap.getCell(x, y);
        return cell != null && cell.getTile() != null
                && cell.getTile().getProperties().containsKey(blockedKey);
    }
}
