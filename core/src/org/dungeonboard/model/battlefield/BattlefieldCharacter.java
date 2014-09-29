package org.dungeonboard.model.battlefield;

import org.dungeonboard.model.GameCharacter;

/**
 *
 */
public class BattlefieldCharacter extends BattlefieldEntity {

    private GameCharacter character;

    public BattlefieldCharacter(GameCharacter character) {
        this.character = character;
    }

    public BattlefieldCharacter(GameCharacter character, float x, float y) {
        super(x, y);
        this.character = character;
    }

    public GameCharacter getCharacter() {
        return character;
    }
}
