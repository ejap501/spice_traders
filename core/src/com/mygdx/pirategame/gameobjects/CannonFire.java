package com.mygdx.pirategame.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.screen.GameScreen;

/**
 * Cannon Fire
 * Combat related cannon fire
 * Used by player and colleges,
 * Use should extend to enemy ships when implementing ship combat
 *
 *@author Ethan Alabaster
 *@version 1.0
 */
public class CannonFire extends Sprite {
    private World world;
    private Texture cannonBall;
    private float stateTime;
    private boolean destroyed;
    private boolean setToDestroy;
    private Body b2body;
    private double angle;
    private float velocity;
    private Vector2 bodyVel;
    private Sound fireNoise;
    private final Texture texture;
    private final Sprite sprite;
    private float sourceX, sourceY;
    private float x, y;
    private Vector2 mouse;

    /**
     * Instantiates cannon fire
     * Determines general cannonball data
     * Determines firing sound
     *
     * @param screen visual data
     * @param sourceX x value of origin
     * @param sourceY y value of origin
     * @param targetX x value of the target
     * @param targetY y value of the target
     * @param body body of origin
     * @param velocity velocity of the cannonball
     */
    public CannonFire(GameScreen screen, float targetX, float targetY, Vector2 position, Texture ship, Body body, OrthographicCamera camera, float velocity, Vector2 middleScaled) {
        this.velocity = velocity;
        this.world = screen.getWorld();
        //sets the angle and velocity
        bodyVel = body.getLinearVelocity();
        //angle = body.getAngle();

        //sourceX = body.getPosition().x;
        //sourceY = body.getPosition().y;

        mouse = PirateGame.getScaledMouseLocation(camera);
        targetX = mouse.x;
        targetY = mouse.y;

        sourceX = Gdx.graphics.getWidth() / 2;
        sourceY = Gdx.graphics.getHeight() / 2;

        x = body.getPosition().x;
        y = body.getPosition().y;

        System.out.println(sourceX);
        System.out.println(sourceY);
        System.out.println(targetX);
        System.out.println(targetY);
        //System.out.println(x);
        //System.out.println(y);

        // Uses a triangle to calculate the new trajectory
        angle = Math.atan2(targetY - sourceY, targetX - sourceX);

        //set cannonBall dimensions for the texture
        this.texture = new Texture("cannonBall.png");
        this.sprite = new Sprite(texture);

        setRegion(texture);
        setBounds(x, y, 10 / PirateGame.PPM, 10 / PirateGame.PPM);
        //set collision bounds
        defineCannonBall();
        //set sound for fire and play if on
        fireNoise = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        if (screen.game.getPreferences().isEffectsEnabled()) {
            fireNoise.play(screen.game.getPreferences().getEffectsVolume());
        }
        /*
        // sets center of sprite at source coordinates
        b2body.getPosition().x = sourceX - (getWidth() / 2);
        b2body.getPosition().y = sourceY - (getHeight() / 2);
         */
    }

    /**
     * Defines the existence, direction, shape and size of a cannonball
     */
    public void defineCannonBall() {
        //sets the body definitions
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bDef);

        //Sets collision boundaries
        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / PirateGame.PPM);

        // setting BIT identifier
        fDef.filter.categoryBits = PirateGame.CANNON_BIT;
        // determining what this BIT can collide with
        fDef.filter.maskBits = PirateGame.ENEMY_BIT | PirateGame.PLAYER_BIT | PirateGame.COLLEGE_BIT;
        fDef.shape = shape;
        fDef.isSensor = true;
        b2body.createFixture(fDef).setUserData(this);

        //Velocity maths
        //System.out.println(velocity);
        //System.out.println(angle);
        /*
        float velX = MathUtils.cos(angle) * velocity + bodyVel.x;
        float velY = MathUtils.sin(angle) * velocity + bodyVel.y;

         */

        //float velX = (float) (Math.cos(angle) * velocity + bodyVel.x);
        //float velY = (float) (Math.sin(angle) * velocity + bodyVel.y);
        float velX = (float) (Math.cos(angle) * velocity);
        float velY = (float) (Math.sin(angle) * velocity);
        System.out.println(Math.toDegrees(angle));
        /*
        System.out.println(velX);
        System.out.println(velY);

         */
        b2body.applyLinearImpulse(new Vector2(velX, velY), b2body.getWorldCenter(), true);
    }

    /**
     * Updates state with delta time
     * Defines range of cannon fire
     *
     * @param delta Delta time (elapsed time since last game tick)
     */
    public void update(float delta){
        stateTime += delta;
        //Update position of ball
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        //If ball is set to destroy and isnt, destroy it
        if((setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        // determines cannonball range
        if(stateTime > 0.98f) {
            setToDestroy();
        }

        /*
        //float velocity = 200 * delta;
        x += Math.cos(angle) * velocity;
        y += Math.sin(angle) * velocity;

        setPosition(x, y);

         */

        /*
        // destroy cannonball if it goes off the screen
        // limiting x
        if (b2body.getPosition().x <= -getWidth() / 2 || b2body.getPosition().x >= camera.viewportWidth - getWidth() / 2) {
            dispose();
        }

        // limiting y
        if (b2body.getPosition().y <= -getHeight() / 2 || b2body.getPosition().y >= camera.viewportHeight - height / 2) {
            dispose();
        }

         */

    }

    /**
     * Changes destruction state
     */
    public void setToDestroy(){
        setToDestroy = true;
    }

    /**
     * Returns destruction status
     */
    public boolean isDestroyed(){
        return destroyed;
    }
}
