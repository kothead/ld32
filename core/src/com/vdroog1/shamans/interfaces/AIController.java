package com.vdroog1.shamans.interfaces;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.ai.AStarPathFinder;
import com.vdroog1.shamans.ai.Path;
import com.vdroog1.shamans.model.Player;

/**
 * Created by kettricken on 19.04.2015.
 */
public class AIController implements MovementController {

    Array<Vector2> positions = new Array(){{
        add(new Vector2(500, 700));
/*        add(new Vector2(500, 160));
        add(new Vector2(150, 160));
        add(new Vector2(0, 128));*/
    }};


    Player player;
    MovementListener listener;

    boolean isMovingRight = false;
    boolean isMovingLeft = false;
    boolean isCasting = false;

    float timeToSpell = 0;

    int lastControlPoint = 0;

    Path path;
    AStarPathFinder pathFinder;

    public AIController(Player player) {
        this.player = player;
        pathFinder = new AStarPathFinder(player.getCollisionLayer());
        setMovementListener(player);
    }

    public void reset() {
        isMovingLeft = false;
        isMovingRight = false;
    }

    @Override
    public void progress(float delta) {
/*        if (!isCasting)
            timeToSpell += delta;

        if (timeToSpell > 2 && !isCasting) {
            listener.onLeftLegJump();
            isCasting = true;
            timeToSpell = 0;
        }

        if (isCasting) return;*/

        if (path == null || path.getLength() == 0) {
            path = pathFinder.findPath(new Vector2(player.getX(), player.getY()), positions.get(lastControlPoint));
            if (path != null) lastControlPoint++;
            isMovingRight = false;
            isMovingLeft = false;
            return;
        }

        Vector2 target = path.getStep(0);
        float deltaY = target.y - player.getY();
        float deltaX = Math.abs(target.x - player.getX());
        if (deltaY > 1) {
            if (listener != null) {
                listener.onJump();
            }
        }
        if (target.x > player.getX()) {
            isMovingRight = true;
            isMovingLeft = false;
        } else if (target.x < player.getX()) {
            isMovingRight = false;
            isMovingLeft = true;
        } else {
            isMovingRight = false;
            isMovingLeft = false;
        }
        if (deltaY < 1 && deltaX < 5) {
            path.removeStep(0);
            isMovingRight = false;
            isMovingLeft = false;
        }
    }

    @Override
    public boolean isMovingRight() {
        return isMovingRight;
    }

    @Override
    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    @Override
    public void setMovementListener(MovementListener movementListener) {
        if (player instanceof MovementListener)
            listener = player;

    }

    @Override
    public void stopLegJumping() {
        isCasting = false;
    }
}
