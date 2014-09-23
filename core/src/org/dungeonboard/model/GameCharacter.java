package org.dungeonboard.model;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.StyleSettings;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class GameCharacter  {

    private String name;
    private Color color;

    private int initiative = 20;
    private int disabledForRounds = 0;
    private boolean turnUsed;
    private boolean inReadyAction = false;

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
}
