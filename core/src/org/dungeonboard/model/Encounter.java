package org.dungeonboard.model;


import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Encapsulates an encounter.
 */
public class Encounter implements Saveable {

    private static final String ROUND_NUMBER = ".roundNumber";
    private static final String EXTRA_TURN_INITIATIVE_DROP_USED = ".extraTurnInitiativeDropUsed";
    private static final String CURRENT_CHARACTER = ".currentCharacter";
    private static final String SELECTED_CHARACTER = ".selectedCharacter";
    private static final String NUMBER_OF_CHARACTERS = ".numberOfCharacters";
    private static final String PLAYER_CHARACTER = ".playerCharacter.";
    private static final String NON_PLAYER_CHARACTER = ".nonPlayerCharacter.";
    private List<GameCharacter> characters = new ArrayList<GameCharacter>();
    private int roundNumber = 0;

    private boolean extraTurnInitiativeDropUsed = false;
    private GameCharacter currentCharacter = null;
    private GameCharacter selectedCharacter = null;
    private Party party;

    private final PartyListener partyListener = new PartyListener() {
        @Override public void onMemberAdded(PlayerCharacter character) {
            addCharacter(character);
        }

        @Override public void onMemberRemoved(PlayerCharacter character) {
            removeCharacter(character);
        }
    };

    private final Comparator<GameCharacter> characterListComparator = new Comparator<GameCharacter>() {
        @Override public int compare(GameCharacter o1, GameCharacter o2) {
            if (o1.getInitiative() < o2.getInitiative()) return 1;
            else if (o1.getInitiative() > o2.getInitiative()) return -1;
            else {
                int i = 0;
                if (o1.isPlayerCharacter()) i--;
                if (o2.isPlayerCharacter()) i++;
                return i;
            }
        }
    };

    // TODO: Make transient
    private final List<EncounterListener> listeners = new ArrayList<EncounterListener>();
    private final List<EncounterListener> tempListeners = new ArrayList<EncounterListener>();

    private final CharacterListener characterListener = new CharacterListener() {
        @Override public void onChanged(GameCharacter character) {
        }

        @Override public void onInitiativeChanged(GameCharacter character) {
            sortCharacters();
            notifyInitiativeChanged();
        }
    };

    public List<GameCharacter> getCharacters() {
        return characters;
    }

    public void addCharacter(GameCharacter character) {
        if (!characters.contains(character)) {
            characters.add(character);
            character.addListener(characterListener);

            // TODO: Fix Kludge to avoid concurrent edition.
            tempListeners.clear();
            tempListeners.addAll(listeners);
            for (EncounterListener listener : tempListeners) {
                listener.onCharacterAdded(this, character);
            }
            tempListeners.clear();

            sortCharacters();
            notifyTurnChanged();
        }
    }

    public void removeCharacter(GameCharacter character) {
        if (characters.contains(character)) {
            character.removeListener(characterListener);
            characters.remove(character);

            if (selectedCharacter == character) {
                selectedCharacter = null;
            }

            if (currentCharacter == character) {
                stepToNextTurn(false);
            }

            notifyCharacterRemoved(character);
        }
    }

    private void notifyCharacterRemoved(GameCharacter character) {
        // TODO: Fix Kludge to avoid concurrent edition.
        tempListeners.clear();
        tempListeners.addAll(listeners);
        for (EncounterListener listener : tempListeners) {
            listener.onCharacterRemoved(this, character);
        }
        tempListeners.clear();
    }

