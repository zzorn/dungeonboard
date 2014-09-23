package org.dungeonboard.model;

/**
 *
 */
public interface CharacterListener {

    void onChanged(GameCharacter character);

    void onInitiativeChanged(GameCharacter character);
}
