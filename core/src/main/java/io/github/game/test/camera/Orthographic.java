package io.github.game.test.camera;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Orthographic implements ApplicationListener {
    private OrthographicCamera camera;
    private final float VIEWPORT_WIDTH = 100f;
    private final float VIEWPORT_HEIGHT = 100f;

    private SpriteBatch batch;
    private Texture background;

    public Orthographic() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
    }

    @Override
    public void create() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT * height / width);
        camera.position.set(0, 0, 0);
        camera.update();

        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("graphics/tiles_background/background.png"));

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = VIEWPORT_WIDTH;
        camera.viewportHeight = VIEWPORT_HEIGHT * height / width;
        camera.update();
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void centerOn(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
    }
}
