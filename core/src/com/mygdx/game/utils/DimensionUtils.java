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

import com.mygdx.game.components.SpriteComponent;
import com.mygdx.game.components.TransformComponent;
import com.mygdx.game.systems.RenderingSystem;

public class DimensionUtils {

    private DimensionUtils() {
    }

    public static float pixelToWorld(float pixel) {
        return pixel / RenderingSystem.PIXEL_PER_UNIT;
    }

    public static float worldToPixel(float world) {
        return world * RenderingSystem.PIXEL_PER_UNIT;
    }

    public static float getSpriteWorldWidth(TransformComponent transform, SpriteComponent sprite) {
        return pixelToWorld(sprite.region.getRegionWidth()) * transform.scale.x ;
    }

    public static float getSpriteWorldHeight(TransformComponent transform, SpriteComponent sprite) {
        return pixelToWorld(sprite.region.getRegionHeight()) * transform.scale.y ;
    }

    public static float getScale(float pixel, float desiredWorld) {
        return worldToPixel(desiredWorld) / pixel;
    }
}
