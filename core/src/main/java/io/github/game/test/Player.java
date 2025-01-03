package io.github.game.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private final Sprite player;
    private final Vector2 position;
    private final Vector2 velocity;
    private boolean revert = false;
    private final float widthPlayer;
    private final float heightPlayer;
    private final GameScreen screen;

    // The animations
    // On ground
    private final Animation<TextureRegion> sprintAnimation;
    private final Animation<TextureRegion> idleAnimation;
    // Jump on the spot
    private final Animation<TextureRegion> jumpIdleAnimation;
    private final Animation<TextureRegion> landingIdleAnimation;
    private final Animation<TextureRegion> levitatingIdleAnimation;
    // Jump on side
    private final Animation<TextureRegion> jumpSideAnimation;
    private final Animation<TextureRegion> landingSideAnimation;
    private final Animation<TextureRegion> levitatingSideAnimation;

    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float stateTimePreced;

    // Move
    private final float gravity = -32f;
    private boolean isGrounded = true;
    private float distanceInAir;
    private float speedInAir = 64f;


    public Player(TextureAtlas atlasPlayer, float x, float y, float widthPlayer, float heightPlayer, GameScreen screen) {
        this.screen = screen;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        player = new Sprite(atlasPlayer.findRegion("player_idle1"));
        player.setBounds(position.x + (widthPlayer / 2 - widthPlayer), position.y + (heightPlayer / 2 - heightPlayer), widthPlayer, heightPlayer);

        this.widthPlayer = widthPlayer;
        this.heightPlayer = heightPlayer;

        sprintAnimation = new Animation<TextureRegion>(0.1f,
            atlasPlayer.findRegion("player_sprint1"),
            atlasPlayer.findRegion("player_sprint2"),
            atlasPlayer.findRegion("player_sprint3"),
            atlasPlayer.findRegion("player_sprint4"),
            atlasPlayer.findRegion("player_sprint5")
        );

        idleAnimation = new Animation<TextureRegion>(0.1f,
            atlasPlayer.findRegion("player_idle1"),
            atlasPlayer.findRegion("player_idle2"),
            atlasPlayer.findRegion("player_idle3")
        );

        jumpIdleAnimation = new Animation<>(0.1f,
            atlasPlayer.findRegion("jump_idle0"),
            atlasPlayer.findRegion("jump_idle1")
        );

        landingIdleAnimation = new Animation<>(0.1f,
            atlasPlayer.findRegion("landing_idle0"),
            atlasPlayer.findRegion("landing_idle1")
        );

        jumpSideAnimation = new Animation<>(0.1f,
            atlasPlayer.findRegion("jump_side0"),
            atlasPlayer.findRegion("jump_side1"),
            atlasPlayer.findRegion("jump_side2")
        );

        landingSideAnimation = new Animation<>(0.1f,
            atlasPlayer.findRegion("landing_side0"),
            atlasPlayer.findRegion("landing_side1"),
            atlasPlayer.findRegion("landing_side2")
        );

        levitatingIdleAnimation = new Animation<>(0.1f,
            atlasPlayer.findRegion("levitating_idle0"),
            atlasPlayer.findRegion("levitating_idle1")
        );

        levitatingSideAnimation = new Animation<>(0.1f,
            atlasPlayer.findRegion("levitating_side0"),
            atlasPlayer.findRegion("levitating_side1"),
            atlasPlayer.findRegion("levitating_side2")
        );

        currentAnimation = idleAnimation;
        stateTime = 0;
        stateTimePreced = 0;
    }

    public void update(float deltaTime) {
        stateTimePreced = stateTime;
        stateTime += deltaTime;

        if (!isGrounded) {
            if (distanceInAir < 35) {
                distanceInAir += (speedInAir * deltaTime);
                velocity.y = speedInAir;
            } else {
                velocity.y = newPosY(gravity);
            }
        }

        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        player.setPosition(position.x + (widthPlayer / 2 - widthPlayer), position.y + (heightPlayer / 2 - heightPlayer));

        if (velocity.len() > 0) {
            if (velocity.y > 0) {
                currentAnimation = (velocity.x == 0) ? jumpIdleAnimation : jumpSideAnimation;
            } else if (velocity.y < 0) {
                currentAnimation = (velocity.x == 0) ? landingIdleAnimation : landingSideAnimation;
            } else {
                currentAnimation = sprintAnimation;
            }
        } else {
            currentAnimation = idleAnimation;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        player.setRegion(currentFrame);
        revert(revert);
        player.draw(batch);
    }

    public void move(float x, float y) {
        if (x != 0) {
            revert(x < 0);
        }
        velocity.set(newPosX(x), velocity.y);
    }

    public float newPosX(float x) {
        float width = widthPlayer / 2;
        if (position.x + x * (stateTime - stateTimePreced) < 32 + width) {
            position.set(32 + width, position.y);
            return 0;
        }
        return x;
    }

    public float newPosY(float y) {
        float height = heightPlayer / 2;
        if (position.y + y * (stateTime - stateTimePreced) < 32 + height) {
            position.set(position.x, 32 + height);
            isGrounded = true;
            return 0;
        }
        return y;
    }

    public void stop() {
        velocity.set(0, 0);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void revert(boolean revert) {
        this.revert = revert;
        player.setFlip(revert, false);
    }

    public void handleInput() {
        float speedX = 16f * screen.getCountTileWidthMap() / 10;

        boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        boolean jump = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (isGrounded) {
            if (jump) {
                isGrounded = false;
                distanceInAir = 0;
                if (moveLeft) {
                    move(-speedX, speedInAir);
                } else if (moveRight) {
                    move(speedX, speedInAir);
                } else {
                    move(0, speedInAir);
                }
            } else if (moveLeft) {
                move(-speedX, 0);
            } else if (moveRight) {
                move(speedX, 0);
            } else {
                stop();
            }
        } else {
            if (moveLeft) {
                move(-speedX, speedInAir);
            } else if (moveRight) {
                move(speedX, speedInAir);
            } else {
                stop();
            }
        }
    }

    public Sprite getPlayer() {
        return player;
    }
}
