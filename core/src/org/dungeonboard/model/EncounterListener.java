package org.dungeonboard.model;

/**
 *
 */
public interface EncounterListener {

    void onTurnChanged(Encounter encounter);
    void onRoundChanged(Encounter encounter);

    void onCharacterAdded(Encounter encounter, GameCharacter character);
    void onCharacterRemoved(Encounter encounter, GameCharacter character);

    void onSelectionChanged(Encounter encounter, GameCharacter selectedCharacter);
}
