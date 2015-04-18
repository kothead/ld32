package com.vdroog1.shamans.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.vdroog1.shamans.data.ImageCache;
import com.vdroog1.shamans.interfaces.MovementController;
import com.vdroog1.shamans.interfaces.MovementListener;
import com.vdroog1.shamans.screen.GameScreen;

/**
 * Created by kettricken on 18.04.2015.
 */
public class Player extends Sprite implements MovementListener {

    private final Vector2 speed = new Vector2(300, 750);
    private final float gravity = 50;//80 * 1.8f;

    private final Vector2 velocity = new Vector2();

    private boolean canJump;
    private float collisionStep;
    private TiledMapTileLayer collisionLayer;

    MovementController movementController;
    private String blockedKey = "blocked-top";

    public Player(TiledMapTileLayer collisionLayer) {
        super(ImageCache.getTexture("player"));

        this.collisionLayer = collisionLayer;

        collisionStep = collisionLayer.getTileWidth();
        collisionStep = getWidth() < collisionStep ? getWidth() / 2 : collisionStep / 2;
    }


    public void setMovementController(MovementController movementController) {
        this.movementController = movementController;
        this.movementController.setMovementListener(this);
    }

    public MovementController getMovementController() {
        return movementController;
    }

    public void update(float delta) {
        movementController.progress(delta);

        velocity.y -= gravity;

        if (velocity.y > speed.y)
            velocity.y = speed.y;
        if (velocity.y < - speed.y)
            velocity.y = -speed.y;

        boolean collisionY = false;

        if (movementController.isMovingLeft()) {
            velocity.x = -speed.x;
        } else if (movementController.isMovingRight()) {
            velocity.x = speed.x;
        } else {
            velocity.x = 0;
        }

        TiledMapTileLayer.Cell oldCell = collisionLayer.getCell((int) (getX() / (collisionLayer.getTileWidth() * GameScreen.UNIT_SCALE)),
                (int) (getY() / (collisionLayer.getTileHeight() * GameScreen.UNIT_SCALE)));

        setX(getX() + velocity.x * delta);

        setY(getY() + velocity.y * delta);

        if (velocity.y < 0) {

            canJump = collisionY = collidesBottom(oldCell);
        }
        if (collisionY) {
            setY(getY() - velocity.y * delta);
            velocity.y = 0;
        }

    }

    private boolean collidesBottom(TiledMapTileLayer.Cell oldCell) {
        for (int step = 0; step < getWidth(); step += collisionStep) {
            if (isCellBlocked(getX() + step, getY(), oldCell)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCellBlocked(float x, float y, TiledMapTileLayer.Cell oldCell) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / (collisionLayer.getTileWidth() * GameScreen.UNIT_SCALE)),
                (int) (y / (collisionLayer.getTileHeight() * GameScreen.UNIT_SCALE)));
        return cell != null && cell.getTile() != null &&
                cell.getTile().getProperties().containsKey(blockedKey);
    }

    @Override
    public void onJump() {
        if (canJump) {
            velocity.y = speed.y;
            canJump = false;
        }
    }
}
