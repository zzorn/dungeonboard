package org.dungeonboard.model;

/**
 * One or more NPC:s.
 */
public class NonPlayerCharacters extends GameCharacter {
    @Override public boolean isPlayerCharacter() {
        return false;
    }
}
