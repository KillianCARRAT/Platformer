package io.github.game.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Sprite player;
    private Vector2 position;
    private Vector2 velocity;
    private boolean revert = false;
    private TextureAtlas atlasPlayer;

    // The animations
    // On ground
    private Animation<TextureRegion> sprintAnimation;
    private Animation<TextureRegion> idleAnimation;
    // Jump on the spot
    private Animation<TextureRegion> jumpIdleAnimation;
    private Animation<TextureRegion> landingIdleAnimation;
    private Animation<TextureRegion> levitatingIdleAnimation;
    // Jump on side
    private Animation<TextureRegion> jumpSideAnimation;
    private Animation<TextureRegion> landingSideAnimation;
    private Animation<TextureRegion> levitatingSideAnimation;

    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    public Player(TextureAtlas atlasPlayer, float x, float y) {
        this.atlasPlayer = atlasPlayer;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        player = new Sprite(atlasPlayer.findRegion("player_idle1"));
        player.setScale(0.3f);

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
    }

    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        player.setPosition(position.x, position.y);

        stateTime += deltaTime;

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
        velocity.set(x, y);
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
        float speedX = 40f;
        float speedY = 25f;


        if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) &&
            (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))) {
            move(-speedX, speedY);
        } else if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) &&
            (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))) {
            move(speedX, speedY);
        } else if ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) &&
            (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
            move(-speedX, -speedY);
        } else if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) &&
            (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
            move(speedX, -speedY);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            move(-speedX, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            move(speedX, 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            move(0, speedY);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            move(0, -speedY);
        } else {
            stop();
        }

    }
}
