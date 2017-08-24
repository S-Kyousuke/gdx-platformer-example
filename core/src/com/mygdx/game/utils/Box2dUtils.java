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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.components.TransformComponent;

public class Box2dUtils {

    private static PolygonShape polygonShape;
    private static CircleShape circleShape;
    private static ChainShape chainShape;

    private Box2dUtils() {
    }

    public static void init() {
        polygonShape = new PolygonShape();
        circleShape = new CircleShape();
    }

    public static BodyDef getBodyDef(BodyDef.BodyType bodyType, Vector2 position, float angle) {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = bodyType;
        groundBodyDef.position.set(position);
        groundBodyDef.angle = angle * MathUtils.degreesToRadians;
        return groundBodyDef;
    }

    public static BodyDef getBodyDef(BodyDef.BodyType bodyType, TransformComponent transform) {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = bodyType;
        groundBodyDef.position.set(transform.position);
        groundBodyDef.angle = transform.angle * MathUtils.degreesToRadians;
        return groundBodyDef;
    }

    public static void setChainShape(float[] vertices, boolean loop) {
        if (chainShape != null)
            chainShape.dispose();
        chainShape = new ChainShape();

        if (loop)
            chainShape.createLoop(vertices);
        else
            chainShape.createChain(vertices);
    }

    public static void setPolygonShape(float[] vertices) {
        polygonShape.set(vertices);
    }

    public static void setBoxShape(float halfWidth, float halfHeight, Vector2 position, float angle) {
        polygonShape.setAsBox(halfWidth, halfHeight, position, angle);
    }

    public static void setCircleShape(float radius, Vector2 position) {
        circleShape.setRadius(radius);
        circleShape.setPosition(position);
    }

    public static FixtureDef getCircleFixtureDef(float density, float friction, float restitution) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        return fixtureDef;
    }

    public static FixtureDef getBoxFixtureDef(float density, float friction, float restitution) {
        return getPolygonFixtureDef(density, friction, restitution);
    }

    public static FixtureDef getPolygonFixtureDef(float density, float friction, float restitution) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        return fixtureDef;
    }

    public static FixtureDef getChainFixtureDef(float density, float friction, float restitution) {
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = chainShape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        return fixtureDef;
    }

    public static void dispose() {
        polygonShape.dispose();
        circleShape.dispose();
        if (chainShape != null)
            chainShape.dispose();
    }
}
