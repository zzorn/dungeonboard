package org.dungeonboard.model;

import org.dungeonboard.actions.ActionRegistry;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;
import org.dungeonboard.model.Party;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent whole simulated world.
 */
public class World {

    private final Party party = new Party();
    private Encounter encounter;

    public World() {
        startEncounter();
    }

    public Encounter getCurrentEncounter() {
        return encounter;
    }

    public Party getParty() {
        return party;
    }

    public void startEncounter() {
        if (encounter != null) throw new IllegalStateException("Encounter already ongoing");

        encounter = new Encounter();
        encounter.setParty(party);
    }

    public void endEncounter() {
        encounter.dispose();
        encounter = null;
    }

    /**
     * Contains all available actions.
     */
    public final ActionRegistry actionRegistry = new ActionRegistry(this);


}
