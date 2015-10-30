package org.dungeonboard.model;

import org.dungeonboard.actions.ActionRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent whole simulated world.
 */
public class World {

    private final List<Item> items = new ArrayList<Item>();
    private final List<PlayerCharacter> playerCharacters = new ArrayList<PlayerCharacter>();

    private final Party party = new Party();
    private Encounter encounter;

    public World() {
        items.add(new Item("Torch", "torch.png"));
        items.add(new Item("Lantern", "lantern.png"));
        items.add(new Item("Shield", "buckler.png"));

        startEncounter();
    }

    public Encounter getCurrentEncounter() {
        return encounter;
    }

    public Party getParty() {
        return party;
    }

    public List<PlayerCharacter> getPlayerCharacters() {
        return playerCharacters;
    }

    /**
     * @param playerCharacter PlayerCharacter to add.
     */
    public final void addPlayerCharacter(PlayerCharacter playerCharacter) {
        //Check.notNull(playerCharacter, "playerCharacter");
        //Check.notContained(playerCharacter, playerCharacters, "playerCharacters");

        playerCharacters.add(playerCharacter);
        party.addMember(playerCharacter);
    }

    /**
     * @param playerCharacter PlayerCharacter to remove.
     */
    public final void removePlayerCharacter(PlayerCharacter playerCharacter) {
        if (playerCharacters != null) {
            playerCharacters.remove(playerCharacter);
        }
    }





    public void startEncounter() {
        if (encounter != null) throw new IllegalStateException("Encounter already ongoing");

        encounter = new Encounter();
        encounter.setParty(party);
    }

    public void endEncounter() {
        encounter.dispose();
        encounter = null;
    }

    /**
     * Contains all available actions.
     */
    public final ActionRegistry actionRegistry = new ActionRegistry(this);


}
