package com.vdroog1.shamans.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * Created by kettricken on 18.04.2015.
 */
public class InputController extends InputAdapter implements MovementController {

    MovementListener movementListener;

    @Override
    public void progress(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.x = -1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)){
            direction.x = 1;
        } else {
            direction.x = 0;
        }
    }

    @Override
    public boolean isMovingRight() {
        return direction.x == 1;
    }

    @Override
    public boolean isMovingLeft() {
        return direction.x == -1;
    }

    @Override
    public void setMovementListener(MovementListener movementListener) {
        this.movementListener = movementListener;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.W:
                if (movementListener != null)
                    movementListener.onJump();
                break;
            case Input.Keys.LEFT:
                if (movementListener != null)
                    movementListener.onLeftLegJump();
                break;
            case Input.Keys.RIGHT:
                if (movementListener != null)
                    movementListener.onRightLegJump();
                break;
            default:
                break;
        }
        return super.keyDown(keycode);
    }
}
