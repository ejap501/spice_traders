package com.mygdx.pirategame.tests.pathfinding;

import com.mygdx.pirategame.MockClass;
import com.mygdx.pirategame.pathfinding.PathFinder;
import com.mygdx.pirategame.screen.GameScreen;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Unit tests for the PathFinder class
 */
public class PathFinderTest {

    /**
     * Ensuring the debug tools for the pathfinder are disabled
     */
    @Test
    public void checkDebug() {
        // checking if the debug tools for pathfinding are disabled
        assertFalse("PathFinder debug tools must be disabled", PathFinder.PATHFINDERDEBUG);
    }

}
