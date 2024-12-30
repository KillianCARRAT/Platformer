package io.github.game.test;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.game.test.camera.Orthographic;

public class GameScreen implements Screen {
    private SpriteBatch batch;
    private Player player;
    private Orthographic camera;
    private TextureAtlas atlasPlayer;
    private Texture background;
    private Platformer game;
    private float centerX;
    private float centerY;

    public GameScreen(Platformer game) {
        this.game = game;
        camera = new Orthographic();
        camera.centerOn(camera.getCamera().viewportWidth / 2f,
            camera.getCamera().viewportHeight / 2f);

        atlasPlayer = new TextureAtlas(Utils.getInternalPath("atlas/player_atlas.atlas"));
        batch = new SpriteBatch();
        background = new Texture(Utils.getInternalPath("map/tiles_background/background.png"));

        centerX = camera.getCamera().viewportWidth / 2f;
        centerY = camera.getCamera().viewportHeight / 2f;

        player = new Player(atlasPlayer, centerX, centerY);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        player.handleInput();
        player.update(delta);

        batch.setProjectionMatrix(camera.getCamera().combined);

        float width = camera.getCamera().viewportWidth;
        float height = camera.getCamera().viewportHeight;

        batch.begin();
        batch.draw(background, 0, 0, width, height);
        player.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
        camera.centerOn(camera.getCamera().viewportWidth / 2f,
            camera.getCamera().viewportHeight / 2f);
        centerX = camera.getCamera().viewportWidth / 2f;
        centerY = camera.getCamera().viewportHeight / 2f;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        atlasPlayer.dispose();
    }
}
