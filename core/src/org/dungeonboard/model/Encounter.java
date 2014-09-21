package org.dungeonboard.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Encapsulates an encounter.
 */
public class Encounter  {

    private List<GameCharacter> characters = new ArrayList<GameCharacter>();

    private int roundNumber = 0;
    private boolean extraTurnInitiativeDropUsed = false;
    private GameCharacter currentCharacter = null;
    private GameCharacter selectedCharacter = null;

    private final Comparator<GameCharacter> characterListComparator = new Comparator<GameCharacter>() {
        @Override public int compare(GameCharacter o1, GameCharacter o2) {
            if (o1.getInitiative() < o2.getInitiative()) return 1;
            else if (o1.getInitiative() > o2.getInitiative()) return -1;
            else return 0;
        }
    };

    // TODO: Make transient
    private final List<EncounterListener> listeners = new ArrayList<EncounterListener>();
    private final List<EncounterListener> tempListeners = new ArrayList<EncounterListener>();

    public List<GameCharacter> getCharacters() {
        return characters;
    }

    public void addCharacter(GameCharacter character) {
        if (!characters.contains(character)) {
            characters.add(character);

            // TODO: Fix Kludge to avoid concurrent edition.
            tempListeners.clear();
            tempListeners.addAll(listeners);
            for (EncounterListener listener : tempListeners) {
                listener.onCharacterAdded(this, character);
            }
            tempListeners.clear();
        }
    }

    public void removeCharacter(GameCharacter character) {
        if (characters.contains(character)) {
            characters.remove(character);

            if (currentCharacter == character) {
                stepToNextTurn();
            }

            if (selectedCharacter == character) {
                selectedCharacter = currentCharacter;
            }

            // TODO: Fix Kludge to avoid concurrent edition.
            tempListeners.clear();
            tempListeners.addAll(listeners);
            for (EncounterListener listener : tempListeners) {
                listener.onCharacterRemoved(this, character);
            }
            tempListeners.clear();
        }
    }

    public boolean isExtraTurnInitiativeDropUsed() {
        return extraTurnInitiativeDropUsed;
    }

    public void setExtraTurnInitiativeDropUsed(boolean extraTurnInitiativeDropUsed) {
        this.extraTurnInitiativeDropUsed = extraTurnInitiativeDropUsed;
    }

    public GameCharacter getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(GameCharacter selectedCharacter) {
        if (characters.contains(selectedCharacter)) {
            this.selectedCharacter = selectedCharacter;
        }
        else {
            this.selectedCharacter = null;
        }

        for (EncounterListener listener : listeners) {
            listener.onSelectionChanged(this, this.selectedCharacter);
        }
    }

    /**
     * Add all player characters from the party to the encounter.
     */
    public void addParty(Party party) {
        for (PlayerCharacter playerCharacter : party.getPlayerCharacters()) {
            addCharacter(playerCharacter);
        }
    }

    public GameCharacter getCurrentCharacter() {
        return currentCharacter;
    }

    public int getRoundNumber() {
        return roundNumber;
    }


    /**
     * Set the specified character as the currently active, even if they do not have the next initiative (used by ready actions).
     */
    public void setCurrentCharacter(GameCharacter character) {
        currentCharacter = character;
        currentCharacter.onTurn();

        notifyTurnChanged();
    }


    public void stepToNextTurn() {
        boolean roundChanged = false;
        if (currentCharacter == null) {
            // We were at between turns
            roundNumber++;

            // Wake up any temporary disabled that should be woken up and do other round upkeep
            for (GameCharacter character : characters) {
                character.onNewRound();
            }

            // Zero some flags
            extraTurnInitiativeDropUsed = false;

            roundChanged = true;
        }

        // Find next character with highest initiative and unused turn and not disabled, and activate that
        GameCharacter nextCharacter = getNextCharacterInTurn();

        currentCharacter = nextCharacter;
        if (currentCharacter != null) {
            // Activate char
            currentCharacter.onTurn();
        }
        else {
            // Between turns

        }

        // Update selected character to the current character
        setSelectedCharacter(currentCharacter);

        // Resort character list
        Collections.sort(characters, characterListComparator);

        if (roundChanged) notifyRoundChanged();
        notifyTurnChanged();
    }

    private GameCharacter getNextCharacterInTurn() {
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
        return nextCharacter;
    }

    public boolean canGetFreeTurn(GameCharacter character, int pointsAboveRequired) {
        final GameCharacter highest = findHighestNonDisabledExcept(null);
        final GameCharacter nextHighest = findHighestNonDisabledExcept(highest);
        return highest == character &&
               (nextHighest == null || character.getInitiative() >= nextHighest.getInitiative() + pointsAboveRequired);
    }

    private GameCharacter findHighestNonDisabledExcept(GameCharacter exceptThis) {
        int highestInitiative = -9999;
        GameCharacter foundChar = null;
        for (GameCharacter character : characters) {
            final int characterInitiative = character.getInitiative();
            if (character != exceptThis &&
                !character.isDisabled() &&
                characterInitiative > highestInitiative) {
                foundChar = character;
                highestInitiative = characterInitiative;
            }
        }

        return foundChar;
    }

    public boolean isBetweenTurns() {
        return getCurrentCharacter() == null;
    }

    public void addListener(EncounterListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EncounterListener listener) {
        listeners.remove(listener);
    }

    private void notifyTurnChanged() {
        for (EncounterListener listener : listeners) {
            listener.onTurnChanged(this);
        }
    }
    private void notifyRoundChanged() {
        for (EncounterListener listener : listeners) {
            listener.onRoundChanged(this);
        }
    }

}
