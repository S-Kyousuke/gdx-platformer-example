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

package com.mygdx.game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.*;
import com.mygdx.game.utils.Direction;
import com.mygdx.game.utils.Mappers;
import com.mygdx.game.utils.PlayerAnimation;


public class PlayerSystem extends IteratingSystem {

    private static final float MIN_SQUARE_WALK_SPEED = 0.1f;

    private static final float STAND_FRICTION = 100f;
    private static final float WALK_FRICTION = 0.2f;

    private static final float WALK_SPEED = 1.5f;
    private static final float JUMP_SPEED = 3f;

    public PlayerSystem() {
        super(Family.all(PhysicsComponent.class,
                CharacterComponent.class,
                AnimatorComponent.class,
                SpriteComponent.class,
                PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Mappers.player.get(entity).stateMachine.update();
    }

    public enum PlayerState implements State<Entity> {
        IDLE() {
            @Override
            public void enter(Entity entity) {
                final AnimatorComponent animator = Mappers.animator.get(entity);
                animator.currentAnimation = PlayerAnimation.IDLE;
                animator.animationTime = 0;
            }

            @Override
            public void update(Entity entity) {
                PhysicsComponent physics = Mappers.physics.get(entity);
                CharacterComponent character = Mappers.character.get(entity);
                PlayerComponent player = Mappers.player.get(entity);

                Vector2 vel = physics.body.getLinearVelocity();
                Vector2 pos = physics.body.getPosition();

                handleGroundControl(physics, character, vel, pos);

                if (vel.len2() >= MIN_SQUARE_WALK_SPEED) {
                    if (character.footContactsCount > 0)
                        player.stateMachine.changeState(WALKING);
                    else
                        player.stateMachine.changeState(JUMPING);
                }
            }
        },
        WALKING() {
            @Override
            public void enter(Entity entity) {
                final AnimatorComponent animator = Mappers.animator.get(entity);

                animator.currentAnimation = PlayerAnimation.WALKING;
                animator.animationTime = 0;
            }

            @Override
            public void update(Entity entity) {
                PhysicsComponent physics = Mappers.physics.get(entity);
                CharacterComponent character = Mappers.character.get(entity);
                PlayerComponent player = Mappers.player.get(entity);

                Vector2 vel = physics.body.getLinearVelocity();
                Vector2 pos = physics.body.getPosition();

                handleGroundControl(physics, character, vel, pos);

                if (vel.len2() < MIN_SQUARE_WALK_SPEED)
                    player.stateMachine.changeState(IDLE);

                if (character.footContactsCount <= 0)
                    player.stateMachine.changeState(JUMPING);
            }
        },
        JUMPING() {
            @Override
            public void enter(Entity entity) {
                final AnimatorComponent animator = Mappers.animator.get(entity);

                animator.currentAnimation = PlayerAnimation.JUMPING;
                animator.animationTime = 0;

                for (int i = 0; i < Mappers.physics.get(entity).body.getFixtureList().size; ++i) {
                    Mappers.physics.get(entity).body.getFixtureList().get(i).setFriction(0f);
                }
            }

            @Override
            public void update(Entity entity) {
                PhysicsComponent physics = Mappers.physics.get(entity);
                CharacterComponent character = Mappers.character.get(entity);
                PlayerComponent player = Mappers.player.get(entity);

                Vector2 vel = physics.body.getLinearVelocity();
                Vector2 pos = physics.body.getPosition();

                handleAirControl(physics, character, vel, pos);

                if (character.footContactsCount > 0) {
                    if (vel.len2() < MIN_SQUARE_WALK_SPEED)
                        player.stateMachine.changeState(IDLE);
                    else
                        player.stateMachine.changeState(WALKING);
                }
            }

            private void handleAirControl(PhysicsComponent physics, CharacterComponent character, Vector2 vel, Vector2 pos) {
                if (Gdx.input.isKeyPressed(Input.Keys.A) && vel.x > -1.5f) {
                    character.viewDirection = Direction.LEFT;
                    float forceX = physics.body.getMass() * (-100f) / 60;
                    physics.body.applyForce(forceX, 0, pos.x, pos.y, true);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D) && vel.x < 1.5f) {
                    character.viewDirection = Direction.RIGHT;
                    float forceX = physics.body.getMass() * (100f) / 60;
                    physics.body.applyForce(forceX, 0, pos.x, pos.y, true);
                }
            }
        };

        private static void setPlayerFriction(PhysicsComponent physics, CharacterComponent character, float friction) {
            for (int i = 0; i < physics.body.getFixtureList().size; ++i) {
                physics.body.getFixtureList().get(i).setFriction(friction);
            }
            for (int i = 0; i < character.footContacts.size; ++i) {
                character.footContacts.get(i).resetFriction();
            }
        }

        private static void handleGroundControl(PhysicsComponent physics, CharacterComponent character, Vector2 vel, Vector2 pos) {
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D))
                setPlayerFriction(physics, character, WALK_FRICTION);
            else
                setPlayerFriction(physics, character, STAND_FRICTION);

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                character.viewDirection = Direction.LEFT;
                float impulseX = physics.body.getMass() * (-WALK_SPEED - vel.x);
                physics.body.applyLinearImpulse(impulseX, 0, pos.x, pos.y, true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                character.viewDirection = Direction.RIGHT;
                float impulseX = physics.body.getMass() * (WALK_SPEED - vel.x);
                physics.body.applyLinearImpulse(impulseX, 0, pos.x, pos.y, true);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                float impulseY = physics.body.getMass() * (JUMP_SPEED - vel.y);
                physics.body.applyLinearImpulse(0, impulseY, pos.x, pos.y, true);
            }
        }

        @Override
        public void exit(Entity entity) {
            // default implementation: do nothing
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }

    }
}
