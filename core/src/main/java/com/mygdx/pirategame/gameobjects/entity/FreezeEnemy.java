package com.mygdx.pirategame.gameobjects.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.gameobjects.enemy.Enemy;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.screen.GameScreen;

/**
 * Heals the player when picked up
 */
public class FreezeEnemy extends PowerUp {
    private Texture freezeEnemy;
    private float timer = 0;
    private float duration;
    private float timeLeft;

    /**
     * x
     * Instantiates an entity
     * Sets position in world
     *
     * @param screen Visual data
     * @param x      x position of entity
     * @param y      y position of entity
     */
    public FreezeEnemy(GameScreen screen, float x, float y) {
        super(screen, x, y);

        // Set speed boost image
        freezeEnemy = new Texture("entity/ice.png");
        //Set the position and size of the speed boost
        setBounds(0,0,48 / PirateGame.PPM, 48 / PirateGame.PPM);
        //Set the texture
        setRegion(freezeEnemy);
        //Sets origin of the speed boost
        setOrigin(24 / PirateGame.PPM,24 / PirateGame.PPM);

        // Set duration of power up
        duration = 10;
    }

    /**
     * Updates the absorption heart state. If needed, deletes the absorption heart if picked up
     */
    public void update() {
        //If coin is set to destroy and isnt, destroy it
        if(setToDestroyed && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        //Update position of power up
        else if(!destroyed) {
            setPosition(b2body.getPosition().x - getWidth() / 2f, b2body.getPosition().y - getHeight() / 2f);
        }
        // Ability lasts for a specified duration
        if (timer > duration) {
            endPowerUp();
            timer = 0;
            timeLeft = 0;
        }
        else if (active) {
            timer += Gdx.graphics.getDeltaTime();
            timeLeft -= Gdx.graphics.getDeltaTime();
            Hud.setFreezeEnemyTimer(timeLeft);
            System.out.println(timeLeft);
        }
    }

    @Override
    public void endPowerUp() {
        // Start enemy movement
        EnemyShip.movement = true;
        EnemyShip.fire = false;
    }

    /**
     * Defines all the parts of the speed boost physical model. Sets it up for collisions
     */
    @Override
    protected void defineEntity() {
        // Sets the body definitions
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Sets collision boundaries
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(24 / PirateGame.PPM);

        // Setting BIT identifier
        fdef.filter.categoryBits = PirateGame.ABSORPTION_HEART_BIT;

        // Determining what this BIT can collide with
        fdef.filter.maskBits = PirateGame.DEFAULT_BIT | PirateGame.PLAYER_BIT | PirateGame.ENEMY_BIT;
        fdef.shape = shape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void entityContact() {
        if (!destroyed) {
            active = true;
            timeLeft += (duration / 2);
            // Stop enemy movement
            EnemyShip.movement = false;
            EnemyShip.fire = false;

            // Set to destroy
            setToDestroyed = true;
            Gdx.app.log("freezeEnemy", "collision");
            // Play pickup sound
            if (screen.game.getPreferences().isEffectsEnabled()) {
                getSound().play(screen.game.getPreferences().getEffectsVolume());
            }
        }
    }
}
