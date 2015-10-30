package org.dungeonboard;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.dungeonboard.model.World;
import org.dungeonboard.screens.CharacterEditorScreen;
import org.dungeonboard.screens.UiScreen;

/**
 *
 */
public interface Context {
    void setScreen(UiScreen screen);

    Skin getSkin();

    Stage getStage();

    World getWorld();

    TextureAtlas getTextureAtlas();

    /**
     * Switch to the next or previous ui screen.
     * @param toNextOne true if next, false if previous.
     */
    void switchScreen(boolean toNextOne);

    /**
     * Switch to the UI screen that we came to this screen from.
     */
    void switchScreenBack();

    UiScreen getCurrentScreen();

    CharacterEditorScreen getCharacterEditScreen();
}
