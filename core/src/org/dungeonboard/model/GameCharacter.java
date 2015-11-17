package org.dungeonboard.model;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.StyleSettings;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class GameCharacter implements Saveable {

    private static final String NAME = ".name";
    private static final String ICON = ".icon";
    private static final String COLOR = ".color";
    private static final String NUMBER_OF_ITEMS = ".numberOfItems";
    private static final String ITEM = ".item.";
    private static final String INITIATIVE = ".initiative";
    private static final String DISABLED_FOR_ROUNDS = ".disabledForRounds";
    private static final String TURN_USED = ".turnUsed";
    private static final String IN_READY_ACTION = ".inReadyAction";

    private String name;
    private Color color;
    private String icon = "characters/adventurer_hat";

    private int initiative = 20;
    private int disabledForRounds = 0;
    private boolean turnUsed;
    private boolean inReadyAction = false;

    private final List<Item> items = new ArrayList<Item>();

    private List<CharacterListener> listeners = new ArrayList<CharacterListener>();

    protected GameCharacter() {
        this("Character");
    }

    protected GameCharacter(String name) {
        this(name, StyleSettings.DEFAULT_NAME_COLOR.cpy());
    }

    protected GameCharacter(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        onChanged();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        onChanged();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
        onChanged();
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
        onInitiativeChanged();
    }

    public boolean isDisabled() {
        return disabledForRounds != 0;
    }

    public int getRoundsDisabled() {
        return disabledForRounds;
    }

    public boolean isDisabledForNow() {
        return disabledForRounds < 0;
    }

    public void setDisabledForRounds(int rounds) {
        disabledForRounds = rounds;
        onChanged();
    }

    public void setDisabled() {
        disabledForRounds = -1;
        inReadyAction = false;
        onChanged();
    }

    public void setEnabled() {
        disabledForRounds = 0;
        onChanged();
    }


    public int getDisabledForRounds() {
        return disabledForRounds;
    }

    public boolean isTurnUsed() {
        return turnUsed;
    }

    public void setTurnUsed(boolean turnUsed) {
        this.turnUsed = turnUsed;
        onChanged();
    }

    public boolean isInReadyAction() {
        return inReadyAction;
    }

    public void setInReadyAction(boolean inReadyAction) {
        this.inReadyAction = inReadyAction;
        onChanged();
    }

    /**
     * @return the items carried / managed by this character.
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @param item Item to add.
     */
    public final void addItem(Item item) {
        //Check.notNull(item, "item");
        //Check.notContained(item, items, "items");

        if (!items.contains(item)) {
            items.add(item);
            onChanged();
        }
    }

    /**
     * @param item Item to toggle.
     */
    public final void toggleItem(Item item) {
        if (items.contains(item)) {
            removeItem(item);
        }
        else {
            addItem(item);
        }
    }


    /**
     * @param item Item to remove.
     */
    public final void removeItem(Item item) {
        if (items.contains(item)) {
            items.remove(item);
            onChanged();
        }
    }

    public void removeAllItems() {
        items.clear();
        onChanged();
    }

    public void onNewRound() {
        // Activate
        turnUsed = false;

        // Decrease disabled timer
        if (disabledForRounds > 0) disabledForRounds--;

        onChanged();
    }

    public void onTurn() {
        // Ready action state disappears at the start of each of your turns
        inReadyAction = false;
    }

    public abstract boolean isPlayerCharacter();

    public void changeInitiative(int change) {
        initiative += change;

        onInitiativeChanged();
    }

    public final void addListener(CharacterListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(CharacterListener listener) {
        listeners.remove(listener);
    }

    protected void onChanged() {
        for (CharacterListener listener : listeners) {
            listener.onChanged(this);
        }
    }

    protected void onInitiativeChanged() {
        onChanged();

        for (CharacterListener listener : listeners) {
            listener.onInitiativeChanged(this);
        }
    }

    public void reset() {
        disabledForRounds = 0;
        turnUsed = false;
        inReadyAction = false;
        onChanged();
    }

    @Override public void save(World world, Preferences preferences, String prefix) {
        preferences.putString(prefix + NAME, name);
        preferences.putString(prefix + ICON, icon);
        preferences.putString(prefix + COLOR, color.toString());
        preferences.putInteger(prefix + INITIATIVE, initiative);
        preferences.putInteger(prefix + DISABLED_FOR_ROUNDS, disabledForRounds);
        preferences.putBoolean(prefix + TURN_USED, turnUsed);
        preferences.putBoolean(prefix + IN_READY_ACTION, inReadyAction);

        // Add items
        preferences.putInteger(prefix + NUMBER_OF_ITEMS, items.size());
        for (int i = 0; i < items.size(); i++) {
            preferences.putString(prefix + ITEM + i, items.get(i).getName());
        }

    }

    @Override public void load(World world, Preferences preferences, String prefix) {

        setName(preferences.getString(prefix + NAME, "Unknown"));
        setIcon(preferences.getString(prefix + ICON, ""));
        setColor(Color.valueOf(preferences.getString(prefix + COLOR, "FF0000")));

        setInitiative(preferences.getInteger(prefix + INITIATIVE, 20));
        setDisabledForRounds(preferences.getInteger(prefix + DISABLED_FOR_ROUNDS, 0));
        setTurnUsed(preferences.getBoolean(prefix + TURN_USED, false));
        setInReadyAction(preferences.getBoolean(prefix + IN_READY_ACTION, false));

        // Load items
        removeAllItems();
        final int numberOfItems = preferences.getInteger(prefix + NUMBER_OF_ITEMS, 0);
        for (int i = 0; i < numberOfItems; i++) {
            final String itemId = preferences.getString(prefix + ITEM + i, "");
            final Item item = world.getItem(itemId);
            if (item != null) addItem(item);
        }


    }
}
