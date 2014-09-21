package org.dungeonboard;

import org.dungeonboard.actions.ActionRegistry;
import org.dungeonboard.actions.changes.ChangeQueue;
import org.dungeonboard.actions.changes.WorldChange;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;
import org.dungeonboard.model.Party;
import org.dungeonboard.storage.Storage;

/**
 * Represent whole simulated world.
 */
public class World {

    // The world state is contained in these storages.
    // TODO: Some way to handle references to other entities, so that serializing everything is easy
    public final Storage<GameCharacter> characters = new Storage<GameCharacter>(GameCharacter.class);
    public final Storage<Encounter> encounters = new Storage<Encounter>(Encounter.class);
    public final Storage<Party> parties = new Storage<Party>(Party.class);

    /**
     * Contains all available actions.
     */
    public final ActionRegistry actionRegistry = new ActionRegistry();

    /**
     * All changes are applied through this.  Also keeps track of the undo and redo queues.
     */
    private final ChangeQueue changeQueue = new ChangeQueue(this);

    /**
     * Apply a change to the world.
     */
    public void applyChange(WorldChange change) {
        changeQueue.applyChange(change);
    }

    /**
     * Undoes the last change to the world.
     */
    public void undo() {
        changeQueue.undo();
    }

    /**
     * Redoes the last undoed change to the world.
     */
    public void redo() {
        changeQueue.redo();
    }

    public boolean canUndo() {
        return changeQueue.canUndo();
    }

    public boolean canRedo() {
        return changeQueue.canRedo();
    }

}
