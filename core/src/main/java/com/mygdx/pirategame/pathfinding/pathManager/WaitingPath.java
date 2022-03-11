package com.mygdx.pirategame.pathfinding.pathManager;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.screen.GameScreen;

/**
 * Superclass used for all pathing managers which are in a passive state waiting for a ship to come into range to attack
 * This class will switch to an attacking pathing manager if within range of an enemy
 */
public abstract class WaitingPath implements PathManager {

    protected final EnemyShip ship;
    protected final GameScreen screen;

    /**
     * Used for all passive pathing managers that can start an attack
     * @param ship The ship that is being pathed
     * @param screen The gameScreen controlling the level
     */
    public WaitingPath(EnemyShip ship, GameScreen screen) {
        this.ship = ship;
        this.screen = screen;
    }

    @Override
    public void update(float dt) {
        // if the ship is in range of the player
        if ((ship.collegeMeta == null || !ship.collegeMeta.isPlayer()) && ship.b2body.getPosition().dst(screen.getPlayerPos()) < 3) {
            ship.setPathManager(new AttackPath(this, ship, screen));
        }

    }

}
