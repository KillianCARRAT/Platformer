package io.github.game.test;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.game.test.camera.Orthographic;

public class GameScreen implements Screen {
    private final Platformer game;

    // Display
    private final SpriteBatch batch;
    private final Orthographic camera;
    private final TextureAtlas atlasPlayer;
    private final Texture background;

    // Map
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final ShapeRenderer shapeRenderer;
    private final int widthTile;
    private final int heightTile;
    private final int countTileWidthMap;
    private final int countTileHeightMap;

    // Player
    private final Player player;
    private float spawnPointX;
    private float spawnPointY;


    public GameScreen(Platformer game) {
        this.game = game;
        background = new Texture(Utils.getInternalPath("graphics/tiles_background/background.png"));


        camera = new Orthographic();

        map = new TmxMapLoader().load("maps/mapTuto.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        widthTile = (int) map.getProperties().get("tilewidth");
        heightTile = (int) map.getProperties().get("tileheight");
        countTileWidthMap = (int) map.getProperties().get("width");
        countTileHeightMap = (int) map.getProperties().get("height");

        atlasPlayer = new TextureAtlas(Utils.getInternalPath("atlas/player_atlas.atlas"));
        batch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();

        findSpawnPoint();
        player = new Player(atlasPlayer, spawnPointX, spawnPointY, widthTile, heightTile);

        camera.centerOn(player.getPosition().x, player.getPosition().y);

    }

    private void findSpawnPoint() {
        for (MapObject object : map.getLayers().get("objectif").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                if ("spawnPoint".equals(object.getName())) {
                    spawnPointX = rectangle.x + (float) widthTile / 2;
                    spawnPointY = rectangle.y + (float) heightTile / 2;
                    return;
                }
            }
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        player.handleInput();
        player.update(delta);

        camera.centerOn(player.getPosition().x, player.getPosition().y);


        camera.getCamera().zoom = player.getPlayer().getWidth() / 4;


        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, widthTile * countTileWidthMap, heightTile * countTileHeightMap);
        player.render(batch);
        batch.end();

        mapRenderer.setView(camera.getCamera());
        mapRenderer.render();

        // drawHitbox();
    }

    private void drawHitbox() {
        shapeRenderer.setProjectionMatrix(camera.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float playerWidth = player.getPlayer().getWidth();
        float playerHeight = player.getPlayer().getHeight();


        // shapeRenderer.rect(playerX + (playerWidth / 2 - playerWidth), playerY + (playerHeight / 2 - playerHeight), playerWidth, playerHeight);
        shapeRenderer.circle(playerX, playerY, 1);

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
        camera.centerOn(player.getPosition().x, player.getPosition().y);
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
        mapRenderer.dispose();
    }

    private float calculateZoomFactor(float playerSize) {
        float baseZoom = 1.0f;
        float playerZoomFactor = playerSize / 100.0f;

        return Math.max(0.5f, Math.min(baseZoom * playerZoomFactor, 2.0f));
    }
}
