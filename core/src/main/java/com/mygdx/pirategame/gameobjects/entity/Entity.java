package com.mygdx.pirategame.gameobjects.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.pirategame.screen.GameScreen;

/**
 * Entity
 * Defines an entity
 * Instantiates an entity
 *
 *@author Ethan Alabaster
 *@version 1.0
 */
public abstract class Entity extends Sprite {
    protected World world;
    protected GameScreen screen;
    public Body b2body;
    public boolean setToDestroyed = false;
    public boolean destroyed = false;

    /**
     * Instantiates an entity
     * Sets position in world
     *
     * @param screen Visual data
     * @param x x position of entity
     * @param y y position of entity
     */
    public Entity(GameScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEntity();
    }

    /**
     * Defines the properties of an entity
     */
    protected abstract void defineEntity();

    /**
     * Defines contact with other objects, The only entity that is able to do so is the player ship
     */
    public abstract void entityContact();
}
