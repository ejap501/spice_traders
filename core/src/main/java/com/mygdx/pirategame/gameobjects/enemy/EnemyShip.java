package com.mygdx.pirategame.gameobjects.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.pirategame.DebugUtils;
import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.pathfinding.Checkpoint;
import com.mygdx.pirategame.pathfinding.PathFinder;
import com.mygdx.pirategame.screen.GameScreen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Enemy Ship
 * Generates enemy ship data
 * Instantiates an enemy ship
 *
 * @author Ethan Alabaster, Sam Pearson, Edward Poulter
 * @version 1.0
 */
public class EnemyShip extends Enemy {
    private Texture enemyShip;
    public Integer collegeID;
    private final Sound destroy;
    private final Sound hit;

    /**
     * used to delay patfinding when the ship collides with something
     */
    private int updateDelay = 0;

    private List<Checkpoint> path;

    /**
     * Instantiates enemy ship
     *
     * @param screen     Visual data
     * @param x          x coordinates of entity
     * @param y          y coordinates of entity
     * @param path       path of texture file
     * @param assignment College ship is assigned to
     */
    public EnemyShip(GameScreen screen, float x, float y, String path, Integer assignment) {
        super(screen, x, y);
        enemyShip = new Texture(path);
        //Assign college
        collegeID = assignment;
        //Set audios
        destroy = Gdx.audio.newSound(Gdx.files.internal("sfx_and_music/ship-explosion-2.wav"));
        hit = Gdx.audio.newSound(Gdx.files.internal("sfx_and_music/ship-hit.wav"));
        //Set the position and size of the college
        setBounds(0, 0, 64 / PirateGame.PPM, 110 / PirateGame.PPM);
        setRegion(enemyShip);
        setOrigin(32 / PirateGame.PPM, 55 / PirateGame.PPM);

        damage = 20;
        // give a seconds speed instead of a portin of it, then limit to a portion of the speed
        generateNewPath();
    }

    /**
     * Used to generate a new path from the current location to a random point on the map
     */
    public void generateNewPath() {

        Vector2 destination = generateDestination();
        int tilewidth = screen.getTileWidth();
        path = screen.getPathFinder().getPath((b2body.getPosition().x * PirateGame.PPM), (b2body.getPosition().y * PirateGame.PPM), destination.x, destination.y, getWidth() * tilewidth, getHeight() * tilewidth);
        if (path != null && path.size() > 1) {
            // removing the start node from the path as ship is already at it
            path.remove(0);

        }

    }

    /**
     * @return a random point on the map that the boat can travel too
     */
    private Vector2 generateDestination() {
        Random rnd = new Random();

        int tileWidth = (int)PirateGame.PPM;

        while (true) {
            int x = rnd.nextInt(2000) - 1000 + (int) (b2body.getPosition().x * tileWidth);
            int y = rnd.nextInt(2000) - 1000 + (int) (b2body.getPosition().y * tileWidth);
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
            if (screen.getPathFinder().isTraversable(x, y, getWidth() * tileWidth, getHeight() * tileWidth)) {
                return new Vector2(x, y);
            }
        }

    }

