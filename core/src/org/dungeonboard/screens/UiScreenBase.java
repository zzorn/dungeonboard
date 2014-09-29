package org.dungeonboard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.dungeonboard.Context;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.model.World;
import org.dungeonboard.actions.GameAction;
import org.dungeonboard.utils.ActionButton;

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
    private String title;
    private boolean active;
    private Actor actor;
    private Table largeButtonArea;
    private Table firstRowButtonArea;
    private Table secondRowButtonArea;

    private List<GameAction> largeActions = new ArrayList<GameAction>();
    private List<GameAction> firstRowActions = new ArrayList<GameAction>();
    private List<GameAction> secondRowActions = new ArrayList<GameAction>();

    private final Context context;

    private final Map<GameAction, TextButton> buttonsForActions = new HashMap<GameAction, TextButton>();

    protected UiScreenBase(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    public final String getTitle() {
        return title;
    }

    @Override public Actor getActor() {
        if (actor == null) {
            actor = create();
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

            //actor.setVisible(active);
        }
    }

    @Override public final boolean isActive() {
        return active;
    }

    private Actor create() {
        final Skin skin = context.getSkin();
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final float heightPc = height * 0.01f;
        final float widthPc = width * 0.01f;

        // Create root table
        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);


        // Buttons
        Table buttonArea = new Table(skin);
        largeButtonArea = new Table(skin);
        buttonArea.add(largeButtonArea).left();

        Table buttonRows = new Table(skin);
        firstRowButtonArea = new Table(skin);
        secondRowButtonArea = new Table(skin);
        buttonRows.add(firstRowButtonArea).left().top().expand();
        buttonRows.row();
        buttonRows.add(secondRowButtonArea).left().bottom().expand();
        buttonArea.add(buttonRows).left().bottom().expand();


        // Create content
        final Actor content = createContent(getWorld(), getSkin(), getTextureAtlas());
        rootTable.add(content).expand().fill();

        // Add action buttons below content
        rootTable.row();
        rootTable.add(buttonArea).left().padBottom(heightPc * 1).expandX();

        /*
        ScrollPane buttonScroll = new ScrollPane(buttonArea, skin);
        buttonScroll.setColor(Color.BLACK);
        buttonScroll.setFlickScroll(true);
        buttonScroll.setFadeScrollBars(true);
        buttonScroll.setScrollingDisabled(false, true);
        buttonScroll.setupFadeScrollBars(0, 0);
        rootTable.add(buttonScroll).left().padBottom(heightPc * 1).expandX();
        */


        refreshActionButtons();

        return rootTable;
    }

    @Override public final void update(float deltaTimeSeconds) {
        refreshActionButtons();
        onUpdate(deltaTimeSeconds, getWorld());
    }

    @Override public final void dispose() {
        onDispose();
    }

    protected final World getWorld() {
        return context.getWorld();
    }

    protected final Skin getSkin() {
        return context.getSkin();
    }

    protected final TextureAtlas getTextureAtlas() {
        return context.getTextureAtlas();
    }

    protected final Context getContext() {
        return context;
    }

    protected final void addAction(final GameAction gameAction, boolean large, boolean rowTwo) {
        final int height = Gdx.graphics.getHeight();
        final float heightPc = height * 0.01f;

        // Select place
        final List<GameAction> actions;
        if (large) actions = largeActions;
        else if (rowTwo) actions = secondRowActions;
        else actions = firstRowActions;

        // Add
        if (!actions.contains(gameAction)) {
            actions.add(gameAction);
            final TextButton button = new ActionButton("   " + gameAction.getName() + "   ", getSkin(), large);
            button.setColor(gameAction.getColor());
            button.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (gameAction.isAvailable(getWorld())) {
                        gameAction.doAction(getWorld());
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

            //buttonArea.add(button).left().padTop(3).padBottom(3).fillX();

            buttonsForActions.put(gameAction, button);

            refreshActionButtons();
        }
    }

    protected void removeAction(GameAction gameAction) {
        // Remove from lists
        largeActions.remove(gameAction);
        firstRowActions.remove(gameAction);
        secondRowActions.remove(gameAction);

        // Remove from ui
        final TextButton button = buttonsForActions.get(gameAction);
        if (button != null) {
            largeButtonArea.removeActor(button);
            firstRowButtonArea.removeActor(button);
            secondRowButtonArea.removeActor(button);

            // Remove mapping
            buttonsForActions.remove(gameAction);

            refreshActionButtons();
        }
    }

    protected void refreshActionButtons() {
        refreshActionsInArea(largeActions, largeButtonArea, true);
        refreshActionsInArea(firstRowActions, firstRowButtonArea, false);
        refreshActionsInArea(secondRowActions, secondRowButtonArea, false);
    }

    private void refreshActionsInArea(List<GameAction> actions, final Table buttonArea, boolean large) {
        for (GameAction action : actions) {
            final boolean available = action.isAvailable(getWorld());

            final TextButton button = buttonsForActions.get(action);

            if (available) buttonArea.add(button).left();
            else buttonArea.removeActor(button);

            button.setVisible(available);
        }

        buttonArea.layout();
    }


    protected abstract Actor createContent(World world, Skin skin, TextureAtlas textureAtlas);
    protected abstract void onUpdate(float deltaTimeSeconds, World world);
    protected abstract void onDispose();

    protected void onActivated() {
        refreshActionButtons();
    }

    protected void onDeactivated() {

    }

}
