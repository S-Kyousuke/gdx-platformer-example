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

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.AnimatorComponent;

import java.util.Comparator;

public class AnimatorUtils {

    private AnimatorUtils() {
    }

    private static TextureAtlas atlas;

    public static void setAtlas(TextureAtlas atlas) {
        AnimatorUtils.atlas = atlas;
    }

    public static void addLoopTo(AnimatorComponent animator, Enum animationName, float frameTime, String regionsName) {
        addTo(animator, animationName, frameTime, Animation.PlayMode.LOOP, regionsName);
    }

    public static void addNormalTo(AnimatorComponent animator, Enum animationName, float frameTime, String regionsName) {
        addTo(animator, animationName, frameTime, Animation.PlayMode.NORMAL, regionsName);
    }

    public static void addTo(AnimatorComponent animator, Enum animationName, float frameTime, Animation.PlayMode mode, String regionsName) {
        animator.animations.put(animationName, new Animation<TextureRegion>(frameTime, atlas.findRegions(regionsName), mode));
    }

    public static boolean isFinished(AnimatorComponent animator, Enum animationName) {
        return animator.animations.get(animationName).isAnimationFinished(animator.animationTime);
    }

    private static class RegionNameComparator implements Comparator<TextureAtlas.AtlasRegion> {

        @Override
        public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
            return region1.name.compareTo(region2.name);
        }
    }
}
