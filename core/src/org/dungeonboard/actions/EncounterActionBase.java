package org.dungeonboard.actions;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.World;
import org.dungeonboard.actions.changes.WorldChange;
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

    @Override public final void doAction(World world, GameCharacter character, Encounter encounter) {
        final WorldChange change = createChange(character, encounter);
        world.applyChange(change);
    }

    protected abstract WorldChange createChange(GameCharacter character, Encounter encounter);
}
