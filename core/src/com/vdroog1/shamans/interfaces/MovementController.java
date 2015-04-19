package com.vdroog1.shamans.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by kettricken on 18.04.2015.
 */
public interface MovementController {

    Vector2 direction = new Vector2();

    public void progress(float delta);
    public boolean isMovingRight();
    public boolean isMovingLeft();
    public void setMovementListener(MovementListener movementListener);

    public void stopLegJumping();
}
