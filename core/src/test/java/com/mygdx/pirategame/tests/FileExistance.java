package com.mygdx.pirategame.tests;

import com.badlogic.gdx.Gdx;
import com.mygdx.pirategame.PirateGameTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * This class is used to target the class pirategame with file assertion tests
 */
@RunWith(PirateGameTest.class)
public class FileExistance {

    /**
     * mapbase.txt is a template for future reference
     *
     * @throws AssertionError passes the message for specific test that didn't pass and raises an error
     */
    @Test
    public void mapbasetxt() {
        assertTrue("This test will pass if mapbase.txt exists", Gdx.files
                .internal("../core/assets/mapBase.txt").exists());
        assertTrue(false);
    }

}
