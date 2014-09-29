package org.dungeonboard.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.dungeonboard.Context;
import org.dungeonboard.model.battlefield.Battlefield;
import org.dungeonboard.model.battlefield.BattlefieldEntity;
import org.dungeonboard.model.battlefield.BattlefieldListener;
import org.dungeonboard.uis.Ui;
import org.dungeonboard.uis.UiBase;

/**
 * View of some battlefield, which allows moving entities on it.
 */
public class BattlefieldUi extends UiBase {

    private Battlefield battlefield;

    private final BattlefieldListener battlefieldListener = new BattlefieldListener() {
        @Override public void onMappedAreaUpdated(Battlefield battlefield) {
            updateUi();
        }

        @Override public void onEntityMoved(Battlefield battlefield, BattlefieldEntity entity) {
            updateUi();
        }

        @Override public void onEntityAdded(Battlefield battlefield, BattlefieldEntity entity) {
            updateUi();
        }

        @Override public void onEntityRemoved(Battlefield battlefield, BattlefieldEntity entity) {
            updateUi();
        }

        @Override public void onEntitySelected(Battlefield battlefield, BattlefieldEntity entity) {
            updateUi();
        }
    };

    public BattlefieldUi(Context context) {
        super(context);
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(Battlefield battlefield) {
        if (this.battlefield != null) {
            this.battlefield.removeListener(battlefieldListener);
        }

        this.battlefield = battlefield;

        if (this.battlefield != null) {
            this.battlefield.addListener(battlefieldListener);
        }

        updateUi();
    }

    @Override protected Actor createUi(Skin skin, TextureAtlas textureAtlas) {
        // TODO
        return new Table(skin);
    }

    @Override public void dispose() {
        // TODO
    }

    private void updateUi() {
        // TODO: Implement
    }
}
