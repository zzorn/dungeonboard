package org.dungeonboard.model;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.storage.Entity;

/**
 *
 */
public abstract class GameCharacter extends Entity {

    private String name;
    private Color color;

    private int initiative;
    private int disabledForRounds = 0;
    private boolean turnUsed;
    private boolean inReadyAction = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
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
    }

    public void setDisabled() {
        disabledForRounds = -1;
    }

    public void setEnabled() {
        disabledForRounds = 0;
    }


    public int getDisabledForRounds() {
        return disabledForRounds;
    }

    public boolean isTurnUsed() {
        return turnUsed;
    }

    public void setTurnUsed(boolean turnUsed) {
        this.turnUsed = turnUsed;
    }

    public boolean isInReadyAction() {
        return inReadyAction;
    }

    public void setInReadyAction(boolean inReadyAction) {
        this.inReadyAction = inReadyAction;
    }

    public void onNewRound() {
        // Activate
        turnUsed = false;

        // Decrease disabled timer
        if (disabledForRounds > 0) disabledForRounds--;
    }

    public void onTurn() {
        // Ready action state disappears at the start of each of your turns
        inReadyAction = false;
    }

    public abstract boolean isPlayerCharacter();
}
