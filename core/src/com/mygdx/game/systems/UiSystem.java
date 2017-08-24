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

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.utils.Assets;

public class UiSystem extends EntitySystem {

    private Stage stage;
    private Label fpsLabel;

    public UiSystem(Stage stage) {
        priority = 5;
        this.stage = stage;

        fpsLabel = new Label("", Assets.instance.skin);
        fpsLabel.pack();

        stage.addActor(fpsLabel);
    }

    @Override
    public void update(float deltaTime) {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

        stage.act(deltaTime);
        stage.draw();
    }
}
