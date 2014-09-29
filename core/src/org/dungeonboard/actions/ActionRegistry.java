package org.dungeonboard.actions;

import org.dungeonboard.model.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Available actions
 */
public final class ActionRegistry {

    private final World world;
    private final List<EncounterAction> actions = new ArrayList<EncounterAction>();

    public ActionRegistry(World world) {
        this.world = world;
    }

    public void addAction(EncounterAction action) {
        actions.add(action);
    }

    public List<EncounterAction> getActions() {
        return actions;
    }

}
