package com.mygdx.pirategame.gameobjects.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.screen.GameScreen;

public class Shop extends Sprite {
    private World world;
    private Body b2body;
    private Vector2 playerPos;


    public Shop(GameScreen screen, float x, float y) {
        this.world = screen.getWorld();
        playerPos = screen.getCenteredPlayerPos();

    }



}
