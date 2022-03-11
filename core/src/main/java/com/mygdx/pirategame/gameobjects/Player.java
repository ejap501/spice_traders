package com.mygdx.pirategame.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.screen.GameScreen;

/**
 * Creates the class of the player. Everything that involves actions coming from the player boat
 * @author Ethan Alabaster, Edward Poulter
 * @version 1.0
 */
public class Player extends Sprite {
    private final GameScreen screen;
    private Texture ship;
    public World world;
    public Body b2body;
    private Sound breakSound, cannonballHitSound;
    private Array<CannonFire> cannonBalls;
    private float timeFired = 0;

    /**
     * Instantiates a new Player. Constructor only called once per game
     *
     * @param screen visual data
     */
    public Player(GameScreen screen) {
        // Retrieves world data and creates ship texture
        this.screen = screen;
        ship = new Texture("college/Ships/player_ship.png");
        this.world = screen.getWorld();

        // Defines a player, and the players position on screen and world
        definePlayer();
        setBounds(0,0,64 / PirateGame.PPM, 110 / PirateGame.PPM);
        setRegion(ship);
        setOrigin(32 / PirateGame.PPM,55 / PirateGame.PPM);

        // Sound effect for terrain collision
        breakSound = Gdx.audio.newSound(Gdx.files.internal("sfx_and_music/wood-bump.mp3"));
        // Sound effect for cannonball hit
        cannonballHitSound = Gdx.audio.newSound(Gdx.files.internal("sfx_and_music/ship-hit.wav"));


        // Sets cannonball array
        cannonBalls = new Array<>();
    }

    /**
     * Update the position of the player. Also updates any cannonballs the player generates
     *
     * @param dt Delta Time
     */
    public void update(float dt) {
        // Updates position and orientation of player
        setPosition(b2body.getPosition().x - getWidth() / 2f, b2body.getPosition().y - getHeight() / 2f);
        float angle = (float) Math.atan2(b2body.getLinearVelocity().y, b2body.getLinearVelocity().x);
        b2body.setTransform(b2body.getWorldCenter(), angle - ((float)Math.PI) / 2.0f);
        setRotation((float) (b2body.getAngle() * 180 / Math.PI));

        // Updates cannonball data
        for(CannonFire ball : cannonBalls) {
            ball.update(dt);
            if(ball.isDestroyed())
                cannonBalls.removeValue(ball, true);
        }

        // Add delay timer between shots
        timeFired += dt;
    }

    /**
     * Plays the break sound when a boat takes damage
     */
    public void playBreakSound() {
        // Plays terrain collision sound effect
        if (screen.game.getPreferences().isEffectsEnabled()) {
            breakSound.play(screen.game.getPreferences().getEffectsVolume());
        }
    }

    /**
     * Plays the explosion sound when a boat is hit by a cannonball
     */
    public void playCannonballHitSound() {
        // Plays cannonball hit sound effect
        if (screen.game.getPreferences().isEffectsEnabled()) {
            cannonballHitSound.play(screen.game.getPreferences().getEffectsVolume());
        }
    }

    /**
     * Defines all the parts of the player's physical model. Sets it up for collisons
     */
    private void definePlayer() {
        // Defines a players position
        BodyDef bdef = new BodyDef();
        bdef.position.set(1200  / PirateGame.PPM, 2500 / PirateGame.PPM); // Default Pos: 1800,2500
        bdef.type = BodyDef.BodyType.DynamicBody;
        // linear damping slows the player if no movement key is pressed
        bdef.linearDamping = 1f;
        b2body = world.createBody(bdef);

        // Defines a player's shape and contact borders
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(55 / PirateGame.PPM);

        // setting BIT identifier
        fdef.filter.categoryBits = PirateGame.PLAYER_BIT;

        // determining what this BIT can collide with
        fdef.filter.maskBits = PirateGame.DEFAULT_BIT | PirateGame.COIN_BIT
                | PirateGame.COIN_MAGNET_BIT | PirateGame.SPEED_BOOST_BIT
                | PirateGame.ABSORPTION_HEART_BIT | PirateGame.FASTER_SHOOTING_BIT
                | PirateGame.FREEZE_ENEMY_BIT | PirateGame.ENEMY_BIT
                | PirateGame.COLLEGE_BIT | PirateGame.COLLEGE_SENSOR_BIT
                | PirateGame.COLLEGE_FIRE_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    /**
     * Called when left button is pressed.
     * One cannonball is launched towards the mouse position from the centre of the player.
     *
     * @param camera The camera that is in charge of scaling
     */
    public void fire(OrthographicCamera camera) {
        // Fires cannon if specified delay time has passed
        if (timeFired > GameScreen.getShootingDelay()) {
            cannonBalls.add(new CannonFire(screen, b2body, camera, 5));
            timeFired = 0;
        }
    }

    /**
     * Draws the player using batch
     * Draws cannonballs using batch
     *
     * @param batch The batch of the program
     */
    public void draw(Batch batch){
        // Draws player and cannonballs
        super.draw(batch);
        for(CannonFire ball : cannonBalls)
            ball.draw(batch);
    }
}
