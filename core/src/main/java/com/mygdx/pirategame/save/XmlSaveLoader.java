package com.mygdx.pirategame.save;

import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.gameobjects.Player;
import com.mygdx.pirategame.gameobjects.enemy.College;
import com.mygdx.pirategame.gameobjects.enemy.CollegeMetadata;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.gameobjects.enemy.SeaMonster;
import com.mygdx.pirategame.gameobjects.entity.Coin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlSaveLoader extends SaveLoader {

    private File file;

    public XmlSaveLoader(File file){
        this.file = file;
    }

    @Override
    public void load(GameScreen screen) {
        try{

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);

            Element root = document.getDocumentElement();

            Element player = (Element) root.getElementsByTagName("player").item(0);
            screen.player = new Player(screen, player);

            Hud.load((Element) root.getElementsByTagName("hud").item(0));

            // TODO ANYTHING BELOW NEEDS MODIFYING
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
        } catch(Exception e){

            // TODO catch error
        }
    }
}
