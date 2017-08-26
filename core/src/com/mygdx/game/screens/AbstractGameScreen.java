package com.mygdx.game.screens;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.PhysicsComponent;
import com.mygdx.game.components.TiledMapComponent;
import com.mygdx.game.listeners.BodyRemovalListener;
import com.mygdx.game.listeners.TiledMapCleanupListener;
import com.mygdx.game.listeners.WorldContactListener;
import com.mygdx.game.systems.*;
import com.mygdx.game.utils.Box2dUtils;
import com.mygdx.game.utils.EntityBuilder;

public class AbstractGameScreen extends AbstractScreen {

    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private PooledEngine engine;

    private BodyRemovalListener bodyRemovalListener;
    private TiledMapCleanupListener tiledMapCleanupListener;

    public AbstractGameScreen(Game game, float sceneWidth, float sceneHeight) {
        super(game);
        initGameScene(sceneWidth, sceneHeight);
    }

    private void initGameScene(float sceneWidth, float sceneHeight) {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(sceneWidth, sceneHeight, camera);

        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(new WorldContactListener());
        debugRenderer = new Box2DDebugRenderer();

        Box2dUtils.init();
        intiEntityEngine();
        EntityBuilder.setEngine(engine);
    }

    private void intiEntityEngine() {
        engine = new PooledEngine();

        engine.addSystem(new RenderingSystem(batch, camera));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, debugRenderer, camera));
        engine.addSystem(new CameraHelperSystem());
        engine.addSystem(new TiledMapRenderingSystem(camera));
        engine.addSystem(new TextureAnimatorSystem());
        engine.addSystem(new CharacterSystem());
        engine.addSystem(new PlayerSystem());
        engine.addSystem(new AiSystem());

        bodyRemovalListener = new BodyRemovalListener(world);
        tiledMapCleanupListener = new TiledMapCleanupListener();

        engine.addEntityListener(Family.all(PhysicsComponent.class).get(), bodyRemovalListener);
        engine.addEntityListener(Family.all(TiledMapComponent.class).get(), tiledMapCleanupListener);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        engine.removeAllEntities();
        engine.removeEntityListener(bodyRemovalListener);
        engine.removeEntityListener(tiledMapCleanupListener);

        batch.dispose();
        world.dispose();
        debugRenderer.dispose();

        Box2dUtils.dispose();
    }

    public World getWorld() {
        return world;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public PooledEngine getEngine() {
        return engine;
    }
}
