package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.components.TextureAnimatorComponent;

public class TextureAnimatorBuilder {

    private ObjectMap<Enum, Animation<TextureRegion>> animations = new ObjectMap<Enum, Animation<TextureRegion>>();

    private TextureAtlas atlas;

    public TextureAnimatorBuilder(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public void addLoopAnimation(Enum animationName,  String regionsName, float frameTime) {
        addAnimation(animationName, regionsName, frameTime, Animation.PlayMode.LOOP);
    }

    public void addAnimation(Enum animationName,  String regionsName, float frameTime) {
        addAnimation(animationName, regionsName, frameTime, Animation.PlayMode.NORMAL);
    }


    public void addAnimation(Enum animationName,  String regionsName, float frameTime, Animation.PlayMode mode) {
        animations.put(animationName, new Animation<TextureRegion>(frameTime, atlas.findRegions(regionsName), mode));
    }

    public TextureAnimatorComponent getAnimator() {
        TextureAnimatorComponent animator = new TextureAnimatorComponent();
        animator.animations = animations;
        return animator;
    }



}
