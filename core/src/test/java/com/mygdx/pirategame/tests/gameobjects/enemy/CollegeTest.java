package com.mygdx.pirategame.tests.gameobjects.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.MockClass;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.PirateGameTest;
import com.mygdx.pirategame.gameobjects.enemy.College;
import com.mygdx.pirategame.gameobjects.enemy.CollegeMetadata;
import com.mygdx.pirategame.screen.GameScreen;
import com.mygdx.pirategame.screen.SkillTreeScreen;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(PirateGameTest.class)
public class CollegeTest {

    @BeforeClass
    public static void init() {
        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    /**
     * test to ensure points increase when a college is destroyed (when college is not owned by player)
     */
    @Test
    public void testPointsOnDestroy() {

        // mocking the hud
        MockClass.mockHudStatic();
        College college = new College(MockClass.mockGameScreen(), CollegeMetadata.ANNELISTER, 0, null);

        // destroying the college
        college.setToDestroy = true;
        // giving the college a chance to destroy itself
        college.update(0);

        assertEquals(Hud.getPoints(), Integer.valueOf(100));
    }

}
