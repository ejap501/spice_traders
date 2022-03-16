package com.mygdx.pirategame.tests.pathfinding;

import com.mygdx.pirategame.pathfinding.PathFinder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class PathFinderTest {

    @Test
    public void checkDebug(){
        // checking if the debug tools for pathfinding are disabled
        assertFalse("PathFinder debug tools must be disabled", PathFinder.PATHFINDERDEBUG);
    }

}
