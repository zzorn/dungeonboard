package org.dungeonboard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.World;
import org.dungeonboard.actions.GameAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dungeonboard.StyleSettings.SCRIPT_FONT;
import static org.dungeonboard.StyleSettings.TITLE_COLOR;

/**
 *
 */
public abstract class UiScreenBase implements UiScreen {


    private static final int NUMBER_OF_BUTTONS_PER_ROW = 4;
    private final World world;
    private String title;
    private boolean active;
    private Skin skin;
    private Actor actor;
    private Table buttonArea;

    private List<GameAction> actions = new ArrayList<GameAction>();

    private final Map<GameAction, TextButton> buttonsForActions = new HashMap<GameAction, TextButton>();

    protected UiScreenBase(World world, String title) {
        this.world = world;
        this.title = title;
    }

    public final String getTitle() {
        return title;
    }

    @Override public Actor getActor(Skin skin) {
        if (actor == null) {
            actor = create(skin);
        }

        return actor;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;

            if (this.active) onActivated();
            else onDeactivated();
        }
    }

    @Override public final boolean isActive() {
        return active;
    }

    @Override public final Actor create(Skin skin) {
        this.skin = skin;

        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final float heightPc = height * 0.01f;
        final float widthPc = width * 0.01f;

        // Create root table
        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);

        Table titleRow = new Table(skin);
        titleRow.setHeight(heightPc*5);

        // Add titlebar
        titleRow.add(new Label(getTitle(), skin, SCRIPT_FONT, TITLE_COLOR)).center();
        rootTable.add(titleRow).expandX().fill();

        // Add screen change buttons
        // TODO

        rootTable.row();


        buttonArea = new Table(skin);


        // Content
        final Actor content = createContent(this.skin, world);
        rootTable.add(content).expand().fill();

        // Action buttons
        rootTable.row();
        ScrollPane buttonScroll = new ScrollPane(buttonArea, skin);
        buttonScroll.setColor(Color.BLACK);
        buttonScroll.setFlickScroll(true);
        buttonScroll.setFadeScrollBars(true);
        buttonScroll.setScrollingDisabled(false, true);
        buttonScroll.setupFadeScrollBars(0, 0);
        rootTable.add(buttonScroll).left().padBottom(heightPc*1).expandX();

        return rootTable;
    }

    @Override public final void update(float deltaTimeSeconds) {
        refreshActionButtons();
        onUpdate(deltaTimeSeconds, world);
    }

    @Override public final void dispose() {
        onDispose();
    }

    protected World getWorld() {
        return world;
    }

    protected void addAction(final GameAction gameAction) {
        final int height = Gdx.graphics.getHeight();
        final float heightPc = height * 0.01f;

        if (!actions.contains(gameAction)) {
            actions.add(gameAction);
            final TextButton button = new TextButton("   " + gameAction.getName() + "   ", skin);
            button.setColor(gameAction.getColor());
            button.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (gameAction.isAvailable(world)) {
                        gameAction.doAction(world);
                        StyleSettings.playButtonPressSound();
                    }
                }
            });

            /*
            // Add a new row every so many buttons
            if (actions.size() % NUMBER_OF_BUTTONS_PER_ROW == 0) {
                buttonArea.row();
            }
            */

            buttonArea.add(button).left().padTop(3).padBottom(3).fillX();


            buttonsForActions.put(gameAction, button);

            refreshActionButtons();
        }
    }

    protected void removeAction(GameAction gameAction) {
        if (actions.contains(gameAction)) {

            // Remove from list
            actions.remove(gameAction);

            // Remove from ui
            final TextButton button = buttonsForActions.get(gameAction);
            buttonArea.removeActor(button);

            // Remove mapping
            buttonsForActions.remove(gameAction);

            refreshActionButtons();
        }
    }

    protected void refreshActionButtons() {
        for (GameAction action : actions) {
            final boolean available = action.isAvailable(world);

            System.out.println("action = " + action.getName());
            System.out.println("available = " + available);

            final TextButton button = buttonsForActions.get(action);

            if (available) buttonArea.add(button);
            else buttonArea.removeActor(button);

            button.setVisible(available);
            /*
            if (!available) {
                button.setColor(0.5f, 0.5f, 0.5f,1);
            }
            else {
                button.setColor(action.getColor());
            }
            */
        }
    }


    protected abstract Actor createContent(Skin skin, World world);
    protected abstract void onUpdate(float deltaTimeSeconds, World world);
    protected abstract void onDispose();

    protected void onActivated() {
        refreshActionButtons();
    }

    protected void onDeactivated() {

    }

}
