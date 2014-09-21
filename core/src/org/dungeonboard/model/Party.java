package org.dungeonboard.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Group of players on an adventure.
 */
public class Party  {

    private final List<PlayerCharacter> playerCharacters = new ArrayList<PlayerCharacter>();

    public List<PlayerCharacter> getPlayerCharacters() {
        return playerCharacters;
    }
}
