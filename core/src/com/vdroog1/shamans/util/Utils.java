package com.vdroog1.shamans.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.vdroog1.shamans.screen.GameScreen;

/**
 * Created by st on 4/18/15.
 */
public class Utils {

    public static boolean isLandscape() {
        return Gdx.input.getNativeOrientation() == Input.Orientation.Landscape
                && (Gdx.input.getRotation() == 0 || Gdx.input.getRotation() == 180)
                || Gdx.input.getNativeOrientation() == Input.Orientation.Portrait
                && (Gdx.input.getRotation() == 90 || Gdx.input.getRotation() == 270);
    }
}
