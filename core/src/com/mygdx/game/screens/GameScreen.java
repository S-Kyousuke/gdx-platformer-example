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
import com.badlogic.gdx.Game;
import com.mygdx.game.systems.RenderingSystem;
import com.mygdx.game.utils.Assets;
import com.mygdx.game.utils.DimensionUtils;
import com.mygdx.game.utils.EntityBuilder;
import com.mygdx.game.utils.TiledMapUtils;

public class GameScreen extends AbstractGameScreen {

    private static final int RESOLUTION_WIDTH = 1024;
    private static final int RESOLUTION_HEIGHT = 576;

    private static final float SCENE_WIDTH = RESOLUTION_WIDTH / RenderingSystem.PIXEL_PER_UNIT;
    private static final float SCENE_HEIGHT = RESOLUTION_HEIGHT / RenderingSystem.PIXEL_PER_UNIT;

    public GameScreen(Game game) {
        super(game, SCENE_WIDTH, SCENE_HEIGHT);

        final int tilePixelWidth = 128;
        final float tileWorldWidth = 0.5f;
        final float mapScale = DimensionUtils.getScale(tilePixelWidth, tileWorldWidth);
        Entity map = EntityBuilder.getMap(getWorld(), Assets.instance.testMap, mapScale, getBatch());
        Entity player = EntityBuilder.getPlayer(getWorld(), 0.6f, 1.725f);
        Entity cameraHelper = EntityBuilder.getCameraHelper(getCamera(), player);

        TiledMapUtils.setCameraHelperBounds(cameraHelper, map);

        getEngine().addEntity(player);
        getEngine().addEntity(cameraHelper);
        getEngine().addEntity(map);
    }

}
