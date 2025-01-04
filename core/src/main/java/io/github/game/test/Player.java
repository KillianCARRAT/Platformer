package io.github.game.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
    // Jump on side
    private final Animation<TextureRegion> jumpSideAnimation;
    private final Animation<TextureRegion> landingSideAnimation;

    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float stateTimePrecede;

    // Move
    private final float gravity = -32f;
    private boolean isGrounded = true;
    private float distanceInAir;
    private final float speedInAir = 64f;

    private final TiledMapTileLayer layer;

    public Player(TextureAtlas atlasPlayer, float widthPlayer, float heightPlayer, GameScreen screen) {
        this.screen = screen;
        this.position = new Vector2(screen.getSpawnPoint().x, screen.getSpawnPoint().y);
        this.velocity = new Vector2(0, 0);
        player = new Sprite(atlasPlayer.findRegion("player_idle1"));
        player.setBounds(position.x + (widthPlayer / 2 - widthPlayer), position.y + (heightPlayer / 2 - heightPlayer), widthPlayer, heightPlayer);

        this.widthPlayer = widthPlayer;
        this.heightPlayer = heightPlayer;

        layer = (TiledMapTileLayer) screen.getMap().getLayers().get("mapTuto");

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


        currentAnimation = idleAnimation;
        stateTime = 0;
        stateTimePrecede = 0;
    }

    public void update(float deltaTime) {
        stateTimePrecede = stateTime;
        stateTime += deltaTime;

        if (!isGrounded) {
            if (distanceInAir < 35) {
                distanceInAir += (speedInAir * deltaTime);
                velocity.y = speedInAir;
            } else {
                velocity.y = newPosY(gravity);
            }
        }

        position.x += velocity.x * deltaTime;
        checkHorizontalCollision();

        position.y += velocity.y * deltaTime;
        checkVerticalCollision();

        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        player.setPosition(position.x + (widthPlayer / 2 - widthPlayer), position.y + (heightPlayer / 2 - heightPlayer));

        checkIfGrounded();

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

    private void checkIfGrounded() {
        float bottomY = position.y - heightPlayer / 2 - 1;
        float leftX = position.x - widthPlayer / 2;
        float rightX = position.x + widthPlayer / 2;


        int bottomTileY = (int) (bottomY / layer.getTileHeight());
        int leftTileX = (int) (leftX / layer.getTileWidth());
        int rightTileX = (int) (rightX / layer.getTileWidth());


        if (isTileSolid(leftTileX, bottomTileY) || isTileSolid(rightTileX, bottomTileY)) {
            isGrounded = true;
        } else {
            isGrounded = false;
            velocity.y = newPosY(gravity);
        }
    }

    private void checkHorizontalCollision() {
        float leftX = position.x - widthPlayer / 2;
        float rightX = position.x + widthPlayer / 2;
        float topY = position.y + heightPlayer / 2;
        float bottomY = position.y - heightPlayer / 2;


        int topTileY = (int) (topY / layer.getTileHeight());
        int bottomTileY = (int) (bottomY / layer.getTileHeight());
        int leftTileX = (int) (leftX / layer.getTileWidth());
        int rightTileX = (int) (rightX / layer.getTileWidth());


        if (velocity.x < 0 && (isTileSolid(leftTileX, topTileY) || isTileSolid(leftTileX, bottomTileY))) {
            position.x = (leftTileX + 1) * layer.getTileWidth() + widthPlayer / 2;
            velocity.x = 0;
        }

        if (velocity.x > 0 && (isTileSolid(rightTileX, topTileY) || isTileSolid(rightTileX, bottomTileY))) {
            position.x = rightTileX * layer.getTileWidth() - widthPlayer / 2;
            velocity.x = 0;
        }
    }

    private void checkVerticalCollision() {
        float leftX = position.x - widthPlayer / 2;
        float rightX = position.x + widthPlayer / 2;
        float topY = position.y + heightPlayer / 2;
        float bottomY = position.y - heightPlayer / 2;


        int topTileY = (int) (topY / layer.getTileHeight());
        int bottomTileY = (int) (bottomY / layer.getTileHeight());
        int leftTileX = (int) (leftX / layer.getTileWidth());
        int rightTileX = (int) (rightX / layer.getTileWidth());


        if (velocity.y < 0 && (isTileSolid(leftTileX, bottomTileY) || isTileSolid(rightTileX, bottomTileY))) {
            position.y = (bottomTileY + 1) * layer.getTileHeight() + heightPlayer / 2;
            velocity.y = 0;
            isGrounded = true;
        }

        if (velocity.y > 0 && (isTileSolid(leftTileX, topTileY) || isTileSolid(rightTileX, topTileY))) {
            position.y = topTileY * layer.getTileHeight() - heightPlayer / 2;
            velocity.y = 0;
        }
    }


    private boolean isTileSolid(int tileX, int tileY) {
        if (tileX < 0 || tileX >= layer.getWidth() || tileY < 0 || tileY >= layer.getHeight()) {
            return false;
        }
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        return cell != null && cell.getTile() != null;
    }


    public float newPosX(float x) {
        float nextX = position.x + x * (stateTime - stateTimePrecede);
        float bottomY = position.y - heightPlayer / 2;
        float topY = position.y + heightPlayer / 2;


        int leftTileX = (int) ((nextX - widthPlayer / 2) / layer.getTileWidth());
        int rightTileX = (int) ((nextX + widthPlayer / 2) / layer.getTileWidth());
        int bottomTileY = (int) (bottomY / layer.getTileHeight());
        int topTileY = (int) (topY / layer.getTileHeight());


        if (x < 0) {
            if (isTileSolid(leftTileX, bottomTileY) || isTileSolid(leftTileX, topTileY)) {
                return 0;
            }
        } else if (x > 0) {
            if (isTileSolid(rightTileX, bottomTileY) || isTileSolid(rightTileX, topTileY)) {
                return 0;
            }
        }
        return x;
    }


    public float newPosY(float y) {
        float nextY = position.y + y * (stateTime - stateTimePrecede);
        float leftX = position.x - widthPlayer / 2;
        float rightX = position.x + widthPlayer / 2;


        int bottomTileY = (int) ((nextY - heightPlayer / 2) / layer.getTileHeight());
        int topTileY = (int) ((nextY + heightPlayer / 2) / layer.getTileHeight());
        int leftTileX = (int) (leftX / layer.getTileWidth());
        int rightTileX = (int) (rightX / layer.getTileWidth());


        if (y < 0) {
            if (isTileSolid(leftTileX, bottomTileY) || isTileSolid(rightTileX, bottomTileY)) {
                position.y = (bottomTileY + 1) * layer.getTileHeight() + heightPlayer / 2;
                isGrounded = true;
                return 0;
            }
        } else if (y > 0) {
            if (isTileSolid(leftTileX, topTileY) || isTileSolid(rightTileX, topTileY)) {
                return 0;
            }
        }

        isGrounded = false;
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

        if (isGrounded && jump) {
            isGrounded = false;
            distanceInAir = 0;
            move(moveLeft ? -speedX : moveRight ? speedX : 0, speedInAir);
        } else if (moveLeft || moveRight) {
            move(moveLeft ? -speedX : speedX, isGrounded ? 0 : speedInAir);
        } else {
            stop();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            position.set(screen.getSpawnPoint().x, screen.getSpawnPoint().y);
        }
    }

    public Sprite getPlayer() {
        return player;
    }

}
