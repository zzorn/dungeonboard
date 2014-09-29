package org.dungeonboard.actions;

import org.dungeonboard.model.World;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;

/**
 * Some possible action for a character during an encounter.
 */
public interface EncounterAction extends GameAction {

    /**
     * @param character character to check availability for.
     * @param encounter the current encounter.
     * @param hasTurn true if the character is currently doing its turn.
     * @param turnUsed true if the character has already used his turn this round.
     * @param betweenRounds true if it is currently between rounds.
     * @return true if the action is available under the specified conditions.
     */
    boolean availableFor(GameCharacter character, Encounter encounter, boolean hasTurn, boolean turnUsed, boolean betweenRounds);

    /**
     * Executes the action with the specified character in the specified encounter.
     */
    void doAction(World world, GameCharacter character, Encounter encounter);

}
