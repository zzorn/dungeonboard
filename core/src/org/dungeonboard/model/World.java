package org.dungeonboard.model;

import com.badlogic.gdx.Preferences;
import org.dungeonboard.actions.ActionRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent whole simulated world.
 */
public class World implements Saveable {

    private static final String PREFERENCES_NAME = "Dungeonboard_world";
    private static final String NUMBER_OF_PLAYERS = ".numberOfPlayers";
    private static final String PARTY = ".party";
    private static final String ENCOUNTER = ".encounter";
    private static final String PLAYER_CHARACTER = ".playerCharacter.";
    private static final String SETTINGS_SAVED = ".settingsSaved";
    private final List<Item> items = new ArrayList<Item>();
    private final List<PlayerCharacter> playerCharacters = new ArrayList<PlayerCharacter>();

    private final Party party = new Party();
    private final Encounter encounter = new Encounter();

    public World() {
        items.add(new Item("Torch", "torch.png"));
        items.add(new Item("Lantern", "lantern.png"));
        items.add(new Item("Shield", "buckler.png"));

        encounter.setParty(party);
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


    public PlayerCharacter getPlayerCharacter(int id) {
        return playerCharacters.get(id);
    }

    public int getPlayerCharacterId(PlayerCharacter playerCharacter) {
        return playerCharacters.indexOf(playerCharacter);
    }

    public Item getItem(String itemId) {
        for (Item item : items) {
            if (item.getName().equals(itemId)) return item;
        }

        return null;
    }

    /**
     * Contains all available actions.
     */
    public final ActionRegistry actionRegistry = new ActionRegistry(this);

    @Override public void save(World world, Preferences preferences, String prefix) {
        // Save players
        preferences.putInteger(prefix + NUMBER_OF_PLAYERS, playerCharacters.size());
        for (int i = 0; i < playerCharacters.size(); i++) {
            playerCharacters.get(i).save(this, preferences, prefix + PLAYER_CHARACTER + i);
        }

        // Save party
        party.save(this, preferences, prefix + PARTY);

        // Save encounter
        encounter.save(this, preferences, prefix + ENCOUNTER);

        // Set a flag that we have saved things
        preferences.putBoolean(prefix + SETTINGS_SAVED, true);
    }

    @Override public void load(World world, Preferences preferences, String prefix) {
        // Check that we have a save
        if (preferences.getBoolean(prefix + SETTINGS_SAVED, false)) {
            // Load players
            playerCharacters.clear();
            final int numberOfPlayers = preferences.getInteger(prefix + NUMBER_OF_PLAYERS, 0);
            for (int i = 0; i < numberOfPlayers; i++) {
                PlayerCharacter playerCharacter = new PlayerCharacter();
                playerCharacter.load(this, preferences, prefix + PLAYER_CHARACTER + i);
                playerCharacters.add(playerCharacter);
            }

            // Load party
            party.load(this, preferences, prefix + PARTY);

            // Load encounter
            encounter.load(this, preferences, prefix + ENCOUNTER);
        }
    }

}
