package org.dungeonboard.actions.changes;

/**
 *
 */
public abstract class WorldChangeBase implements WorldChange {

    private final String changeDescription;

    protected WorldChangeBase(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public final String getChangeDescription() {
        return changeDescription;
    }
}
