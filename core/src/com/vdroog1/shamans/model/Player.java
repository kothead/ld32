package com.vdroog1.shamans.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vdroog1.shamans.data.ImageCache;
import com.vdroog1.shamans.interfaces.MovementController;
import com.vdroog1.shamans.interfaces.MovementListener;
import com.vdroog1.shamans.screen.GameScreen;
import com.vdroog1.shamans.view.Message;

/**
 * Created by kettricken on 18.04.2015.
 */
public class Player extends Sprite implements MovementListener {

    public static final float FALL_TIME = 0.8f;
    public static final int STRIKE_DELAY = 2;
    private GameScreen gameScreen;

    private Message message;
    private Array<ArrowButton.Type> spellCasing = new Array<ArrowButton.Type>();

    public void setIsPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Array<ArrowButton.Type> getSpellCasing() {
        return spellCasing;
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    enum State {
        STAND("player", 0, 0, Animation.PlayMode.NORMAL),
        STRIKE("player-strike", 2, 0.1f, Animation.PlayMode.LOOP),
        FALL("player-strike", 2, 0.1f, Animation.PlayMode.LOOP),
        JUMP("player-jump", 0, 0, Animation.PlayMode.NORMAL),
        WALK("player-walk", 2, 0.1f, Animation.PlayMode.LOOP),
        LEFT_JUMP_START("player-dance-left", 2, 0.1f, Animation.PlayMode.NORMAL),
        LEFT_JUMP_STOP("player-dance-left", 2, 0.1f, Animation.PlayMode.REVERSED),
        RIGHT_JUMP_START("player-dance-right", 2, 0.1f, Animation.PlayMode.NORMAL),
        RIGHT_JUMP_STOP("player-dance-right", 2, 0.1f, Animation.PlayMode.REVERSED),;

        private boolean animated;
        private Animation animation;
        private TextureRegion region;

        State(String texture, int count, float duration, Animation.PlayMode playMode) {
            if (count > 0) {
                animated = true;
                animation = new Animation(duration, ImageCache.getFrames(texture, 1, count));
                animation.setPlayMode(playMode);
            } else {
                animated = false;
                region = ImageCache.getTexture(texture);
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
    private boolean isJumping;
    private float collisionStep;
    private TiledMapTileLayer collisionLayer;

    MovementController movementController;
    private String blockedKey = "blocked-top";

    private State state;
    private float stateTime;

    private float strikeDelay = 0;
    private boolean waitingForStrike = false;

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
                fallingTime = 0;
            } else if (state == State.RIGHT_JUMP_START || state == State.LEFT_JUMP_START) {
                onAnyJump();
            } else if (state == State.RIGHT_JUMP_STOP || state == State.LEFT_JUMP_STOP) {
                setState(State.STAND);
                movementController.stopLegJumping();
                if (spellCasing.size == 3) {
                    strikeDelay = 0;
                    waitingForStrike = true;
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

    public void update(float delta, Player closestPlayer) {
        if (!message.process(delta)) {
            stopCasting();
        }

        if (waitingForStrike && strikeDelay > STRIKE_DELAY) {
            gameScreen.castSpell(this);
            stopCasting();
        } else if (waitingForStrike) {
            strikeDelay += delta;
        }

        updateState(delta);
        if (gameScreen.isGameOver() || state == State.STRIKE) return;

        movementController.progress(delta, closestPlayer);


        velocity.y -= gravity;

        if (velocity.y > speed.y)
            velocity.y = speed.y;
        if (velocity.y < - speed.y)
            velocity.y = -speed.y;

        boolean collisionY = false;

        if (movementController.isMovingLeft()) {
            if (state == State.STAND && canJump) setState(State.WALK);
            velocity.x = -speed.x;
        } else if (movementController.isMovingRight()) {
            if (state == State.STAND && canJump) setState(State.WALK);
            velocity.x = speed.x;
        } else {
            if (state == State.WALK) setState(State.STAND);
            velocity.x = 0;
        }

        int oldCellY = getCellY(getY());
        setX(getX() + velocity.x * delta);
        setY(getY() + velocity.y * delta);

        if (getX() < 0) setX(0);
        if (getX() > collisionLayer.getWidth() * gameScreen.getTileWidth() - getWidth())
            setX(collisionLayer.getWidth() * gameScreen.getTileWidth() - getWidth());

        if (state == State.FALL) {
            fallingTime += delta;
            if (getY() < 4 * gameScreen.getTileHeight()) {
                setState(State.STAND);
                movementController.onFallen();
            }
            if (fallingTime < FALL_TIME)
                return;
        }

        boolean collisionX = false;
        if (velocity.x < 0)
            collisionX = collidesLeft();
        else if (velocity.x > 0)
            collisionX = collidesRight();

        if (collisionX) {
            gameScreen.gameOver(isPlayer);
        }

      //  Gdx.app.log("Velocity y ", String.valueOf(velocity.y));
        if (velocity.y < 0) {
            canJump = collisionY = collidesBottom();
            if (oldCellY <= getCellY(getY())) collisionY = false;
            if (isJumping) {
                isJumping = false;
                finishJumpAnimation();
            }
        }

        if (collisionY) {
            if (state == State.FALL) {
                setState(State.STAND);
                movementController.onFallen();
            }
            setY(getY() - velocity.y * delta);
            velocity.y = 0;
        }

        if (getY() < 4 * gameScreen.getTileHeight()) {
            setY(4 * gameScreen.getTileHeight());
        }

    }

    private void stopCasting() {
        strikeDelay = 0;
        waitingForStrike = false;
        spellCasing.clear();
    }

    public boolean strike(Player fromPlayer) {
        Gdx.app.log("Test", "Strike");
        if (isSpellCanceled(fromPlayer)) {
            Gdx.app.log("Test", "Spell Canceled");
            setState(State.STAND);
            stopCasting();
            message.reset();
            return false;
        } else {
            Gdx.app.log("Test", "Spell Works");
            if (gameScreen.isGameOver()) return true;
            movementController.stopLegJumping();
            setState(State.STRIKE);
            stopCasting();
            message.reset();
            return true;
        }
    }

    private boolean isSpellCanceled(Player fromPlayer) {
        for (int i = 0; i < 3; i++) {
            if (i >= fromPlayer.getSpellCasing().size) return false;
            ArrowButton.Type from = fromPlayer.getSpellCasing().get(i);
            if (i >= spellCasing.size) return false;
            ArrowButton.Type to = spellCasing.get(i);
            if (from != to) return false;
        }
        return true;
    }

    private void finishJumpAnimation() {
        if (gameScreen.isGameOver()) return;

        if (state == State.RIGHT_JUMP_START) setState(State.RIGHT_JUMP_STOP);
        if (state == State.LEFT_JUMP_START) setState(State.LEFT_JUMP_STOP);
        if (state == State.JUMP) setState(State.STAND);
    }

    private boolean collidesBottom() {
        for (int step = 0; step < getWidth(); step += collisionStep) {
            if (isCellBlocked(getX() + step, getY())) {
                return true;
            }
        }
        return false;
    }

    public boolean collidesRight() {
        for(float step = 0; step <= getHeight(); step += collisionStep)
            if(hasTotem(getX() + getWidth(), getY() + step))
                return true;
        return false;
    }

    private boolean hasTotem(float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(getCellX(x), getCellY(y));
        return cell != null && cell.getTile() != null
                && cell.getTile().getProperties().containsKey("object")
                && cell.getTile().getProperties().get("object", String.class).equals("totem");
    }

    public boolean collidesLeft() {
        for(float step = 0; step <= getHeight(); step += collisionStep)
            if(hasTotem(getX(), getY() + step))
                return true;
        return false;
    }

    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(getCellX(x), getCellY(y));
        return cell != null && cell.getTile() != null
                && cell.getTile().getProperties().containsKey(blockedKey);
    }

    @Override
    public void onJump() {
        if (state == State.STRIKE || !gameScreen.isPlayable())
            return;
        if (canJump) {
            setState(State.JUMP);
            onAnyJump();
        }
    }

    private void onAnyJump() {
        if (canJump) {
            isJumping = true;
            velocity.y = speed.y;
            canJump = false;
        }
    }

    @Override
    public void onLeftLegJump() {
        if (state == State.STRIKE || !gameScreen.isPlayable())
            return;
        if (!isCastingJumping()) {
            addSpellCasting(ArrowButton.Type.LEFT);
            setState(State.LEFT_JUMP_START);
        }
    }

    private void addSpellCasting(ArrowButton.Type type) {
        message.reset();
        spellCasing.insert(0, type);
        if (spellCasing.size > 3) spellCasing.pop();
    }

    @Override
    public void onRightLegJump() {
        if (state == State.STRIKE || !gameScreen.isPlayable())
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

    public int getCellX() {
        return getCellX(getX());
    }

    public int getCellY() {
        return getCellY(getY());
    }

    private int getCellX(float x) {
        return (int) (x / gameScreen.getTileWidth());
    }

    private int getCellY(float y) {
        return (int) (y / gameScreen.getTileHeight());
    }
}
