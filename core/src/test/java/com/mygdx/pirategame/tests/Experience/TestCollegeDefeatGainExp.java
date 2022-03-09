package com.mygdx.pirategame.tests.Experience;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.PirateGameTest;
import com.mygdx.pirategame.gameobjects.enemy.College;
import com.mygdx.pirategame.screen.GameScreen;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

        // Mock the graphics class.
        Gdx.graphics = mock(Graphics.class);
        when(Gdx.graphics.getWidth()).thenReturn(500);
        when(Gdx.graphics.getHeight()).thenReturn(500);

    }

    /**
     * Test that a player gains experience/points after defeating a college
     */
    @Test
    public void testDefeat(){
        Integer originalPoints = Hud.getPoints();
        Integer newPoints = 0;
        HashMap<Integer, College> colleges = Mockito.mock(GameScreen.class).getColleges();
        for (College collegeToTest : colleges.values()){
            collegeToTest.destroyed = true;
            newPoints = Hud.getPoints();
            Integer pointsDiff = newPoints - originalPoints;
            assertTrue("This test passes if points increase by 100 after college defeat", pointsDiff == 100 );
            originalPoints = newPoints;

        }

    }

}
