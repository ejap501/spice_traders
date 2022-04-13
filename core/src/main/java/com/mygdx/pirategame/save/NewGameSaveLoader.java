package com.mygdx.pirategame.save;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.pirategame.gameobjects.Player;
import com.mygdx.pirategame.gameobjects.enemy.College;
import com.mygdx.pirategame.gameobjects.enemy.CollegeMetadata;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.gameobjects.enemy.SeaMonster;
import com.mygdx.pirategame.gameobjects.entity.Coin;
import com.mygdx.pirategame.pathfinding.PathFinder;
import com.mygdx.pirategame.world.WorldContactListener;
import com.mygdx.pirategame.world.WorldCreator;

import java.util.ArrayList;
import java.util.HashMap;

public class NewGameSaveLoader extends  SaveLoader{
    @Override
    public void load(GameScreen screen) {

        screen.player = new Player(screen);

        // Alcuin college
        screen.getColleges().put(CollegeMetadata.ALCUIN, new College(screen, CollegeMetadata.ALCUIN, 6, screen.getInvalidSpawn()));
        // Anne Lister college
        screen.getColleges().put(CollegeMetadata.ANNELISTER, new College(screen, CollegeMetadata.ANNELISTER, 8, screen.getInvalidSpawn()));
        // Constantine college
        screen.getColleges().put(CollegeMetadata.CONSTANTINE, new College(screen, CollegeMetadata.CONSTANTINE, 8, screen.getInvalidSpawn()));
        // Goodricke college
        screen.getColleges().put(CollegeMetadata.GOODRICKE, new College(screen, CollegeMetadata.GOODRICKE, 8, screen.getInvalidSpawn()));

        for (CollegeMetadata college : CollegeMetadata.values()) {
            screen.getEnemyShips().addAll(screen.getCollege(college).fleet);
        }

        //Random ships
        for (int i = 0; i < 20; i++) {
            int[] loc = screen.getRandomLocation();
            //Add a ship at the random coords
            screen.getEnemyShips().add(new EnemyShip(screen, loc[0], loc[1], "college/Ships/unaligned_ship.png", null));
        }

        //Random sea monsters
        for (int i = 0; i < 7; i++) {
            int[] loc = screen.getRandomLocation();
            //Add a sea monster at the random coords
            screen.getMonsters().add(new SeaMonster(screen, loc[0], loc[1]));
        }

        //Random coins
        for (int i = 0; i < 100; i++) {
            int[] loc = screen.getRandomLocation();
            //Add a coins at the random coords
            screen.getCoins().add(new Coin(screen, loc[0], loc[1]));
        }

        //Random power ups
        screen.addPowerUps();

        /*
        //Random tornado
        Tornados = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int[] loc = getRandomLocation();
            //Add a tornado at the random coords
            Tornados.add(new Tornado(this, loc[0], loc[1]));
        }
        */
    }
}
