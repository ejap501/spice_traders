package com.mygdx.pirategame.tests.screen;

import com.mygdx.pirategame.save.GameScreen;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class GameScreenTest {

    @Test
    public void testDebugToolsDisabled() {
        assertFalse(GameScreen.PHYSICSDEBUG);
    }

}
