package com.vdroog1.shamans.view;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.model.ArrowButton;
import com.vdroog1.shamans.model.Player;

/**
 * Created by kettricken on 18.04.2015.
 */
public class Message {

    private static final float TIME_FOR_MESSAGE = 5f;

    private static final float MESSAGE_HORIZONTAL_PADDING = -20;
    private static final float MESSAGE_VERTICAL_PADDING = 10;

    private Label label;
    private Player player;

    private float timer;

    Array<ArrowButton.Type> spellCasting;

    String[] spell = {"Uga", " Buga", " Chuga"};

    public Message(Label label, Player player, Array<ArrowButton.Type> spellCasting) {
        this.label = label;
        this.player = player;
        this.spellCasting = spellCasting;
    }

    public boolean process(float delta) {
        String spellString = "";
        for (int i = 0; i < Math.min(3, spellCasting.size); i++) {
            spellString += spell[i];
        }

        label.setText(spellString);
        label.setPosition(player.getX(),
                player.getY() + player.getHeight() + MESSAGE_VERTICAL_PADDING);

        timer += delta;
        if (timer > TIME_FOR_MESSAGE) {
            timer = 0;
            return false;
        }

        return true;
    }

    public void reset() {
        timer = 0;
    }
}
