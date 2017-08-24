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

package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraHelperComponent implements Component {

    public OrthographicCamera camera;
    public float zoom = 1f;
    public float speed = 0.1f;
    public Entity target;

    public float leftMost = -Float.MAX_VALUE;
    public float rightMost = Float.MAX_VALUE;
    public float topMost = Float.MAX_VALUE;
    public float bottomMost = -Float.MAX_VALUE;
}
