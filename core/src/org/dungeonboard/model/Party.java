package org.dungeonboard.model;


import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of players on an adventure.
 */
public class Party  {

    private final List<PlayerCharacter> playerCharacters = new ArrayList<PlayerCharacter>();

    private final Array<PartyListener> listeners = new Array<PartyListener>();

    public List<PlayerCharacter> getPlayerCharacters() {
        return playerCharacters;
    }

    public void addMember(PlayerCharacter character) {
        playerCharacters.add(character);

        for (PartyListener listener : listeners) {
            listener.onMemberAdded(character);
        }
    }

    public void removeMember(PlayerCharacter character) {
        playerCharacters.remove(character);

        for (PartyListener listener : listeners) {
            listener.onMemberRemoved(character);
        }
    }

    public final void addListener(PartyListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(PartyListener listener) {
        listeners.removeValue(listener, true);
    }
}
