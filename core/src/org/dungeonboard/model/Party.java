package org.dungeonboard.model;

import org.dungeonboard.storage.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of players on an adventure.
 */
public class Party extends Entity {

    private final List<PlayerCharacter> playerCharacters = new ArrayList<PlayerCharacter>();

    public List<PlayerCharacter> getPlayerCharacters() {
        return playerCharacters;
    }
}
