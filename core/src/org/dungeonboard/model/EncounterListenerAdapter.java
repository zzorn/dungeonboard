package org.dungeonboard.model;

/**
 *
 */
public class EncounterListenerAdapter implements EncounterListener {
    @Override public void onTurnChanged(Encounter encounter) {
    }

    @Override public void onRoundChanged(Encounter encounter) {
    }

    @Override public void onCharacterAdded(Encounter encounter, GameCharacter character) {
    }

    @Override public void onCharacterRemoved(Encounter encounter, GameCharacter character) {
    }

    @Override public void onSelectionChanged(Encounter encounter, GameCharacter selectedCharacter) {
    }

    @Override public void onInitiativeChanged(Encounter encounter) {
    }
}
