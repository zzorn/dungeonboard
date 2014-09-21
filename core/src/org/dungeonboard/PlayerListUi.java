package org.dungeonboard;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import org.dungeonboard.model.Party;

/**
 *
 */
public class PlayerListUi implements UiScreen {

    private final Party party;

    public PlayerListUi(Party party) {
        this.party = party;
    }

    @Override public Actor create() {
        return new ScrollPane();
    }

    @Override public void update(float deltaTimeSeconds) {
        // TODO: Implement

    }

    @Override public void dispose() {
        // TODO: Implement

    }
}
