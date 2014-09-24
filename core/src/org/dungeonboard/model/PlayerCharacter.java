package org.dungeonboard.model;

import com.badlogic.gdx.graphics.Color;

/**
 *
 */
public class PlayerCharacter extends GameCharacter {

    public PlayerCharacter() {
    }

    public PlayerCharacter(String name) {
        super(name);
    }

    public PlayerCharacter(String name, Color color) {
        super(name, color);
    }

    @Override public boolean isPlayerCharacter() {
        return true;
    }
}
