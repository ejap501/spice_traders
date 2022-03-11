package com.mygdx.pirategame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.pirategame.Hud;
import com.mygdx.pirategame.PirateGame;
import com.mygdx.pirategame.gameobjects.Player;
import com.mygdx.pirategame.gameobjects.enemy.College;
import com.mygdx.pirategame.gameobjects.enemy.CollegeMetadata;
import com.mygdx.pirategame.gameobjects.enemy.EnemyShip;
import com.mygdx.pirategame.gameobjects.entity.Coin;
import com.mygdx.pirategame.pathfinding.PathFinder;

import com.mygdx.pirategame.gameobjects.entity.*;

import com.mygdx.pirategame.world.AvailableSpawn;
import com.mygdx.pirategame.world.WorldContactListener;
import com.mygdx.pirategame.world.WorldCreator;

import java.util.*;


/**
 * Game Screen
 * Class to generate the various screens used to play the game.
 * Instantiates all screen types and displays current screen.
 *
 * @author Ethan Alabaster, Adam Crook, Joe Dickinson, Sam Pearson, Tom Perry, Edward Poulter
 * @version 1.0
 */
public class GameScreen implements Screen {
    private static float maxSpeed = 4f;
    private static float accel = 0.1f;
    private static float shootingDelay = 0.5f;
    private float stateTime;

    public static PirateGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final Stage stage;

    private final TmxMapLoader maploader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final Player player;
    private static HashMap<CollegeMetadata, College> colleges = new HashMap<>();
    private static ArrayList<EnemyShip> ships = new ArrayList<>();
    private static ArrayList<Coin> Coins = new ArrayList<>();

    private static ArrayList<PowerUp> PowerUps = new ArrayList<>();
    private final AvailableSpawn invalidSpawn = new AvailableSpawn();
    private final Hud hud;


    public static final int GAME_RUNNING = 0;
    public static final int GAME_PAUSED = 1;
    private static int gameStatus;

    private final PathFinder pathFinder;

    private Table pauseTable;
    private Table table;

    public Random rand = new Random();

    private Integer attackingCollege;
    private final List<Integer> collegesLeft = new LinkedList<Integer>(Arrays.asList(1, 2, 3));
    private final Random collegeRand = new Random();

    /**
     * Initialises the Game Screen,
     * generates the world data and data for entities that exist upon it,
     *
     * @param game passes game data to current class,
     */
    public GameScreen(PirateGame game) {
        gameStatus = GAME_RUNNING;
        GameScreen.game = game;
        // Initialising camera and extendable viewport for viewing game
        camera = new OrthographicCamera();
        camera.zoom = 0.0155f;
        viewport = new FitViewport(1280, 720, camera);
        camera.position.set(viewport.getWorldWidth() / 3, viewport.getWorldHeight() / 3, 0);

        // Initialize a hud
        hud = new Hud(game.batch);

        // Initialising box2d physics
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        player = new Player(this);

        // making the Tiled tmx file render as a map
        maploader = new TmxMapLoader();
        map = maploader.load("map/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, getUnitScale());
        pathFinder = new PathFinder(this, 64);

        new WorldCreator(this);

        // Setting up contact listener for collisions
        world.setContactListener(new WorldContactListener());

        // Spawning enemy ship and coin. x and y is spawn location
        colleges = new HashMap<>();

        // Alcuin college
        colleges.put(CollegeMetadata.ALCUIN, new College(this, CollegeMetadata.ALCUIN, 6, invalidSpawn));
        // Anne Lister college
        colleges.put(CollegeMetadata.ANNELISTER, new College(this, CollegeMetadata.ANNELISTER, 8, invalidSpawn));
        // Constantine college
        colleges.put(CollegeMetadata.CONSTANTINE, new College(this, CollegeMetadata.CONSTANTINE, 8, invalidSpawn));
        // Goodricke college
        colleges.put(CollegeMetadata.GOODRICKE, new College(this, CollegeMetadata.GOODRICKE, 8, invalidSpawn));

        ships = new ArrayList<>();

        for (CollegeMetadata college : CollegeMetadata.values()) {
            ships.addAll(getCollege(college).fleet);
        }

        //Random ships
        for (int i = 0; i < 20; i++) {
            int[] loc = getRandomLocation();
            //Add a ship at the random coords
            ships.add(new EnemyShip(this, loc[0], loc[1], "college/Ships/unaligned_ship.png", null));
        }

        //Random coins
        Coins = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int[] loc = getRandomLocation();
            //Add a coins at the random coords
            Coins.add(new Coin(this, loc[0], loc[1]));
        }

        addPowerUps();

        //Setting stage
        stage = new Stage(new ScreenViewport());

        //Setting the college that can currently be attacked
        nextCollege();
    }

