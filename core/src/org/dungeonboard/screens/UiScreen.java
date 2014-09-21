package org.dungeonboard.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.dungeonboard.actions.GameAction;

import java.util.List;

/**
 *
 */
public interface UiScreen {

    String getTitle();

    Actor getActor(Skin skin);

    Actor create(Skin skin);

    void update(float deltaTimeSeconds);

    /**
     * Whether the screen is active and visible.
     */
    void setActive(boolean active);

    boolean isActive();

    void dispose();

    /**
     *
     * @return generic actions available in this secreen.
     */
    List<GameAction> getAvailableActions();

}
