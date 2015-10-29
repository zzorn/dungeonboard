package org.dungeonboard.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Tuning the button class a bit.
 */
public class ActionButton extends TextButton {

    private static final float BUTTON_HEIGHT_SCALE = 1.5f;

    private boolean large = false;

    public ActionButton(String text, Skin skin) {
        super(text, skin);
    }

    public ActionButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public ActionButton(String text, TextButtonStyle style) {
        super(text, style);
    }

    public ActionButton(String text, Skin skin, boolean large) {
        super(text, skin);
        this.large = large;
    }

    public ActionButton(String text, Skin skin, String styleName, boolean large) {
        super(text, skin, styleName);
        this.large = large;
    }

    public ActionButton(String text, TextButtonStyle style, boolean large) {
        super(text, style);
        this.large = large;
    }

    public boolean isLarge() {
        return large;
    }

    public void setLarge(boolean large) {
        this.large = large;
    }

    @Override public float getPrefHeight() {
        final float prefHeight = super.getPrefHeight() * BUTTON_HEIGHT_SCALE;

        if (large && !isMultiRow()) {
            return prefHeight * 2;
        }
        else {
            return prefHeight;
        }
    }

    private boolean isMultiRow() {
        return getText().toString().contains("\n");
    }
}
