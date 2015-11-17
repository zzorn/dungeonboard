package org.dungeonboard.model;

import com.badlogic.gdx.Preferences;

/**
 *
 */
public interface Saveable {

    /**
     * Save state.
     *
     * @param preferences preferences to save state to.
     * @param prefix prefix to use for all state variables.
     */
    void save(World world, Preferences preferences, String prefix);

    /**
     * Restore state from saved preferences.
     *
     * @param preferences preferences to load state from.
     * @param prefix prefix used for all state variables.
     */
    void load(World world, Preferences preferences, String prefix);
}
