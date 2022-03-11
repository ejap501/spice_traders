package com.mygdx.pirategame.tests.Experience;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.PirateGameTest;
import com.mygdx.pirategame.gameobjects.enemy.College;
import com.mygdx.pirategame.gameobjects.enemy.CollegeMetadata;
import com.mygdx.pirategame.screen.GameScreen;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertTrue;

@RunWith (PirateGameTest.class)
public class TestCollegeDefeatGainExp {
    /**
     * Setting up before running this test class
     */
    @BeforeClass
    public static void init() {
        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = mock(GL20.class);
        Gdx.gl = Gdx.gl20;

    }

    /**
     * Test that a player gains experience/points after defeating a college
     */
    @Test
    public void testDefeat(){
        Integer originalPoints = Hud.getPoints();
        Integer newPoints = 0;
        HashMap<CollegeMetadata, College> colleges = Mockito.mock(GameScreen.class).getColleges();
        for (College collegeToTest : colleges.values()){
            collegeToTest.destroyed = true;
            newPoints = Hud.getPoints();
            Integer pointsDiff = newPoints - originalPoints;
            assertTrue("This test passes if points increase by 100 after college defeat", pointsDiff == 100 );
            originalPoints = newPoints;

        }

    }

}