    /**
     * Randomly generates x and y and checks if they are valid
     *
     * @return x, y
     */
    public int[] getRandomLocation() {
        Boolean validLoc = false;
        int x = 0, y = 0;
        while (!validLoc) {
            //Get random x and y coords
            x = rand.nextInt(AvailableSpawn.xCap - AvailableSpawn.xBase) + AvailableSpawn.xBase;
            y = rand.nextInt(AvailableSpawn.yCap - AvailableSpawn.yBase) + AvailableSpawn.yBase;
            validLoc = checkGenPos(x, y);
        }
        return new int[]{x, y};
    }

    /**
     * Randomly positions power ups around the sea
     */
    public void addPowerUps() {
        //Random powerups
        PowerUps = new ArrayList<>();
        // Add Speedboosts

        for (int i = 0; i < 100; i++) {
            //Add a powerup at the random coords
            int[] loc = getRandomLocation();

            int select = i % 4;
            // Iterates through to add each power up
            if (select == 0) {
                PowerUps.add(new AbsorptionHeart(this, loc[0], loc[1]));
            }else if (select == 1) {
                PowerUps.add(new SpeedBoost(this, loc[0], loc[1]));
            }else if (select == 2) {
                PowerUps.add(new FasterShooting(this, loc[0], loc[1]));
            } else {
                PowerUps.add(new CoinMagnet(this, loc[0], loc[1]));
            }
        }
    }

    /**
     * Returns the player object
     * @return player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the array of coins in the level
     * @return coin array
     */
    public ArrayList<Coin> getCoins() {
        return Coins;
    }

