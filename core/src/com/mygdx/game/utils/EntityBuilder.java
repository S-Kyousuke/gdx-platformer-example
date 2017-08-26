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

package com.mygdx.game.utils;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.components.*;
import com.mygdx.game.systems.PlayerSystem;
import com.mygdx.game.systems.RenderingSystem;

public class EntityBuilder {

    private static PooledEngine engine;

    private EntityBuilder() {
    }

    public static Entity getCameraHelper(OrthographicCamera camera, Entity target) {
        TransformComponent transform = new TransformComponent();
        transform.position.set(Mappers.transform.get(target).position);

        CameraHelperComponent cameraHelper = new CameraHelperComponent();
        cameraHelper.target = target;
        cameraHelper.camera = camera;

        return getEntityFrom(transform, cameraHelper);
    }

    public static Entity getMap(World world, TiledMap tilemap, float mapScale, SpriteBatch batch) {
        final float rendererScale = mapScale / RenderingSystem.PIXEL_PER_UNIT;

        TiledMapComponent tiledMap = new TiledMapComponent();
        tiledMap.renderer = new OrthogonalTiledMapRenderer(tilemap, rendererScale, batch);

        PhysicsComponent physics = TiledMapUtils.generateMapPhysics(world, tiledMap);

        return getEntityFrom(tiledMap, physics);
    }

    public static Entity getPlayer(World world, float x, float y) {
        final float playerScale = 0.7f;
        final float playerWidth = 0.45f;
        final float playerHeight = 0.67f;
        final float frameDuration = 1.0f / 8;

        TextureComponent sprite = new TextureComponent();

        TransformComponent transform = new TransformComponent();
        transform.scale.set(playerScale, playerScale);
        transform.position.set(x, y);

        TextureAnimatorBuilder animatorBuilder = new TextureAnimatorBuilder(Assets.instance.playerAtlas);
        animatorBuilder.addLoopAnimation(PlayerAnimation.IDLE, "player_idle", frameDuration);
        animatorBuilder.addLoopAnimation(PlayerAnimation.WALKING, "player_walk", frameDuration);
        animatorBuilder.addLoopAnimation(PlayerAnimation.JUMPING, "player_jump", frameDuration);
        TextureAnimatorComponent animator = animatorBuilder.getAnimator();

        animator.currentAnimation = PlayerAnimation.IDLE;

        final CharacterComponent character = new CharacterComponent();

        PhysicsComponent physics = new PhysicsComponent();
        BodyDef bodyDef = Box2dUtils.getBodyDef(BodyDef.BodyType.DynamicBody, transform);
        physics.body = world.createBody(bodyDef);
        physics.body.setFixedRotation(true);

        final float halfBoxWidth = playerWidth / 2;
        final float halfBoxHeight = (playerHeight - halfBoxWidth) / 2;

        final Vector2 fixtureOffset = new Vector2(0f, -0.155f);

        final Vector2 boxPosition = new Vector2(0, halfBoxHeight);
        final Vector2 circlePosition = new Vector2();
        final Vector2 sensorPosition = new Vector2(0, -halfBoxWidth);

        boxPosition.add(fixtureOffset);
        circlePosition.add(fixtureOffset);
        sensorPosition.add(fixtureOffset);

        Box2dUtils.setBoxShape(halfBoxWidth, halfBoxHeight, boxPosition, 0f);
        FixtureDef boxFixtureDef = Box2dUtils.getBoxFixtureDef(1f, 0.2f, 0f);
        physics.body.createFixture(boxFixtureDef);

        Box2dUtils.setCircleShape(halfBoxWidth, circlePosition);
        FixtureDef circleFixtureDef = Box2dUtils.getCircleFixtureDef(1f, 100f, 0f);
        physics.body.createFixture(circleFixtureDef);

        Box2dUtils.setBoxShape(playerWidth / 5, playerWidth / 20, sensorPosition, 0f);
        Fixture footSensor = physics.body.createFixture(Box2dUtils.getBoxFixtureDef(0f, 0f, 0f));
        footSensor.setSensor(true);
        footSensor.setUserData(new FixtureData() {
            @Override
            public FixtureType getFixtureType() {
                return FixtureType.FOOT_SENSOR;
            }

            @Override
            public void beginContact(Contact contact) {
                character.footContacts.add(contact);
                character.footContactsCount++;
            }

            @Override
            public void endContact(Contact contact) {
                character.footContacts.removeValue(contact, true);
                character.footContactsCount--;
            }
        });

        PlayerComponent playerComponent = new PlayerComponent();
        Entity player = getEntityFrom(physics, sprite, transform, animator, character, playerComponent);

        playerComponent.stateMachine.setOwner(player);
        playerComponent.stateMachine.setInitialState(PlayerSystem.PlayerState.IDLE);

        return player;
    }

    private static Entity getEntityFrom(Component... components) {
        Entity entity = engine.createEntity();
        for (Component component : components) {
            entity.add(component);
        }
        return entity;
    }

    public static void setEngine(PooledEngine engine) {
        EntityBuilder.engine = engine;
    }
}
