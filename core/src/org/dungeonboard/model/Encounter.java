package org.dungeonboard.model;

import org.dungeonboard.storage.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates an encounter.
 */
public class Encounter extends Entity {

    private List<GameCharacter> characters = new ArrayList<GameCharacter>();
    private int roundNumber = 0;
    private GameCharacter currentCharacter = null;

    public List<GameCharacter> getCharacters() {
        return characters;
    }

    public void addCharacter(GameCharacter character) {
        characters.add(character);
    }

    public void removeCharacter(GameCharacter character) {
        characters.remove(character);
    }

    /**
     * Add all player characters from the party to the encounter.
     */
    public void addParty(Party party) {
        characters.addAll(party.getPlayerCharacters());
    }

    public GameCharacter getCurrentCharacter() {
        return currentCharacter;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void stepToNextTurn() {
        if (currentCharacter == null) {
            // We were at between turns
            roundNumber++;
        }

        // If this is the first turn of the round, wake up any temporary disabled that should be woken up and do other round upkeep
        if (currentCharacter == null) {
            for (GameCharacter character : characters) {
                character.onNewRound();
            }
        }

        // Find next character with highest initiative and unused turn and not disabled, and activate that
        int highestInitiative = -9999;
        GameCharacter nextCharacter = null;
        for (GameCharacter character : characters) {
            final int characterInitiative = character.getInitiative();
            if (!character.isDisabled() &&
                !character.isTurnUsed() &&
                characterInitiative > highestInitiative) {
                nextCharacter = character;
                highestInitiative = characterInitiative;
            }
        }

        currentCharacter = nextCharacter;
        if (currentCharacter != null) {
            // Activate char
            currentCharacter.onTurn();
        }
        else {
            // Between turns

        }
    }
}
