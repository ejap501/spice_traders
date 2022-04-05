package com.mygdx.pirategame.tests.pathfinding.pathManager;

import java.util.Random;

import com.mygdx.pirategame.pathfinding.pathManager.PatrolPath;
import net.bytebuddy.pool.TypePool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.pirategame.MockClass;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.PirateGameTest;
import com.mygdx.pirategame.gameobjects.enemy.CollegeMetadata;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.pathfinding.PathFinder;
import com.mygdx.pirategame.pathfinding.pathManager.RandomPath;
import com.mygdx.pirategame.screen.GameScreen;
import com.mygdx.pirategame.tests.FakeGL20;

@RunWith(PirateGameTest.class)
public class PatrolPathTest {

    private static GameScreen mockedGameScreen;

    @BeforeClass
    public static void init() {
        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = new FakeGL20();

        // note all mocking cannot appear in a @Test annotated method
        // or the mocking will not work, all mocking must occur in @BeforeClass
        // at least from my testing it does not even work in a @Before method
        MockClass.mockHudStatic();

        mockedGameScreen = MockClass.mockGameScreen();
    }

    /**
     * Test a random path can be instatiated without any errors
     */
    @Test(expected = Test.None.class /* no error expected */)
    public void testCreationAndUpdate() {

        String college = CollegeMetadata.ALCUIN.getFilePath();
        String shipPath = "college/Ships/" + college + "_ship.png";
        EnemyShip ship = new EnemyShip(mockedGameScreen, 13 * 64 / PirateGame.PPM, 11 * 64 / PirateGame.PPM, shipPath,
                CollegeMetadata.ALCUIN);

        PatrolPath path = new PatrolPath(ship, mockedGameScreen);
        path.update(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCollege() {
        String college = CollegeMetadata.ALCUIN.getFilePath();
        String shipPath = "college/Ships/" + college + "_ship.png";
        EnemyShip ship = new EnemyShip(mockedGameScreen, 13 * 64 / PirateGame.PPM, 11 * 64 / PirateGame.PPM, shipPath,
                null);

        PatrolPath path = new PatrolPath(ship, mockedGameScreen);
    }

    /**
     * Ensuring the random path generates a random destination
     */
    @Test
    public void testPatrolPathGeneration() {
        // running the test 20 times to account for randomness
        int ranX, ranY;
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {

            String college = CollegeMetadata.ALCUIN.getFilePath();
            String shipPath = "college/Ships/" + college + "_ship.png";
            EnemyShip ship = new EnemyShip(mockedGameScreen, 13 * 64 / PirateGame.PPM, 11 * 64 / PirateGame.PPM,
                    shipPath, CollegeMetadata.ALCUIN);

            PatrolPath path = new PatrolPath(ship, mockedGameScreen);

            // generating destination
            Vector2 dest = path.generateDestination();

            // ensuring the destination is valid
            PathFinder pathFinder = new PathFinder(mockedGameScreen, 64);
            Assert.assertTrue(pathFinder.isTraversable(dest.x, dest.y, 1, 1));
        }


    }


}