    /**
     * Updates the state of each object with delta time
     * Checks for ship destruction
     *
     * @param dt Delta time (elapsed time since last game tick)
     */
    public void update(float dt) {
        //If ship is set to destroy and isnt, destroy it
        if (destroyed) {
            return;
        }


        if (setToDestroy) {
            //Play death noise
            if (GameScreen.game.getPreferences().isEffectsEnabled()) {
                destroy.play(GameScreen.game.getPreferences().getEffectsVolume());
            }
            world.destroyBody(b2body);
            destroyed = true;
            //Change player coins and points
            Hud.changePoints(20);
            Hud.changeCoins(10);
        }

        //Update position and angle of ship
        setPosition(b2body.getPosition().x - getWidth() / 2f, b2body.getPosition().y - getHeight() / 2f);
        float angle = (float) Math.atan2(b2body.getLinearVelocity().y, b2body.getLinearVelocity().x);
        b2body.setTransform(b2body.getWorldCenter(), angle - ((float) Math.PI) / 2.0f);
        setRotation((float) (b2body.getAngle() * 180 / Math.PI));
        //Update health bar
        bar.update();

        if (health <= 0) {
            setToDestroy = true;
        }

        if (updateDelay > 0) {
            updateDelay--;
            return;
        }

        if (path == null || path.isEmpty()) {
            generateNewPath();
            return;
        }

        Checkpoint cp = path.get(0);

        if (cp.getVector2().dst(b2body.getPosition().scl(PirateGame.PPM)) < PirateGame.PPM / 2) {
            path.remove(cp);
            if (path.isEmpty()) {
                generateNewPath();
            }
        }

        final float speed = 100f * dt;
        Vector2 v = travelToCheckpoint(speed, cp);
        b2body.setLinearVelocity(v);

        // below code is to move the ship to a coordinate (target)
        //Vector2 target = new Vector2(960 / PirateGame.PPM, 2432 / PirateGame.PPM);
        //target.sub(b2body.getPosition());
        //target.nor();
        //float speed = 1.5f;
        //b2body.setLinearVelocity(target.scl(speed));
    }

    /**
     * Used
     *
     * @param maxDistance The max distance that can be travelled
     * @param cp          The checkpoint to travel towards
     * @return The vector that needs to be applied to travel towards the checkpoint
     */
    private Vector2 travelToCheckpoint(float maxDistance, Checkpoint cp) {
        Vector2 v = new Vector2(cp.x - (b2body.getPosition().x * PirateGame.PPM) - getWidth() / 2, cp.y - (b2body.getPosition().y * PirateGame.PPM) - getHeight() / 2);

        return v.limit(maxDistance);
    }

    /**
     * Constructs the ship batch
     *
     * @param batch The batch of visual data of the ship
     */
    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
            //Render health bar
            bar.render(batch);
            // checking if pathfinding information should be displayed
            if (PathFinder.PATHFINDERDEBUG && path != null && !path.isEmpty()) {
                batch.end();
                for (int i = 0; i < path.size() - 1; i++) {

                    DebugUtils.drawDebugLine(path.get(i).getVector2().scl(1 / PirateGame.PPM), path.get(i + 1).getVector2().scl(1 / PirateGame.PPM), batch.getProjectionMatrix());
                    DebugUtils.drawDebugDot(path.get(i).getVector2().scl(1 / PirateGame.PPM), batch.getProjectionMatrix());
                }
                batch.begin();
            }
        }
    }

    /**
     * Defines the ship as an enemy
     * Sets data to act as an enemy
     */
    @Override
    protected void defineEnemy() {
        //sets the body definitions
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        //b2body.setLinearDamping(1);
        //Sets collision boundaries
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(55 / PirateGame.PPM);
        // setting BIT identifier
        fdef.filter.categoryBits = PirateGame.ENEMY_BIT;
        // determining what this BIT can collide with
        fdef.filter.maskBits = PirateGame.DEFAULT_BIT | PirateGame.PLAYER_BIT | PirateGame.ENEMY_BIT | PirateGame.CANNON_BIT;
        fdef.shape = shape;
        fdef.restitution = 0.7f;
        b2body.createFixture(fdef).setUserData(this);
    }

    /**
     * Checks contact
     * Changes health in accordance with contact and damage
     */
    @Override
    public void onContact() {
        updateDelay = 50;
        Gdx.app.log("enemy", "collision");
        //Play collision sound
        if (GameScreen.game.getPreferences().isEffectsEnabled()) {
            hit.play(GameScreen.game.getPreferences().getEffectsVolume());
        }
        //Deal with the damage
        health -= damage;
        bar.changeHealth(damage);
        Hud.changePoints(5);
    }

    /**
     * Updates the ship image. Particuarly change texture on college destruction
     *
     * @param alignment Associated college
     * @param path      Path of new texture
     */
    public void updateTexture(Integer alignment, String path) {
        collegeID = alignment;
        enemyShip = new Texture(path);
        setRegion(enemyShip);
    }
}
