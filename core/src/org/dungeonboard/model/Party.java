package org.dungeonboard.model;


import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of players on an adventure.
 */
public class Party implements Saveable {

    private static final String PARTY_SIZE = ".partySize";
    private static final String PARTY_MEMBER = ".partyMember.";
    private final List<PlayerCharacter> partyMembers = new ArrayList<PlayerCharacter>();

    private final Array<PartyListener> listeners = new Array<PartyListener>();

    public List<PlayerCharacter> getPartyMembers() {
        return partyMembers;
    }

    public void addMember(PlayerCharacter character) {
        partyMembers.add(character);

        for (PartyListener listener : listeners) {
            listener.onMemberAdded(character);
        }
    }

    public void removeMember(PlayerCharacter character) {
        partyMembers.remove(character);
        notifyMemberRemoved(character);
    }

    public void clearParty() {
        for (PlayerCharacter partyMember : partyMembers) {
            notifyMemberRemoved(partyMember);
        }
        partyMembers.clear();
    }

    private void notifyMemberRemoved(PlayerCharacter character) {
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

    @Override public void save(World world, Preferences preferences, String prefix) {
        preferences.putInteger(prefix + PARTY_SIZE, partyMembers.size());
        for (int i = 0; i < partyMembers.size(); i++) {
            preferences.putInteger(prefix + PARTY_MEMBER + i,
                                   world.getPlayerCharacterId(partyMembers.get(i)));
        }
    }

    @Override public void load(World world, Preferences preferences, String prefix) {
        clearParty();
        final int partySize = preferences.getInteger(prefix + PARTY_SIZE, 0);
        for (int i = 0; i < partySize; i++) {
            final int memberId = preferences.getInteger(prefix + PARTY_MEMBER + i);
            final PlayerCharacter playerCharacter = world.getPlayerCharacter(memberId);
            addMember(playerCharacter);
        }
    }
}
