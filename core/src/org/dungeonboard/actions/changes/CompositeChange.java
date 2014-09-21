package org.dungeonboard.actions.changes;

import org.dungeonboard.World;

/**
 * Contains several changes, run at once.
 */
public final class CompositeChange extends WorldChangeBase {

    private final WorldChange[] changes;

    public CompositeChange(String changeDescription, WorldChange ... changes) {
        super(changeDescription);
        this.changes = changes;
    }

    @Override public boolean doAction(World world) {
        // Do all actions in sequence
        boolean canBeUndoed = true;
        for (WorldChange change : changes) {
            canBeUndoed = canBeUndoed && change.doAction(world);
        }

        return canBeUndoed;
    }

    @Override public void undoAction(World world) {
        // Undo all actions in reverse sequence
        for (int i = changes.length - 1; i >= 0; i--) {
            WorldChange change = changes[i];
            change.undoAction(world);
        }
    }
}
