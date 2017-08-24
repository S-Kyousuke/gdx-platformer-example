/*
 *    Copyright 2017 Surasek Nusati <surasek@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mygdx.game.screens;

import com.badlogic.ashley.core.Entity;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.PhysicsComponent;
import com.mygdx.game.components.TiledMapComponent;
import com.mygdx.game.listeners.BodyRemovalListener;
import com.mygdx.game.listeners.TiledMapCleanupListener;
import com.mygdx.game.listeners.WorldContactListener;
import com.mygdx.game.systems.*;
import com.mygdx.game.utils.*;

public class GameScreen extends AbstractGameScreen {

    private static final int RESOLUTION_WIDTH = 1024;
    private static final int RESOLUTION_HEIGHT = 576;

    private static final float SCENE_WIDTH = RESOLUTION_WIDTH / RenderingSystem.PIXEL_PER_UNIT;
    private static final float SCENE_HEIGHT = RESOLUTION_HEIGHT / RenderingSystem.PIXEL_PER_UNIT;

    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private PooledEngine engine;

    private BodyRemovalListener bodyRemovalListener;
    private TiledMapCleanupListener tiledMapCleanupListener;

    public GameScreen(Game game) {
        super(game);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
        stage = new Stage(new FitViewport(RESOLUTION_WIDTH, RESOLUTION_HEIGHT), batch);

        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(new WorldContactListener());
        debugRenderer = new Box2DDebugRenderer();

        Box2dUtils.init();
        intiEntityEngine();
        EntityBuilder.setEngine(engine);

        final int tilePixelWidth = 128;
        final float tileWorldWidth = 0.5f;
        final float mapScale = DimensionUtils.getScale(tilePixelWidth, tileWorldWidth);
        Entity map = EntityBuilder.getMap(world, Assets.instance.testMap, mapScale, batch);
        Entity player = EntityBuilder.getPlayer(world, 0.6f, 1.725f);
        Entity cameraHelper = EntityBuilder.getCameraHelper(camera, player);

        TiledMapUtils.setCameraHelperBounds(cameraHelper, map);

        engine.addEntity(player);
        engine.addEntity(cameraHelper);
        engine.addEntity(map);
    }

    private void intiEntityEngine() {
        engine = new PooledEngine();

        engine.addSystem(new RenderingSystem(batch, camera));
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, debugRenderer, camera));
        engine.addSystem(new CameraHelperSystem());
        engine.addSystem(new TiledMapRenderingSystem(camera));
        engine.addSystem(new AnimatorSystem());
        engine.addSystem(new CharacterSystem());
        engine.addSystem(new PlayerSystem());
        engine.addSystem(new AiSystem());
        engine.addSystem(new UiSystem(stage));

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
        stage.getViewport().update(width, height);
    }

    @Override
    public void hide() {
        engine.removeAllEntities();
        engine.removeEntityListener(bodyRemovalListener);
        engine.removeEntityListener(tiledMapCleanupListener);

        batch.dispose();
        world.dispose();
        stage.dispose();
        debugRenderer.dispose();

        Box2dUtils.dispose();
    }
}
