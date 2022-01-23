package com.mygdx.pirategame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (fixA.getUserData() == "sensor" || fixB.getUserData() == "sensor") {
            Fixture sensor = fixA.getUserData() == "sensor" ? fixA : fixB;
            Fixture object = sensor == fixA ? fixB : fixA;
            if(object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onContact();
            }
        }
        if (fixA.getUserData() == "coin" || fixB.getUserData() == "coin") {
            Fixture sensor = fixA.getUserData() == "coin" ? fixA : fixB;
            Fixture object = sensor == fixA ? fixB : fixA;
            if(object.getUserData() != null && Entity.class.isAssignableFrom(object.getUserData().getClass())) {
                ((Entity) object.getUserData()).entityContact();
            }
            Hud.changeCoins(1);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Gdx.app.log("End Contact", "");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
