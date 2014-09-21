package org.dungeonboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 */
public abstract class UiScreenBase implements UiScreen {

    private final World world;
    private String title;
    private boolean active;
    private Skin skin;

    protected UiScreenBase(World world, String title) {
        this.world = world;
        this.title = title;
    }

    public final String getTitle() {
        return title;
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

        // Add undo & redo buttons
        // TODO
        final TextButton undo = new TextButton("Undo", skin);
        undo.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {

            }
        });
        titleRow.add(undo);


        final TextButton redo = new TextButton("Redo", skin);
        titleRow.add(redo);

        // Add titlebar
        // TODO

        // Add screen change buttons
        // TODO




        final Actor content = createContent(this.skin, world);

        return rootTable;
    }

    @Override public final void update(float deltaTimeSeconds) {
        onUpdate(deltaTimeSeconds, world);
    }

    @Override public final void dispose() {
        onDispose();
    }

    protected abstract Actor createContent(Skin skin, World world);
    protected abstract void onUpdate(float deltaTimeSeconds, World world);
    protected abstract void onDispose();

    protected void onActivated() {

    }

    protected void onDeactivated() {

    }

}
