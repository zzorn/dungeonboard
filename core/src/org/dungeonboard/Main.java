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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.dungeonboard.model.NonPlayerCharacters;
import org.dungeonboard.model.PlayerCharacter;
import org.dungeonboard.model.World;
import org.dungeonboard.screens.CharacterEditorScreen;
import org.dungeonboard.screens.GroupScreen;
import org.dungeonboard.screens.InitiativeScreen;
import org.dungeonboard.screens.UiScreen;

import java.util.ArrayList;

import static org.dungeonboard.StyleSettings.SCRIPT_FONT;
import static org.dungeonboard.StyleSettings.TITLE_COLOR;

public class Main extends ApplicationAdapter implements Context {

    public static final float TITLE_HEIGHT_PERCENT = 5;
    private Stage stage;
    private Table screenContainer;
    // For debug drawing
    private ShapeRenderer shapeRenderer;
    private Skin skin;
    private AssetManager assetManager;

    private TextureAtlas textureAtlas;

    private UiScreen currentScreen;

    private final ArrayList<UiScreen> uiScreens = new ArrayList<UiScreen>();
    private World world;
    private Label titleLabel;
    private Table rootContainer;


    @Override public void setScreen(UiScreen screen) {
        if (currentScreen != screen) {
            if (currentScreen != null) {
                screenContainer.removeActor(currentScreen.getActor());
                currentScreen.setActive(false);
            }

            currentScreen = screen;

            if (currentScreen != null) {
                screenContainer.addActor(currentScreen.getActor());
                currentScreen.setActive(true);
                titleLabel.setText(currentScreen.getTitle());
            }
            else {
                titleLabel.setText(StyleSettings.APPLICATION_NAME);
            }

        }
    }

    @Override public Skin getSkin() {
        return skin;
    }

    @Override public World getWorld() {
        return world;
    }

    @Override public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    @Override public void switchScreen(boolean toNextOne) {
        final int currentIndex = uiScreens.indexOf(currentScreen);
        final int firstIndex = 0;
        final int lastIndex = uiScreens.size() - 1;

        int index = currentIndex + (toNextOne ? 1 : -1);
        if (index < firstIndex) {
            index = lastIndex;
        }
        else if (index > lastIndex) {
            index = firstIndex;
        }

        if (index >= firstIndex && index <= lastIndex) {
            setScreen(uiScreens.get(index));
        }
        else {
            setScreen(null);
        }

    }

    protected void addScreen(UiScreen screen) {
        uiScreens.add(screen);

        // Select first screen to be added.
        if (currentScreen == null) {
            setScreen(screen);
        }
    }

    public void create () {
        // Create model
        world = createWorld();

        // Build base ui
        buildUi();

        // Add UI Screens
        addScreen(new InitiativeScreen(this));
        addScreen(new GroupScreen(this));
        addScreen(new CharacterEditorScreen(this));
    }

    private void buildUi() {// Build ui
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final float heightPc = height * 0.01f;
        final float widthPc = width * 0.01f;

        // Load textures
        textureAtlas = new TextureAtlas("textures/textures.atlas");

        // Setup list of icons
        StyleSettings.loadIcons(textureAtlas);

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
        rootContainer = new Table(skin);
        rootContainer.setFillParent(true);
        stage.addActor(rootContainer);

        // Title
        Table titleRow = createTitlebar(heightPc);
        rootContainer.add(titleRow).top().expandX().fillX().pad(0, 2*widthPc, 0, 2*widthPc);
        rootContainer.row();

        // Container for the current screen
        screenContainer = new Table(skin);
        rootContainer.add(screenContainer).expand().fill();
    }


    private Table createTitlebar(float heightPc) {
        Table titleRow = new Table(skin);
        titleRow.setHeight(heightPc* TITLE_HEIGHT_PERCENT);

        titleRow.add(createSwitchButton("Prev", false)).left();

        titleLabel = new Label(StyleSettings.APPLICATION_NAME, skin, SCRIPT_FONT, TITLE_COLOR);
        titleLabel.setColor(Color.WHITE);
        titleRow.add(titleLabel).center().expandX();

        titleRow.add(createSwitchButton("Next", true)).right();

        return titleRow;
    }

    private Actor createSwitchButton(final String text, final boolean toNextOne) {
        final TextButton switchButton = new TextButton(text, skin);
        switchButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                switchScreen(toNextOne);
            }
        });
        return switchButton;
    }

    private World createWorld() {
        World world = new World();
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Adventurer1"));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Adventurer2"));
        world.getCurrentEncounter().addCharacter(new PlayerCharacter("Adventurer3"));
        world.getCurrentEncounter().addCharacter(new NonPlayerCharacters("Others"));
        return world;
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
