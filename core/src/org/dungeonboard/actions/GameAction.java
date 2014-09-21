package org.dungeonboard.actions;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;
import org.dungeonboard.World;

/**
 * Generic action, supports undo.
 */
public interface GameAction {

    /**
     * @return name of the action.
     */
    String getName();

    /**
     * @return color of the action button.
     */
    Color getColor();

    /**
     * @return true if the action can be done with the current world state.
     */
    boolean isAvailable(World world);

    void doAction(World world);
}
