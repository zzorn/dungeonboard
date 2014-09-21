package org.dungeonboard.actions;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.World;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;

/**
 *
 */
public abstract class EncounterActionBase extends GameActionBase implements EncounterAction {

    protected EncounterActionBase(String name) {
        super(name);
    }

    protected EncounterActionBase(String name, Color color) {
        super(name, color);
    }

    @Override public final boolean isAvailable(World world) {
        final Encounter currentEncounter = world.getCurrentEncounter();
        final GameCharacter selectedCharacter = currentEncounter.getSelectedCharacter();

        return availableFor(selectedCharacter,
                            currentEncounter,
                            selectedCharacter != null && currentEncounter.getCurrentCharacter() == selectedCharacter,
                            selectedCharacter != null && selectedCharacter.isTurnUsed(),
                            currentEncounter.isBetweenTurns());
    }

    @Override public final void doAction(World world) {
        final Encounter currentEncounter = world.getCurrentEncounter();
        doAction(world, currentEncounter.getSelectedCharacter(), currentEncounter);

    }
}
