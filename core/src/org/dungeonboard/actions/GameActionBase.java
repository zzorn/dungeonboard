package org.dungeonboard.actions;

import com.badlogic.gdx.graphics.Color;
import org.dungeonboard.World;
import org.dungeonboard.actions.changes.WorldChange;
import org.dungeonboard.model.Encounter;
import org.dungeonboard.model.GameCharacter;

/**
 *
 */
public abstract class GameActionBase implements GameAction {

    private final String name;
    private final Color color;

    protected GameActionBase(String name) {
        this(name, Color.LIGHT_GRAY);
    }

    protected GameActionBase(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }


}
