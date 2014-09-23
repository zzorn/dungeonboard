package org.dungeonboard;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.dungeonboard.model.NonPlayerCharacters;
import org.dungeonboard.model.PlayerCharacter;
import org.dungeonboard.screens.UiScreen;
import org.dungeonboard.screens.initiative.InitiativeScreen;

public class Main extends ApplicationAdapter {

    private Stage stage;
    private Table rootContainer;
    // For debug drawing
    private ShapeRenderer shapeRenderer;
    private Skin skin;
    private AssetManager assetManager;

    private TextureAtlas textureAtlas;


    private UiScreen currentScreen;

    public void setScreen(UiScreen screen) {
        if (currentScreen != screen) {
            if (currentScreen != null) {
                rootContainer.removeActor(currentScreen.getActor(skin));
                currentScreen.setActive(false);
            }

            currentScreen = screen;

            if (currentScreen != null) {
                rootContainer.addActor(currentScreen.getActor(skin));
                currentScreen.setActive(true);
            }
        }
    }

    public void create () {
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final float heightPc = height * 0.01f;
        final float widthPc = width * 0.01f;

        // Load textures
        textureAtlas = new TextureAtlas("textures/textures.atlas");


        // Stage
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        final OrthographicCamera camera = new OrthographicCamera(width, height);
        stage.getViewport().setCamera(camera);

        // Load skin with custom sized font
        skin = new Skin(new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas")));
        final BitmapFont dialogFont = createFont("fonts/"+ StyleSettings.DIALOG_FONT_NAME +".ttf", height / StyleSettings.NUMBER_OF_ROWS_OF_DIALOG_TEXT_TO_FIT_ON_SCREEN);
        final BitmapFont dialogLargeFont = createFont("fonts/"+ StyleSettings.DIALOG_FONT_NAME +".ttf", height / StyleSettings.NUMBER_OF_ROWS_OF_LARGE_DIALOG_TEXT_TO_FIT_ON_SCREEN);
        final BitmapFont scriptFont = createFont("fonts/"+ StyleSettings.SCRIPT_FONT_NAME +".ttf", height / StyleSettings.NUMBER_OF_ROWS_OF_SCRIPT_TEXT_TO_FIT_ON_SCREEN);
        skin.add(StyleSettings.DIALOG_FONT, dialogFont, BitmapFont.class);
        skin.add(StyleSettings.DIALOG_LARGE_FONT, dialogLargeFont, BitmapFont.class);
        skin.add(StyleSettings.SCRIPT_FONT, scriptFont, BitmapFont.class);
        skin.load(Gdx.files.internal("skins/uiskin.json"));

        // Renderer?
        shapeRenderer = new ShapeRenderer();

        // Root container
        rootContainer = new Table();
        rootContainer.setFillParent(true);
        stage.addActor(rootContainer);

        // List of actors
        Table actorList = new Table();
        actorList.pad(10).defaults().expandX().space(4);

        final ScrollPane scroll = new ScrollPane(actorList, skin);
        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };

        // Debug content
        for (int i = 0; i < 100; i++) {
            actorList.row();
            actorList.add(new Label(i + " Jörgen", skin, StyleSettings.SCRIPT_FONT, new Color(0.9f, 0.8f, 0.7f, 1f))).expandX().fillX();
            final String buttonText = i + "dos";
            TextButton button = new TextButton(buttonText, skin);

            actorList.add(button);
            button.addListener(new ClickListener() {
                public void clicked (InputEvent event, float x, float y) {
                    System.out.println(buttonText + " click " + x + ", " + y);
                }
            });
            Slider slider = new Slider(0, 100, 1, false, skin);
            slider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.
            actorList.add(slider);
            actorList.add(new Label(i + "tres long0 long1 long2 long3 long4 long5 long6 long7 long8 long9 long10 long11 long12", skin));
        }

        final TextButton flickButton = new TextButton("Flick Scroll", skin.get("toggle", TextButton.TextButtonStyle.class));
        flickButton.setChecked(true);
        flickButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                scroll.setFlickScroll(flickButton.isChecked());
            }
        });
        final TextButton fadeButton = new TextButton("Fade Scrollbars", skin.get("toggle", TextButton.TextButtonStyle.class));
        fadeButton.setChecked(true);
        fadeButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                scroll.setFadeScrollBars(fadeButton.isChecked());
            }
        });
        final TextButton smoothButton = new TextButton("Smooth Scrolling", skin.get("toggle", TextButton.TextButtonStyle.class));
        smoothButton.setChecked(true);
        smoothButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                scroll.setSmoothScrolling(smoothButton.isChecked());
            }
        });
        final TextButton onTopButton = new TextButton("Scrollbars On Top", skin.get("toggle", TextButton.TextButtonStyle.class));
        onTopButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                scroll.setScrollbarsOnTop(onTopButton.isChecked());
            }
        });
        //rootContainer.add(scroll).expand().fill();


        Table mainButtonBar = new Table();
        mainButtonBar.row().space(2*heightPc).padBottom(3 * heightPc);
        mainButtonBar.pad(10).defaults().expandX().space(4);
        mainButtonBar.add(flickButton).right().expandX();
        mainButtonBar.add(onTopButton);
        mainButtonBar.add(smoothButton);
        mainButtonBar.add(fadeButton).left().expandX();
        ScrollPane mainButtonScroll = new ScrollPane(mainButtonBar, skin);
        mainButtonScroll.setScrollingDisabled(false, true);
        mainButtonScroll.setFlickScroll(true);
        mainButtonScroll.setColor(Color.BLACK);

        //rootContainer.row().height(heightPc * 10);
        //rootContainer.add(mainButtonScroll).expandX().fill();


        World world = new World();
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Adventurer1"));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Adventurer2"));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Adventurer3"));
        world.getCurrentEncounter().addCharacter(new NonPlayerCharacters("Others"));
        /*
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Igor", Color.PINK));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Jurgen"));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Oscaaar", Color.CYAN));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Cthulhu", Color.GREEN));
        */
        InitiativeScreen initiativeScreen = new InitiativeScreen(world, textureAtlas);
        setScreen(initiativeScreen);

    }

    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        Table.drawDebug(stage); // This is optional, but enables debug lines for tables.
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }


    private ScrollPane createActorList() {

        VerticalGroup actorList = new VerticalGroup();
        ScrollPane actorScroll = new ScrollPane(actorList, skin);




        return actorScroll;
    }


    private BitmapFont createFont(final String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Adjust size to available
        parameter.size = size;

        BitmapFont font = generator.generateFont(parameter);

        // Dispose generator
        generator.dispose();

        return font;
    }






}
