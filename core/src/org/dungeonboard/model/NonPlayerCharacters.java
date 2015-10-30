package org.dungeonboard.model;

import com.badlogic.gdx.graphics.Color;

/**
 * One or more NPC:s.
 */
public class NonPlayerCharacters extends GameCharacter {

    public NonPlayerCharacters() {
    }

    public NonPlayerCharacters(String name) {
        this(name, new Color(0.2f, 0.9f, 0.2f, 1f));
    }

    public NonPlayerCharacters(String name, Color color) {
        super(name, color);

        setIcon("characters/skull");
    }

    @Override public boolean isPlayerCharacter() {
        return false;
    }
}
