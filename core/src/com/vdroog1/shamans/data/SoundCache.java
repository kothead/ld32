package com.vdroog1.shamans.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by st on 9/25/14.
 */
public class SoundCache {

    public static final String SOUND_WIN = "win";
    public static final String SOUND_STRIKE = "strike";
    public static final String SOUND_JUMP = "jump";

    private static final String SOUND_DIR = "sound/";
    private static final String SOUND_EXT = ".wav";

    private static ObjectMap<String, Sound> sounds;

    public static void load() {
        sounds = new ObjectMap<String, Sound>();

        String[] keys = {
                SOUND_WIN, SOUND_STRIKE, SOUND_JUMP
        };
        for (String key: keys) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(SOUND_DIR + key + SOUND_EXT));
            sounds.put(key, sound);
        }
    }

    public static void play(String key) {
        sounds.get(key).play(0.3f);
    }
}
