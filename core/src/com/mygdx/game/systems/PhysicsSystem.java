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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.components.PhysicsComponent;
import com.mygdx.game.components.TransformComponent;
import com.mygdx.game.utils.Mappers;

public class PhysicsSystem extends IteratingSystem {

    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final float STEP_TIME = 1 / 60f;
    private static final float MAX_FRAME_TIME = 0.25f;

    private World world;
    private float accumulator = 0f;

    public PhysicsSystem(World world) {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get());
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        final float frameTime = Math.min(deltaTime, MAX_FRAME_TIME);
        accumulator += deltaTime;
        while (accumulator >= STEP_TIME) {
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= frameTime;
            super.update(deltaTime);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = Mappers.physics.get(entity);
        if (physics.body.isAwake()) {
            TransformComponent transform = Mappers.transform.get(entity);
            transform.position.set(physics.body.getPosition());
            transform.angle = physics.body.getAngle() * MathUtils.radiansToDegrees;
        }
    }

}