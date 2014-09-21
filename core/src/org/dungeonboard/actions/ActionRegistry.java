package org.dungeonboard.actions;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.World;
import org.dungeonboard.actions.changes.WorldChange;
import org.dungeonboard.actions.changes.WorldChangeBase;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * Available actions
 */
public final class ActionRegistry {

    private final World world;
    private final List<EncounterAction> actions = new ArrayList<EncounterAction>();

    public ActionRegistry(World world) {
        this.world = world;
    }

    public void addAction(EncounterAction action) {
        actions.add(action);
    }

    public List<EncounterAction> getActions() {
        return actions;
    }

    public void setupActions() {

        addAction(new EncounterActionBase("Done", Color.GREEN) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return hasTurn;
            }

            @Override protected WorldChange createChange(GameCharacter character, Encounter encounter) {
                final long encounterId = encounter.getId();
                return new WorldChangeBase("finish turn") {
                    @Override public boolean doAction(World world) {
                        world.encounters.get(encounterId).stepToNextTurn();
                        return true;
                    }

                    @Override public void undoAction(World world) {
                        // TODO: Reverse all logic in stepToNext turn, or save world snapshots...  Not worth it, undo & redo just adds way way too much implementation effort.

                    }
                };
            }
        });



    }
}
