package com.mygdx.pirategame.pathfinding.pathManager;

import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.gameobjects.enemy.Enemy;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.gameobjects.enemy.SeaMonster;
import com.mygdx.pirategame.screen.GameScreen;

import java.util.Random;

/**
 * Generates random paths for ships and randomly paths between them
 * Used for ships without an assigned college
 */
public class RandomPath extends WaitingPath {


    public RandomPath (EnemyShip ship, GameScreen screen) {
        super(ship, screen);
    }

    public RandomPath (SeaMonster ship, GameScreen screen) {
        super(ship, screen);
    }

    @Override
    public Vector2 generateDestination() {
        Random rnd = new Random();

        int tileWidth = (int) PirateGame.PPM;

        while (true) {
            // generate random position for ship to go
            int x;
            int y;
            if (ship != null) {
                x = rnd.nextInt(2000) - 1000 + (int) (ship.b2body.getPosition().x * tileWidth);
                y = rnd.nextInt(2000) - 1000 + (int) (ship.b2body.getPosition().y * tileWidth);
            }
            else {
                x = rnd.nextInt(2000) - 1000 + (int) (seaMonster.b2body.getPosition().x * tileWidth);
                y = rnd.nextInt(2000) - 1000 + (int) (seaMonster.b2body.getPosition().y * tileWidth);
            }

            // bounding the location
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x > screen.getTileMapWidth()) {
                x = screen.getTileMapWidth();
            }
            if (y > screen.getTileMapHeight()) {
                y = screen.getTileMapHeight();
            }

            // checking if the location is valid
            if (ship != null) {
                if (ship.isTraversable(x, y)) {
                    // going to that location
                    return new Vector2(x, y);
                }
            }
            else {
                if (seaMonster.isTraversable(x, y)) {
                    // going to that location
                    return new Vector2(x, y);
                }
            }

        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        // nothing to do here
    }
}
