package org.dungeonboard.storage;

/**
 * Any game entity.  Has an id.
 */
public abstract class Entity {

    private long id;

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }
}
