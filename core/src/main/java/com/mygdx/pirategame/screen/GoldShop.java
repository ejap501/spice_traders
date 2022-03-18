package com.mygdx.pirategame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.pirategame.PirateGame;

import java.util.ArrayList;
import java.util.List;

public class GoldShop implements Screen {

    private final PirateGame parent;
    private final Stage stage;
    /*private ShapeRenderer shapeRenderer;
    private Rectangle rectangle;*/
    public boolean display = false;
    private OrthographicCamera camera;
    private GameScreen gameScreen;

    //To store whether buttons are enabled or disabled
    private static final List<Integer> states = new ArrayList<Integer>();

    /**
     * adding default states
     */
    static {
        //0 = enabled, 1 = disabled
        states.add(1);
        states.add(1);
        states.add(1);
        states.add(1);
    }

    private static TextButton item1;
    private TextButton item2;
    private TextButton item3;
    private TextButton item4;

    public GoldShop(PirateGame pirateGame, OrthographicCamera camera, GameScreen gameScreen) {
        parent = pirateGame;
        this.camera = camera;
        this.gameScreen = gameScreen;
        stage = new Stage(new ScreenViewport());
    }

    /**
     * Called when this screen becomes the current screen
     */
    @Override
    public void show() {
        // Renders the shop in GameScreen if display is true
        display = true;

        //The skin for the actors
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        //Set the input processor
        Gdx.input.setInputProcessor(stage);
        // Create a table that fills the screen
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        /*// Shop background
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        rectangle = new Rectangle(stage.getCamera().position.x - stage.getCamera().viewportWidth / 5f, stage.getCamera().position.y - stage.getCamera().viewportHeight / 2.4f,
                stage.getCamera().viewportWidth / 2.5f, stage.getCamera().viewportHeight / 1.2f);*/


        //create skill tree buttons
        item1 = new TextButton("????????????", skin);

        //Sets enabled or disabled
        if (states.get(0) == 1){
            item1.setDisabled(true);
        }
        item2 = new TextButton("????????????", skin);
        if (states.get(1) == 1){
            item2.setDisabled(true);
        }
        item3 = new TextButton("????????????", skin);
        if (states.get(2) == 1){
            item3.setDisabled(true);
        }

        item4 = new TextButton("????????????", skin);

        if (states.get(3) == 1){
            item4.setDisabled(true);

        }

        // Item price labels
        final Label unlock100 = new Label("100 gold",skin);
        final Label unlock200 = new Label("200 gold",skin);
        final Label unlock300 = new Label("300 gold",skin);
        final Label unlock400 = new Label("400 gold",skin);
        final Label goldShop = new Label("Gold Shop",skin);
        goldShop.setFontScale(1.2f);

        //Return Button
        TextButton closeButton = new TextButton("Close", skin);

        // Close the gold shop
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                display = false; // Stop the shop from being rendered
                dispose();
            }
        });

        //add buttons and labels to main table
        table.row().pad(100, 0, 10, 0);
        table.add(goldShop);
        table.row().pad(10, 0, 10, 0);
        table.add(item1).width(stage.getCamera().viewportWidth / 5f).height(stage.getCamera().viewportHeight / 9f);
        table.add(unlock100);
        table.row().pad(10, 0, 10, 0);
        table.add(item2).width(stage.getCamera().viewportWidth / 5f).height(stage.getCamera().viewportHeight / 9f);
        table.add(unlock200);
        table.row().pad(10, 0, 10, 0);
        table.add(item3).width(stage.getCamera().viewportWidth / 5f).height(stage.getCamera().viewportHeight / 9f);
        table.add(unlock300);
        table.row().pad(10, 0, 10, 0);
        table.add(item4).width(stage.getCamera().viewportWidth / 5f).height(stage.getCamera().viewportHeight / 9f);
        table.add(unlock400);
        table.row().pad(10, 0, 10, 0);
        table.add(closeButton).width(stage.getCamera().viewportWidth / 5f).height(stage.getCamera().viewportHeight / 9f);
        table.top();
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        /*// Render background color of shop
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        shapeRenderer.end();

        // Render border around background
        shapeRenderer.begin();
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(rectangle.getX(), rectangle.getY(), rectangle.getWidth()+1, rectangle.getHeight()+1);
        shapeRenderer.end();*/


        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getViewport().getCamera().update();
    }

    /**
     * (Not Used)
     * Pauses game
     */
    @Override
    public void pause() {

    }

    /**
     * (Not Used)
     * Resumes game
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        stage.dispose();
        //shapeRenderer.dispose();
    }
}
