package org.dungeonboard.actions.changes;

import org.dungeonboard.World;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Queues actions to allow them to be undone or redone.
 */
public final class ChangeQueue {

    private final World world;
    private final Deque<WorldChange> undoQueue = new ArrayDeque<WorldChange>();
    private final Deque<WorldChange> redoQueue = new ArrayDeque<WorldChange>();

    public ChangeQueue(World world) {
        this.world = world;
    }

    /**
     * Applies a change to the world
     */
    public void applyChange(WorldChange change) {

        // Apply change to the world
        final boolean canBeUndone = change.doAction(world);

        if (canBeUndone) {
            // Store it if it can be undone
            undoQueue.add(change);
        }

        // Clear redo queue as it is no longer applicable
        redoQueue.clear();
    }

    /**
     * Undoes most recent change.
     */
    public void undo() {
        if (!canUndo()) throw new IllegalStateException("Can not undo");

        final WorldChange change = undoQueue.pop();
        change.undoAction(world);
        redoQueue.push(change);
    }

    /**
     * Redoes most recently undone change.
     */
    public void redo() {
        if (!canRedo()) throw new IllegalStateException("Can not redo");

        final WorldChange change = redoQueue.pop();
        change.doAction(world);
        undoQueue.push(change);
    }

    /**
     * @return true if undo can be called.
     */
    public boolean canUndo() {
        return !undoQueue.isEmpty();
    }

    /**
     * @return true if redo can be called.
     */
    public boolean canRedo() {
        return !redoQueue.isEmpty();
    }
}

