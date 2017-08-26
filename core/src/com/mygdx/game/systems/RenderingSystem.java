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
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.components.TextureComponent;
import com.mygdx.game.components.TransformComponent;
import com.mygdx.game.utils.Mappers;

import java.util.Comparator;


public class RenderingSystem extends SortedIteratingSystem {

    public static final float PIXEL_PER_UNIT = 100.0f;

    private SpriteBatch batch;
    private Camera camera;

    private Array<Entity> renderQueue = new Array<Entity>();

    public RenderingSystem(SpriteBatch batch, Camera camera) {
        super(Family.all(TextureComponent.class, TransformComponent.class).get(), new LayerComparator(), 3);

        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < renderQueue.size; ++i) {
            Entity entity = renderQueue.get(i);

            TransformComponent transform = Mappers.transform.get(entity);
            TextureComponent sprite = Mappers.sprite.get(entity);

            final float width = sprite.region.getRegionWidth();
            final float height = sprite.region.getRegionHeight();

            final float originX = width / 2;
            final float originY = height / 2;

            if (sprite.region.isFlipX() != sprite.flipX) {
                sprite.region.flip(true, false);
            }
            if (sprite.region.isFlipY() != sprite.flipY) {
                sprite.region.flip(false, true);
            }

            batch.draw(sprite.region, transform.position.x - originX, transform.position.y - originY,
                    originX, originY,
                    width, height,
                    transform.scale.x / PIXEL_PER_UNIT, transform.scale.y / PIXEL_PER_UNIT, transform.angle);
        }
        batch.end();
        renderQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    private static class LayerComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity e1, Entity e2) {
            return Mappers.sprite.get(e1).layer - Mappers.sprite.get(e2).layer;
        }
    }
}
