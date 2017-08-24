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

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.components.CameraHelperComponent;
import com.mygdx.game.components.PhysicsComponent;
import com.mygdx.game.components.TiledMapComponent;

public class TiledMapUtils {

    private TiledMapUtils() {
    }

    public static void setCameraHelperBounds(Entity cameraHelper, Entity tileMap) {
        CameraHelperComponent cameraHelperComp = Mappers.cameraHelper.get(cameraHelper);
        TiledMapComponent tileMapComp = Mappers.tiledMap.get(tileMap);

        final float rendererScale = tileMapComp.renderer.getUnitScale();
        final MapProperties mapProperties = tileMapComp.renderer.getMap().getProperties();

        int mapWidth = mapProperties.get("width", Integer.class);
        int mapHeight = mapProperties.get("height", Integer.class);
        int tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        int tilePixelHeight = mapProperties.get("tileheight", Integer.class);

        cameraHelperComp.leftMost = 0;
        cameraHelperComp.rightMost = mapWidth * tilePixelWidth * rendererScale;
        cameraHelperComp.bottomMost = 0;
        cameraHelperComp.topMost = mapHeight * tilePixelHeight * rendererScale;
    }

    public static PhysicsComponent generateMapPhysics(World world, TiledMapComponent tiledMapComp) {

        final float rendererScale = tiledMapComp.renderer.getUnitScale();
        final TiledMap tiledMap = tiledMapComp.renderer.getMap();

        PhysicsComponent physics = new PhysicsComponent();
        BodyDef bodyDef = Box2dUtils.getBodyDef(BodyDef.BodyType.StaticBody, Vector2.Zero, 0);
        physics.body = world.createBody(bodyDef);

        MapLayer physicsLayer = tiledMap.getLayers().get("physics");

        Array<PolylineMapObject> polylineObjects = physicsLayer.getObjects().getByType(PolylineMapObject.class);
        Array<PolygonMapObject> polygonObjects = physicsLayer.getObjects().getByType(PolygonMapObject.class);

        for (int i = 0; i< polylineObjects.size; ++i) {
            Polyline polyline = polylineObjects.get(i).getPolyline();
            polyline.setPosition(polyline.getX() * rendererScale, polyline.getY() * rendererScale);
            polyline.setScale(rendererScale, rendererScale);

            float[] vertices = polyline.getTransformedVertices();
            boolean looping = (vertices[0] == vertices[vertices.length - 2])
                    && (vertices[1] == vertices[vertices.length - 1]);

            if (looping) {
                float[] fixedVertices = new float[vertices.length - 2];
                System.arraycopy(vertices, 0, fixedVertices, 0, fixedVertices.length);
                vertices = fixedVertices;
            }

            Box2dUtils.setChainShape(vertices, looping);
            physics.body.createFixture(Box2dUtils.getChainFixtureDef(0, 0.2f, 0f)).setUserData("map physics");
        }

        for (int i = 0; i< polygonObjects.size; ++i) {
            Polygon polygon = polygonObjects.get(i).getPolygon();
            polygon.setPosition(polygon.getX() * rendererScale, polygon.getY() * rendererScale);
            polygon.setScale(rendererScale, rendererScale);

            Box2dUtils.setPolygonShape(polygon.getTransformedVertices());
            physics.body.createFixture(Box2dUtils.getPolygonFixtureDef(0, 0.2f, 0f)).setUserData("map physics");
        }

        return physics;
    }
}