    public Party getParty() {
        return party;
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
     * Set the party participating in this encounter.  All characters from it are added.
     */
    public void setParty(Party party) {
        if (this.party != null) {
            this.party.removeListener(partyListener);

            for (PlayerCharacter playerCharacter : this.party.getPartyMembers()) {
                removeCharacter(playerCharacter);
            }
        }

        this.party = party;

        if (this.party != null) {
            for (PlayerCharacter playerCharacter : this.party.getPartyMembers()) {
                addCharacter(playerCharacter);
            }

            this.party.addListener(partyListener);
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


    public void stepToNextTurn(boolean allowSameCharacter) {
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
        currentCharacter = getNextCharacter(allowSameCharacter);
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
        sortCharacters();

        if (roundChanged) notifyRoundChanged();
        notifyTurnChanged();
    }

    private void sortCharacters() {
        Collections.sort(characters, characterListComparator);
    }

    /**
     * @param allowSameCharacter true if the same character can get the turn again if he has an unused turn and highest initiative
     * @return character whose turn it will be next
     */
    public GameCharacter getNextCharacter(boolean allowSameCharacter) {
        int highestInitiative = -9999;
        GameCharacter nextCharacter = null;
        for (GameCharacter character : characters) {
            final int characterInitiative = character.getInitiative();
            if ((character != currentCharacter || allowSameCharacter) &&
                !character.isDisabled() &&
                !character.isTurnUsed() &&
                characterInitiative > highestInitiative) {
                nextCharacter = character;
                highestInitiative = characterInitiative;
            }
        }
        return nextCharacter;
    }

    public boolean canGetFreeTurn(GameCharacter character, int pointsToDropForFreeTurn) {
        final GameCharacter highest = findHighestNonDisabledExcept(null);
        final GameCharacter nextHighest = findHighestNonDisabledExcept(highest);
        return highest == character &&
               (nextHighest == null || character.getInitiative() > nextHighest.getInitiative());
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
    private void notifyInitiativeChanged() {
        for (EncounterListener listener : listeners) {
            listener.onInitiativeChanged(this);
        }
    }

    public void reset() {
        roundNumber = 0;
        currentCharacter = null;
        selectedCharacter = null;
        extraTurnInitiativeDropUsed = false;

        // Reset characters
        for (GameCharacter character : characters) {
            character.reset();
        }
        sortCharacters();

        notifyTurnChanged();
        notifyRoundChanged();
    }

    public boolean severalWithSameInitiativeAndTurnNotDone() {
        if (currentCharacter == null || currentCharacter.isTurnUsed()) {
            return false;
        }
        else {
            final GameCharacter nextCharacter = getNextCharacter(false);
            if (nextCharacter == null || nextCharacter.isTurnUsed()) {
                return false;
            }
            else {
                return currentCharacter.getInitiative() == nextCharacter.getInitiative();
            }
        }
    }

    public void dispose() {
        setParty(null);
    }

    @Override public void save(World world, Preferences preferences, String prefix) {
        preferences.putInteger(prefix + ROUND_NUMBER, roundNumber);
        preferences.putBoolean(prefix + EXTRA_TURN_INITIATIVE_DROP_USED, extraTurnInitiativeDropUsed);

        // Save characters
        preferences.putInteger(prefix + NUMBER_OF_CHARACTERS, characters.size());
        for (int i = 0; i < characters.size(); i++) {
            GameCharacter character = characters.get(i);

            if (character instanceof PlayerCharacter) {
                preferences.putInteger(prefix + PLAYER_CHARACTER + i, world.getPlayerCharacterId((PlayerCharacter) character));
            }
            else {
                character.save(world, preferences, prefix + NON_PLAYER_CHARACTER + i);
            }
        }

        preferences.putInteger(prefix + CURRENT_CHARACTER, characters.indexOf(currentCharacter));
        preferences.putInteger(prefix + SELECTED_CHARACTER, characters.indexOf(selectedCharacter));
    }

    @Override public void load(World world, Preferences preferences, String prefix) {
        roundNumber = preferences.getInteger(prefix + ROUND_NUMBER, 0);
        extraTurnInitiativeDropUsed = preferences.getBoolean(prefix + EXTRA_TURN_INITIATIVE_DROP_USED, false);

        // Load characters
        removeAllCharacters();
        final int numberOfCharacters = preferences.getInteger(prefix + NUMBER_OF_CHARACTERS, 0);
        for (int i = 0; i < numberOfCharacters; i++) {
            final int playerId = preferences.getInteger(prefix + PLAYER_CHARACTER + i, -1);
            if (playerId >= 0) {
                // Get PC
                addCharacter(world.getPlayerCharacter(playerId));
            }
            else {
                // Load NPC
                final NonPlayerCharacters nonPlayerCharacter = new NonPlayerCharacters();
                nonPlayerCharacter.load(world, preferences, prefix + NON_PLAYER_CHARACTER + i);
                addCharacter(nonPlayerCharacter);
            }
        }

        // Get current and selected character
        final int currentCharacterIndex = preferences.getInteger(prefix + CURRENT_CHARACTER, -1);
        final int selectedCharacterIndex = preferences.getInteger(prefix + SELECTED_CHARACTER, -1);
        currentCharacter = currentCharacterIndex < 0 ? null : characters.get(currentCharacterIndex);
        selectedCharacter = selectedCharacterIndex < 0 ? null : characters.get(selectedCharacterIndex);

        // Notify listeners
        for (EncounterListener listener : listeners) {
            listener.onTurnChanged(this);
            listener.onRoundChanged(this);
        }
    }

    private void removeAllCharacters() {
        for (GameCharacter character : characters) {
            character.removeListener(characterListener);
            notifyCharacterRemoved(character);
        }
        characters.clear();
        selectedCharacter = null;
        currentCharacter = null;

    }

}
