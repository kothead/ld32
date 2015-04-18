package com.vdroog1.shamans.model;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private GameScreen gameScreen;

    enum State {
        STAND("player", 0, 0, Animation.PlayMode.NORMAL),
        STRIKE("player-strike", 3, 0.6f, Animation.PlayMode.NORMAL),
        FALL("player-fall", 0, 0, Animation.PlayMode.NORMAL),
        DEAD("player-dead", 0, 0, Animation.PlayMode.NORMAL),
        LEFT_JUMP_START("playerl", 2, 0.1f, Animation.PlayMode.NORMAL),
        LEFT_JUMP_STOP("playerl", 2, 0.1f, Animation.PlayMode.REVERSED),
        RIGHT_JUMP_START("playerr", 2, 0.1f, Animation.PlayMode.NORMAL),
        RIGHT_JUMP_STOP("playerr", 2, 0.1f, Animation.PlayMode.REVERSED),;

        private boolean animated;
        private Animation animation;
        private TextureRegion region;

        State(String texture, int count, float duration, Animation.PlayMode playMode) {
            if (count > 0) {
                animated = true;
                animation = new Animation(duration, ImageCache.getFrames(texture, 1, count));
                animation.setPlayMode(playMode);
            } else {
                region = ImageCache.getTexture("player");
            }
        }

        public TextureRegion getFrame(float stateTime) {
            if (animated) {
                return animation.getKeyFrame(stateTime, false);
            } else {
                return region;
            }
        }
    }

    private final Vector2 speed = new Vector2(300, 750);
    private final float gravity = 50;

    private final Vector2 velocity = new Vector2();

    private boolean canJump;
    private boolean isJumpingLow;
    private float collisionStep;
    private TiledMapTileLayer collisionLayer;

    MovementController movementController;
    private String blockedKey = "blocked-top";

    private State state;
    private float stateTime;

    private float fallingTime = 0;
    private boolean isPlayer = true;

    public Player(GameScreen gameScreen, TiledMapTileLayer collisionLayer) {
        super(ImageCache.getTexture("player"));

        this.gameScreen = gameScreen;

        setState(State.STAND);

        this.collisionLayer = collisionLayer;

        collisionStep = collisionLayer.getTileWidth();
        collisionStep = getWidth() < collisionStep ? getWidth() / 2 : collisionStep / 2;
    }

    @Override
    public void draw(Batch batch) {
        setRegion(getStateFrame());
        super.draw(batch);
    }

    @Override
    public void draw(Batch batch, float alphaModulation) {
        setRegion(getStateFrame());
        super.draw(batch, alphaModulation);
    }

    private void setState(State state) {
        if (gameScreen.isGameOver()) return;
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
        }
    }

    private void updateState(float delta) {
        if (gameScreen.isGameOver()) return;

        stateTime += delta;
        if (state.animated && state.animation.isAnimationFinished(stateTime)) {
            if (state == State.STRIKE) {
                setState(State.FALL);
            } else if (state == State.RIGHT_JUMP_START || state == State.LEFT_JUMP_START) {
                isJumpingLow = true;
                onJump();
            } else if (state == State.RIGHT_JUMP_STOP || state == State.LEFT_JUMP_STOP) {
                setState(State.STAND);
                if (gameScreen.getSpellCount() == 3) {
                    gameScreen.castSpell();
                }
            }

        }
    }

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }

    public void setMovementController(MovementController movementController) {
        this.movementController = movementController;
        this.movementController.setMovementListener(this);
    }

    public MovementController getMovementController() {
        return movementController;
    }

    public void update(float delta) {
        if (state == State.DEAD || gameScreen.isGameOver())
            return;

        movementController.progress(delta);
        updateState(delta);

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

        int oldCellY = getCellY(getY());
        setX(getX() + velocity.x * delta);
        setY(getY() + velocity.y * delta);

        if (state == State.FALL) {
            fallingTime += delta;
            if (getY() + getHeight() < 0) {
                gameScreen.gameOver(false);
                return;
            }
            if (fallingTime < 5)
                return;
        }

        if (velocity.y < 0) {
            canJump = collisionY = collidesBottom();
            if (oldCellY <= getCellY(getY())) collisionY = false;
            if (isJumpingLow) {
                isJumpingLow = false;
                finishJumpAnimation();
            }
        }

        if (collisionY) {
            setY(getY() - velocity.y * delta);
            velocity.y = 0;
        }

    }

    public void strike() {
        if (gameScreen.isGameOver()) return;

        setState(State.STRIKE);
    }

    private void finishJumpAnimation() {
        if (gameScreen.isGameOver()) return;

        if (state == State.RIGHT_JUMP_START)
            setState(State.RIGHT_JUMP_STOP);
        if (state == State.LEFT_JUMP_START)
            setState(State.LEFT_JUMP_STOP);
    }

    private boolean collidesBottom() {
        for (int step = 0; step < getWidth(); step += collisionStep) {
            if (isCellBlocked(getX() + step, getY())) {
                return true;
            }
        }
        return false;
    }

    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(getCellX(x), getCellY(y));
        return cell != null && cell.getTile() != null
                && cell.getTile().getProperties().containsKey(blockedKey);
    }

    @Override
    public void onJump() {
        if (state == State.STRIKE || gameScreen.isGameOver())
            return;
        if (canJump) {
            velocity.y = speed.y;
            canJump = false;
        }
    }

    @Override
    public void onLeftLegJump() {
        if (state == State.STRIKE || gameScreen.isGameOver())
            return;
        if (!isCastingJumping()) {
            addSpellCasting(ArrowButton.Type.LEFT);
            setState(State.LEFT_JUMP_START);
        }
    }

    private void addSpellCasting(ArrowButton.Type type) {
        gameScreen.addSpellCasting(type);
    }

    @Override
    public void onRightLegJump() {
        if (state == State.STRIKE || gameScreen.isGameOver())
            return;
        if (!isCastingJumping()) {
            addSpellCasting(ArrowButton.Type.RIGHT);
            setState(State.RIGHT_JUMP_START);
        }
    }

    private boolean isCastingJumping() {
        return state == State.LEFT_JUMP_START || state == State.LEFT_JUMP_STOP
                || state == State.RIGHT_JUMP_START || state == State.RIGHT_JUMP_STOP;
    }

    private int getCellX(float x) {
        return (int) (x / collisionLayer.getTileWidth() / GameScreen.UNIT_SCALE);
    }

    private int getCellY(float y) {
        return (int) (y / collisionLayer.getTileHeight() / GameScreen.UNIT_SCALE);
    }
}
