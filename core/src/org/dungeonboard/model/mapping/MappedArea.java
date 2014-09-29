package org.dungeonboard.model.mapping;

import com.badlogic.gdx.utils.Array;

/**
 * Represents some mapped area.
 */
// TODO: Store as sequence of edits, allow undo or change of a specific step, with subsequent steps playing out
// TODO  relative to the change (e.g. adjust angle at some point, or length of some corridor)?
public class MappedArea {

    private final Dungeon dungeon;
    private final int level;
    private final String name;

    private final Array<MappedAreaListener> listeners = new Array<MappedAreaListener>(3);

    public MappedArea(Dungeon dungeon, int level, String name) {
        this.dungeon = dungeon;
        this.level = level;
        this.name = name;
    }

    public final void addListener(MappedAreaListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(MappedAreaListener listener) {
        listeners.removeValue(listener, true);
    }


    // TODO: Call this when changed
    protected void notifyMappedAreaChanged() {
        for (MappedAreaListener listener : listeners) {
            listener.onMappedAreaChanged(this);
        }
    }

}
