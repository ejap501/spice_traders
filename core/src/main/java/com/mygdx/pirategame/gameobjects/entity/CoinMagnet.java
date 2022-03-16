package com.mygdx.pirategame.gameobjects.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.screen.GameScreen;

public class CoinMagnet extends PowerUp {
    private Texture coinMagnet;
    private boolean toggleCoinMagnet = false;

    /**
     * x
     * Instantiates an entity
     * Sets position in world
     *
     * @param screen Visual data
     * @param x      x position of entity
     * @param y      y position of entity
     */
    public CoinMagnet(GameScreen screen, float x, float y) {
        super(screen, x, y);

        // Set speed boost image
        coinMagnet = new Texture("cannonBall.png"); // CHANGE
        //Set the position and size of the speed boost
        setBounds(0,0,48 / PirateGame.PPM, 48 / PirateGame.PPM);
        //Set the texture
        setRegion(coinMagnet);
        //Sets origin of the speed boost
        setOrigin(24 / PirateGame.PPM,24 / PirateGame.PPM);

        // set duration of power up
        duration = 10;
    }

    /**
     * Defines the properties of an entity
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
        fdef.filter.categoryBits = PirateGame.COIN_MAGNET_BIT;

        // Determining what this BIT can collide with
        fdef.filter.maskBits = PirateGame.DEFAULT_BIT | PirateGame.PLAYER_BIT | PirateGame.ENEMY_BIT;
        fdef.shape = shape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(this);
    }

    /**
     * Defines contact with other objects, The only entity that is able to do so is the player ship
     */
    @Override
    public void entityContact() {
        if (!destroyed) {
            toggleCoinMagnet = true;
            active = true;

            // Set to destroy
            setToDestroyed = true;
            Gdx.app.log("coinmagnet", "collision");
            // Play pickup sound
            if (screen.game.getPreferences().isEffectsEnabled()) {
                getSound().play(screen.game.getPreferences().getEffectsVolume());
            }
        }
    }

    @Override
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
        if (toggleCoinMagnet) {
            toggleCoinMagnet();
            toggleCoinMagnet = false;
        }
        // ability lasts for a specified duration
        if (timer > duration) {
            endPowerUp();
            timer = 0;
        }
        else if (active) {
            timer += Gdx.graphics.getDeltaTime();
        }
    }

    /**
     * Enables/disables the coin magnet power up
     */
    private void toggleCoinMagnet() {
        // collect coins in a larger radius around player
        for (Coin coin : screen.getCoins()) coin.toggleCoinMagnet();
    }

    @Override
    public void endPowerUp() {
        toggleCoinMagnet();
        active = false;
        Gdx.app.log("coinmagnet", "ended");
    }
}
