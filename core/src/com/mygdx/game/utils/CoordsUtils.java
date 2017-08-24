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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class CoordsUtils {

    private CoordsUtils() {
    }

    private static Vector3 tempCoords = new Vector3();
    private static Vector2 stageCoords = new Vector2();

    public static Vector2 worldToStageCoords(float worldX, float worldY, Camera worldCamera, Stage stage) {
        tempCoords = worldCamera.project(tempCoords.set(worldX, worldY, 0));
        tempCoords = stage.getCamera().unproject(tempCoords.set(tempCoords.x, tempCoords.y, 0));
        tempCoords.y = stage.getCamera().viewportHeight - tempCoords.y;
        return stageCoords.set(tempCoords.x, tempCoords.y) ;
    }
}
