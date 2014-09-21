package org.dungeonboard.actions.changes;

import org.dungeonboard.World;

/**
 * A change to the game world.  Created by an action.
 * Can normally be undone and redone.
 *
 * May store undo data in itself when doAction is called, and redo data when undoAction is called.
 */
public interface WorldChange {

    /**
     * @return short human readable description of the change.  E.g. "delete character foo".
     */
    String getChangeDescription();

    /**
     * Applies this change to the world.
     * Returns true if this change can be reversed, or false if this change can not be reversed.
     */
    boolean doAction(World world);

    /**
     * Undoes this change.
     */
    void undoAction(World world);

}
