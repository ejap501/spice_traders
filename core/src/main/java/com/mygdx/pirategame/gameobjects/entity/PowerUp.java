package com.mygdx.pirategame.gameobjects.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.pirategame.screen.GameScreen;

public abstract class PowerUp extends Entity {
    private Sound pickupSound;

    /**
     * x
     * Instantiates an entity
     * Sets position in world
     *
     * @param screen Visual data
     * @param x      x position of entity
     * @param y      y position of entity
     */
    public PowerUp(GameScreen screen, float x, float y) {
        super(screen, x, y);
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEntity();

        // Sets pickup sound
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("sfx_and_music/coin-pickup.mp3"));
    }

    public abstract void update();

    public Sound getSound() {
        return pickupSound;
    }


}

