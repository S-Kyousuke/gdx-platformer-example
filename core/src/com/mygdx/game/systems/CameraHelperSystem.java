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
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.components.CameraHelperComponent;
import com.mygdx.game.components.PhysicsComponent;
import com.mygdx.game.components.TransformComponent;
import com.mygdx.game.utils.Mappers;

public class CameraHelperSystem extends IteratingSystem {

    private static final float MIN_ZOOM = 0.01f;
    private static final float MIN_SPEED = 0.01f;
    private static final float MAX_SPEED = 1.0f;

    public CameraHelperSystem() {
        super(Family.all(CameraHelperComponent.class).get(), 1);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraHelperComponent cameraHelper = Mappers.cameraHelper.get(entity);
        TransformComponent transform = Mappers.transform.get(entity);

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            cameraHelper.zoom += deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            cameraHelper.zoom -= deltaTime;
        }

        cameraHelper.speed = MathUtils.clamp(cameraHelper.speed, MIN_SPEED, MAX_SPEED);

        if (cameraHelper.target != null) {
            followTarget(cameraHelper, transform);
        }

        final float halfViewportWidth = cameraHelper.camera.viewportWidth * 0.5f;
        final float halfViewportHeight = cameraHelper.camera.viewportHeight * 0.5f;

        limitZoom(halfViewportWidth, halfViewportHeight, cameraHelper);
        keepCameraInBounds(halfViewportWidth * cameraHelper.zoom, halfViewportHeight * cameraHelper.zoom, cameraHelper, transform);
        applyToCamera(cameraHelper, transform);
    }

    private void followTarget(CameraHelperComponent cameraHelper, TransformComponent transform) {
        PhysicsComponent targetPhysics = Mappers.physics.get(cameraHelper.target);

        transform.position.x += (targetPhysics.body.getPosition().x - transform.position.x) * cameraHelper.speed;
        transform.position.y += (targetPhysics.body.getPosition().y - transform.position.y) * cameraHelper.speed;
    }

    private void limitZoom(float halfViewportWidth, float halfViewportHeight,
                           CameraHelperComponent cameraHelper) {
        final float maxHorizontalZoom = (cameraHelper.rightMost - cameraHelper.leftMost) / (halfViewportWidth * 2);
        final float maxVerticalZoom = (cameraHelper.topMost - cameraHelper.bottomMost) / (halfViewportHeight * 2);
        final float maxZoom = Math.min(maxHorizontalZoom, maxVerticalZoom);

        cameraHelper.zoom = MathUtils.clamp(cameraHelper.zoom, MIN_ZOOM, maxZoom);
    }

    private void keepCameraInBounds(float halfCameraWidth, float halfCameraHeight,
                                    CameraHelperComponent cameraHelper, TransformComponent transform) {

        if (transform.position.x > cameraHelper.rightMost - halfCameraWidth) {
            transform.position.x = cameraHelper.rightMost - halfCameraWidth;
        } else if (transform.position.x < cameraHelper.leftMost + halfCameraWidth)
            transform.position.x = cameraHelper.leftMost + halfCameraWidth;

        if (transform.position.y > cameraHelper.topMost - halfCameraHeight)
            transform.position.y = cameraHelper.topMost - halfCameraHeight;
        else if (transform.position.y < cameraHelper.bottomMost + halfCameraHeight)
            transform.position.y = cameraHelper.bottomMost + halfCameraHeight;
    }

    private void applyToCamera(CameraHelperComponent cameraHelper, TransformComponent transform) {
        cameraHelper.camera.position.x = transform.position.x;
        cameraHelper.camera.position.y = transform.position.y;
        cameraHelper.camera.zoom = cameraHelper.zoom;
        cameraHelper.camera.update();
    }
}
