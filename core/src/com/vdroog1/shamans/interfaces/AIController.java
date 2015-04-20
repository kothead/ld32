package com.vdroog1.shamans.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.ai.AStarPathFinder;
import com.vdroog1.shamans.ai.Path;
import com.vdroog1.shamans.model.ArrowButton;
import com.vdroog1.shamans.model.Player;
import com.vdroog1.shamans.screen.GameScreen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kettricken on 19.04.2015.
 */
public class AIController implements MovementController {

    private static final float TIME_WAKE_UP = 3;

    Array<Vector2> positions = new Array(){{
        add(new Vector2(41, 249 - 223));
        add(new Vector2(34, 249 - 206));
        add(new Vector2(35, 249 - 196));
        add(new Vector2(31, 249 - 124));
        add(new Vector2(48, 249 - 209));
    }};

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    Player player;
    MovementListener listener;

    boolean isMovingRight = false;
    boolean isMovingLeft = false;
    boolean isCasting = false;
    boolean isSleeping = false;

    float timeBetweenSpells = 0;
    float wakeUpTimer = 0;

    boolean isPathSearchRunning = false;

    Path path;
    AStarPathFinder pathFinder;

    Vector2 target = new Vector2();

    public AIController(Player player) {
        this.player = player;
        pathFinder = new AStarPathFinder(player.getCollisionLayer());
        setMovementListener(player);
    }

    @Override
    public void progress(float delta, Player closestPlayer) {
        if (isSleeping) {
            wakeUpTimer += delta;
        }

        timeBetweenSpells += delta;

        float distance = closestPlayer.getY() - player.getY();
        int mySize = player.getSpellCasing().size;
        int enemySize = closestPlayer.getSpellCasing().size;
        if (enemySize >= 2 && !isCasting && enemySize > mySize) {
            ArrowButton.Type jump = closestPlayer.getSpellCasing().get(enemySize - mySize - 1);
            //random error
            int randomNum = MathUtils.random(0, 7);
            if (randomNum != 0) {
                if (jump == ArrowButton.Type.RIGHT) listener.onRightLegJump();
                else listener.onLeftLegJump();
            } else {
                if (jump == ArrowButton.Type.RIGHT) listener.onLeftLegJump();
                else listener.onRightLegJump();
            }
            isCasting = true;
        } else if ((distance > 200 || distance < -50) && !isCasting && timeBetweenSpells > 1.5
                && enemySize <= mySize && mySize < 3) {
            int randomNum = MathUtils.random(0, 1);
            if (randomNum == 0) listener.onLeftLegJump();
            else listener.onRightLegJump();
            isCasting = true;
            timeBetweenSpells = 0;
        }

        if (isCasting) {
            isMovingRight = false;
            isMovingLeft = false;
            return;
        }

        if (path == null || path.getLength() == 0) {
            if (!isPathSearchRunning) {
                findPath();
            }

            isMovingRight = false;
            isMovingLeft = false;
            return;
        }

        if (isPathSearchRunning) return;

        target.set(path.getStep(0).x * player.getCollisionLayer().getTileWidth() * GameScreen.UNIT_SCALE,
                path.getStep(0).y * player.getCollisionLayer().getTileHeight() * GameScreen.UNIT_SCALE);

        //Gdx.app.log("Test", "target [" + target.x + ", " + target.y + "]");

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

    private void findPath() {
        if (isSleeping && wakeUpTimer > TIME_WAKE_UP) {
            isSleeping = false;
        }
        if (isSleeping) {
            return;
        }

        executor.submit(new Runnable() {
            @Override
            public void run() {
                Gdx.app.log("Test", "start path search");
                isPathSearchRunning = true;
                Vector2 closestControlPoint = getControlPoint();
                if (closestControlPoint == null) {
                    return;
                }
                path = pathFinder.findPath(new Vector2(player.getCellX(), player.getCellY()), closestControlPoint);
                isPathSearchRunning = false;
                if (path == null) {
                    isSleeping = true;
                    wakeUpTimer = 0;
                }
            }
        });
    }

    private Vector2 getControlPoint() {
        Vector2 closestControlPoint = null;
        int minDif = Integer.MAX_VALUE;
        for (Vector2 controlPoint : positions) {
            float dif = controlPoint.y - player.getCellY();
            if (dif > 0 && dif < minDif){
                closestControlPoint = controlPoint;
            }
        }
        return closestControlPoint;
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
        if (player != null)
            listener = player;

    }

    @Override
    public void stopLegJumping() {
        isCasting = false;
        isSleeping = false;
        findPath();
    }

    @Override
    public void onFallen() {
        isSleeping = false;
        findPath();
    }
}