    /**
     * Makes this the current screen for the game.
     * Generates the buttons to be able to interact with what screen is being displayed.
     * Creates the escape menu and pause button
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        //GAME BUTTONS
        final TextButton pauseButton = new TextButton("Pause", skin);
        final TextButton skill = new TextButton("Skill Tree", skin);

        //PAUSE MENU BUTTONS
        final TextButton start = new TextButton("Resume", skin);
        final TextButton options = new TextButton("Options", skin);
        TextButton exit = new TextButton("Exit", skin);

        //Create main table and pause tables
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        pauseTable = new Table();
        pauseTable.setFillParent(true);
        stage.addActor(pauseTable);

        //Set the visibility of the tables. Particularly used when coming back from options or skillTree
        if (gameStatus == GAME_PAUSED) {
            table.setVisible(false);
            pauseTable.setVisible(true);
        } else {
            pauseTable.setVisible(false);
            table.setVisible(true);
        }

        //ADD TO TABLES
        table.add(pauseButton);
        table.row().pad(10, 0, 10, 0);
        table.left().top();

        pauseTable.add(start).fillX().uniformX();
        pauseTable.row().pad(20, 0, 10, 0);
        pauseTable.add(skill).fillX().uniformX();
        pauseTable.row().pad(20, 0, 10, 0);
        pauseTable.add(options).fillX().uniformX();
        pauseTable.row().pad(20, 0, 10, 0);
        pauseTable.add(exit).fillX().uniformX();
        pauseTable.center();


        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                table.setVisible(false);
                pauseTable.setVisible(true);
                pause();

            }
        });
        skill.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseTable.setVisible(false);
                game.changeScreen(PirateGame.SKILL);
            }
        });
        start.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseTable.setVisible(false);
                table.setVisible(true);
                resume();
            }
        });
        options.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    pauseTable.setVisible(false);
                                    game.setScreen(new OptionsScreen(game, game.getScreen()));
                                }
                            }
        );
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    /**
     * Checks for input and performs an action
     * Applies to key "W" "A" "S" "D" "E" "Esc" "Left" "Right" "Up" "Down"
     * <p>
     * Caps player velocity
     *
     * @param dt Delta time (elapsed time since last game tick)
     */
    public void handleInput(float dt) {
        if (gameStatus == GAME_RUNNING) {
            // Left physics impulse on 'A'
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.b2body.applyLinearImpulse(new Vector2(-accel, 0), player.b2body.getWorldCenter(), true);
            }
            // Right physics impulse on 'D'
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.b2body.applyLinearImpulse(new Vector2(accel, 0), player.b2body.getWorldCenter(), true);
            }
            // Up physics impulse on 'W'
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.b2body.applyLinearImpulse(new Vector2(0, accel), player.b2body.getWorldCenter(), true);
            }
            // Down physics impulse on 'S'
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.b2body.applyLinearImpulse(new Vector2(0, -accel), player.b2body.getWorldCenter(), true);
            }
            // Checking if player at max velocity, and keeping them below max
            if (player.b2body.getLinearVelocity().x >= maxSpeed) {
                player.b2body.applyLinearImpulse(new Vector2(-accel, 0), player.b2body.getWorldCenter(), true);
            }
            if (player.b2body.getLinearVelocity().x <= -maxSpeed) {
                player.b2body.applyLinearImpulse(new Vector2(accel, 0), player.b2body.getWorldCenter(), true);
            }
            if (player.b2body.getLinearVelocity().y >= maxSpeed) {
                player.b2body.applyLinearImpulse(new Vector2(0, -accel), player.b2body.getWorldCenter(), true);
            }
            if (player.b2body.getLinearVelocity().y <= -maxSpeed) {
                player.b2body.applyLinearImpulse(new Vector2(0, accel), player.b2body.getWorldCenter(), true);
            }
            // Firing Code, when left mouse is pressed
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                player.fire(camera);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (gameStatus == GAME_PAUSED) {
                resume();
                table.setVisible(true);
                pauseTable.setVisible(false);
            } else {
                table.setVisible(false);
                pauseTable.setVisible(true);
                pause();
            }
        }
    }

    /**
     * Updates the state of each object with delta time
     *
     * @param dt Delta time (elapsed time since last game tick)
     */
    public void update(float dt) {
        stateTime += dt;
        handleInput(dt);
        // Stepping the physics engine by time of 1 frame
        world.step(1 / 60f, 6, 2);

        // Update all players and entities
        player.update(dt);

        for (CollegeMetadata college : CollegeMetadata.values()) {
            getCollege(college).update(dt);
        }

        //Update ships
        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).update(dt);
        }

        //Updates coin
        for (int i = 0; i < Coins.size(); i++) {
            Coins.get(i).update();
        }
        //Updates powerups
        for (int i = 0; i < PowerUps.size(); i++) {
            PowerUps.get(i).update();
        }
        //After a delay check if a college is destroyed. If not, if can fire
        if (stateTime > 1) {
            for (CollegeMetadata college : CollegeMetadata.values()) {
                if (!college.isPlayer() && !getCollege(college).destroyed) {
                    getCollege(college).fire();
                }
            }
            stateTime = 0;
        }

        hud.update(dt);

        // Centre camera on player boat
        camera.position.x = player.b2body.getPosition().x;
        camera.position.y = player.b2body.getPosition().y;
        camera.update();
        renderer.setView(camera);
    }

    /**
     * Renders the visual data for all objects
     * Changes and renders new visual data for ships
     *
     * @param dt Delta time (elapsed time since last game tick)
     */
    @Override
    public void render(float dt) {
        if (gameStatus == GAME_RUNNING) {
            update(dt);
        } else {
            handleInput(dt);
        }

        Gdx.gl.glClearColor(46 / 255f, 204 / 255f, 113 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
        // b2dr is the hitbox shapes, can be commented out to hide
        //b2dr.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // Order determines layering

        //Renders coins
        for (int i = 0; i < Coins.size(); i++) {
            Coins.get(i).draw(game.batch);
        }
        //Renders powerups
        for (int i = 0; i < PowerUps.size(); i++) {
            PowerUps.get(i).draw(game.batch);
        }

        //Renders colleges
        player.draw(game.batch);
        for (Map.Entry<CollegeMetadata, College> college : colleges.entrySet()) {
            college.getValue().draw(game.batch);
        }

        //Updates all ships
        for (int i = 0; i < ships.size(); i++) {
            // if the ship is in a college
            if (ships.get(i).collegeMeta != null) {
                //Flips a colleges allegiance if their college is destroyed
                if (getCollege(ships.get(i).collegeMeta).destroyed) {

                    ships.get(i).updateTexture(0, "college/Ships/alcuin_ship.png");
                }
            }
            ships.get(i).draw(game.batch);
        }
        game.batch.end();
        Hud.stage.draw();
        stage.act();
        stage.draw();
        //Checks game over conditions
        gameOverCheck();
    }

    /**
     * Changes the camera size, Scales the hud to match the camera
     *
     * @param width  the width of the viewable area
     * @param height the height of the viewable area
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
        Hud.resize(width, height);
        camera.update();
        renderer.setView(camera);
    }

    /**
     * Returns the map
     *
     * @return map : returns the world map
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * @return The tile map width in in-game pixels
     */
    public int getTileMapWidth() {
        MapProperties prop = getMap().getProperties();
        int mapWidth = prop.get("width", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);

        return mapWidth * tilePixelWidth;
    }

    /**
     * @return the width of a single tile on the tilemap
     */
    public int getTileWidth() {
        MapProperties prop = getMap().getProperties();
        int tilePixelWidth = prop.get("tilewidth", Integer.class);

        return tilePixelWidth;
    }

    /**
     * @return The tile map height in in-game pixels
     */
    public int getTileMapHeight() {
        MapProperties prop = getMap().getProperties();
        int mapHeight = prop.get("height", Integer.class);
        int tilePixelHeight = prop.get("tileheight", Integer.class);

        return mapHeight * tilePixelHeight;
    }

    /**
     * Returns the world (map and objects)
     *
     * @return world : returns the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns the college from the colleges hashmap
     *
     * @param collegeID uses a collegeID as an index
     * @return college : returns the college fetched from colleges
     * @deprecated use CollegeMetadata instead of collegeID
     */
    @Deprecated
    public College getCollege(Integer collegeID) {
        return getCollege(CollegeMetadata.getCollegeMetaFromId(collegeID));
    }

    /**
     * Returns the college from the colleges hashmap
     *
     * @param college uses the collegeMetadata to find the college
     * @return returns the college fetched from colleges
     */
    public College getCollege(CollegeMetadata college) {
        return colleges.get(college);
    }

    /**
     * When called, finds the next college that the player can attack
     */
    public void nextCollege() {
        // Selects, at random, an index from length of collegeLeft list
        Integer collegeIndex = collegeRand.nextInt(collegesLeft.size());
        // Gets the collegeID from the collegeLeft list
        attackingCollege = collegesLeft.get(collegeIndex);
        // Removes the college selected from the collegeLeft list
        collegesLeft.remove(collegeIndex);
    }

    /**
     * Returns the college which can currently be attacked
     *
     * @return integer : returns CollegeID
     */
    public Integer getAttackingCollege() {
        return attackingCollege;
    }

    /**
     * Checks if the game is over
     * i.e. goal reached (all colleges bar 0 are destroyed)
     */
    public void gameOverCheck() {
        //Lose game if ship on 0 health or Alcuin is destroyed
        if (Hud.getHealth() <= 0 || getCollege(CollegeMetadata.ALCUIN).destroyed) {
            game.changeScreen(PirateGame.DEATH);
            game.killGame();
        }
        //Win game if all colleges destroyed
        else if (getCollege(CollegeMetadata.ANNELISTER).destroyed && getCollege(CollegeMetadata.CONSTANTINE).destroyed && getCollege(CollegeMetadata.GOODRICKE).destroyed) {
            game.changeScreen(PirateGame.VICTORY);
            game.killGame();
        }
    }

    /**
     * Fetches the player's current position
     *
     * @return position vector : returns the position of the player
     */
    public Vector2 getPlayerPos() {
        return new Vector2(player.b2body.getPosition().x, player.b2body.getPosition().y);
    }

    /**
     * Calculates the players position centered in the middle of the player
     *
     * @return The centered position of the player
     */
    public Vector2 getCenteredPlayerPos() {
        return getPlayerPos().add(player.getWidth(), player.getHeight());
    }

    /**
     * Updates acceleration by a given percentage. Accessed by skill tree and power ups
     *
     * @param percentage percentage increase
     */
    public static void changeAcceleration(Float percentage) {
        accel = accel * (1 + (percentage / 100));
    }

    /**
     * Sets acceleration to a given value
     *
     * @param value new acceleration value
     */
    public static void setAcceleration(Float value) {
        accel = value;
    }

    /**
     * Updates max speed by a given percentage. Accessed by skill tree
     *
     * @param percentage percentage increase
     */
    public static void changeMaxSpeed(Float percentage) {
        maxSpeed = maxSpeed * (1 + (percentage / 100));
    }

    /**
     * Sets max speed to a given value
     *
     * @param value new max speed value
     */
    public static void setMaxSpeed(Float value) {
        maxSpeed = value;
    }

    /**
     * Resets game values to default, used on game restart
     */
    public static void resetValues() {
        setMaxSpeed(4f);
        setAcceleration(0.1f);
        setShootingDelay(1f);
    }

    /**
     * Fetches the current shooting delay
     *
     * @return shooting delay : returns the current shooting delay value
     */
    public static float getShootingDelay() {
        return shootingDelay;
    }

    /**
     * Sets shooting delay to a given value
     *
     * @param value new shooting delay value
     */
    public static void setShootingDelay(Float value) {
        shootingDelay = value;
    }

    /**
     * Updates shooting delay by a given percentage. Accessed by power ups
     *
     * @param percentage percentage decrease
     */
    public static void changeShootingDelay(Float percentage) {
        shootingDelay = shootingDelay * (1 - (percentage / 100));
    }

    /**
     * Changes the amount of damage done by each hit. Accessed by skill tree
     *
     * @param value damage dealt
     */
    public static void changeDamage(int value) {

        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).changeDamageReceived(value);
        }

        for(Map.Entry<CollegeMetadata, College> college : colleges.entrySet()){
            college.getValue().changeDamageReceived(value);
        }

    }

    /**
     * Tests validity of randomly generated position
     *
     * @param x random x value
     * @param y random y value
     */
    private Boolean checkGenPos(int x, int y) {
        if (invalidSpawn.tileBlocked.containsKey(x)) {
            ArrayList<Integer> yTest = invalidSpawn.tileBlocked.get(x);
            return !yTest.contains(y);
        }
        return true;
    }

    /**
     * Pauses game
     */
    @Override
    public void pause() {
        gameStatus = GAME_PAUSED;
    }

    /**
     * Resumes game
     */
    @Override
    public void resume() {
        gameStatus = GAME_RUNNING;
    }

    /**
     * (Not Used)
     * Hides game
     */
    @Override
    public void hide() {

    }

    /**
     * Disposes game data
     */
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        stage.dispose();
    }


    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public float getUnitScale() {
        return 1 / PirateGame.PPM;
    }

    public PathFinder getPathFinder() {
        return pathFinder;
    }

    public AvailableSpawn getInvalidSpawn() {
        return invalidSpawn;
    }

    public HashMap<CollegeMetadata, College> getColleges() {
        return colleges;

    }
}
