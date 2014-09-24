package org.dungeonboard.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.dungeonboard.model.GameCharacter;

/**
 *
 */
public class CharacterButton extends ImageButton {

    private final TextureAtlas textureAtlas;
    private GameCharacter character;
    private Drawable drawable;

    private float width = 32;
    private float height = 32;

    public CharacterButton(Skin skin, GameCharacter character, TextureAtlas textureAtlas, float size) {
        super(skin);
        this.textureAtlas = textureAtlas;

        setCharacter(character);
        setSize(size);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(float size) {
        setSize(size, size);
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setCharacter(GameCharacter character) {
        this.character = character;

        if (character != null) {
            final TextureAtlas.AtlasRegion icon = textureAtlas.findRegion("icons/" + character.getIcon());

            if (icon != null) {
                drawable = new TextureRegionDrawable(icon);
                final ImageButtonStyle style = new ImageButtonStyle(null, null, null, drawable, null, null);
                setStyle(style);
            }

            getImage().setColor(character.getColor());
        }

    }



    @Override public float getPrefWidth() {
        return width;
    }

    @Override public float getPrefHeight() {
        return height;
    }
}
