package org.dungeonboard.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.model.CharacterListener;
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

    private final CharacterListener characterListener = new CharacterListener() {
        @Override public void onChanged(GameCharacter character) {
            updateUi(character);
        }

        @Override public void onInitiativeChanged(GameCharacter character) {
        }
    };

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
        if (this.character != null) {
            this.character.removeListener(characterListener);
        }

        this.character = character;

        if (this.character != null) {
            this.character.addListener(characterListener);
            updateUi(this.character);
        }
    }

    private void updateUi(GameCharacter character) {
        // Update icon
        final TextureAtlas.AtlasRegion icon = StyleSettings.getIcon(character.getIcon());
        if (icon != null) {
            drawable = new TextureRegionDrawable(icon);
            final Drawable drawable = this.drawable;
            final CharacterButton characterButton = this;
            setImageButtonImage(characterButton, drawable);
        }

        // Update color
        getImage().setColor(character.getColor());
    }

    public static void setImageButtonImage(ImageButton imageButton, Drawable image) {
        imageButton.setStyle(new ImageButtonStyle(null, null, null, image, null, null));
    }


    @Override public float getPrefWidth() {
        return width;
    }

    @Override public float getPrefHeight() {
        return height;
    }
}
