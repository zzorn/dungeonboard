package org.dungeonboard.uis;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.dungeonboard.Context;

/**
 *
 */
public abstract class UiBase implements Ui {

    private final Context context;
    private Actor ui;

    protected UiBase(Context context) {
        if (context == null) throw new IllegalArgumentException("Context required, was null");

        this.context = context;
    }

    @Override public final Actor getUi() {
        if (ui == null) {
            ui = createUi(context.getSkin(), context.getTextureAtlas());
        }

        return ui;
    }

    protected final Context getContext() {
        return context;
    }

    protected final Skin getSkin() {
        return context.getSkin();
    }

    protected abstract Actor createUi(Skin skin, TextureAtlas textureAtlas);
}
