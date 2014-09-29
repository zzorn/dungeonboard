package org.dungeonboard.model;

/**
 * Listens to changes in the party.
 */
public interface PartyListener {

    void onMemberAdded(PlayerCharacter character);
    void onMemberRemoved(PlayerCharacter character);

}
