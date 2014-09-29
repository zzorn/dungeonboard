package org.dungeonboard.model.battlefield;

import com.badlogic.gdx.utils.Array;
import org.dungeonboard.model.GameCharacter;
import org.dungeonboard.model.mapping.MappedArea;
import org.dungeonboard.model.mapping.MappedAreaListener;
import org.dungeonboard.model.mapping.Position;
import org.dungeonboard.model.mapping.PositionListener;

/**
 * Layout of figures on some mapped area.
 */
public final class Battlefield {

    private MappedArea mappedArea;
    private final Array<BattlefieldEntity> battlefieldEntities = new Array<BattlefieldEntity>();
    private final Array<BattlefieldListener> listeners = new Array<BattlefieldListener>(3);

    private final BattlefieldEntityListener entityListener = new BattlefieldEntityListener() {
        @Override public void onEntityMoved(BattlefieldEntity entity) {
            for (BattlefieldListener listener : listeners) {
                listener.onEntityMoved(Battlefield.this, entity);
            }
        }
    };

    private final MappedAreaListener mappedAreaListener = new MappedAreaListener() {
        @Override public void onMappedAreaChanged(MappedArea mappedArea) {
            for (BattlefieldListener listener : listeners) {
                listener.onMappedAreaUpdated(Battlefield.this);
            }
        }
    };

    public Battlefield(MappedArea mappedArea) {
        setMappedArea(mappedArea);
    }

    public void addEntity(final BattlefieldEntity entity) {
        battlefieldEntities.add(entity);

        // Notify listeners about added entity
        for (BattlefieldListener listener : listeners) {
            listener.onEntityAdded(this, entity);
        }

        // Listen to entity
        entity.addListener(entityListener);
    }

    public void removeEntity(BattlefieldEntity entity) {
        battlefieldEntities.removeValue(entity, true);

        // Stop listening to entity
        entity.removeListener(entityListener);

        // Notify listeners about entity removal
        for (BattlefieldListener listener : listeners) {
            listener.onEntityRemoved(this, entity);
        }
    }

    public void addCharacter(final GameCharacter character, float x, float y) {
        addEntity(new BattlefieldCharacter(character, x, y));
    }

    public void removeCharacter(final GameCharacter character) {
        BattlefieldEntity entityToRemove = findCharacterEntity(character);
        if (entityToRemove != null) removeEntity(entityToRemove);
    }

    /**
     * Called by the ui when an entity is clicked or otherwise selected.
     */
    public void selectEntity(BattlefieldEntity entity) {
        for (BattlefieldListener listener : listeners) {
            listener.onEntitySelected(this, entity);
        }
    }

    public void addListener(BattlefieldListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BattlefieldListener listener) {
        listeners.removeValue(listener, true);
    }

    public MappedArea getMappedArea() {
        return mappedArea;
    }

    public void setMappedArea(MappedArea mappedArea) {

        if (this.mappedArea != null) {
            this.mappedArea.removeListener(mappedAreaListener);
        }

        this.mappedArea = mappedArea;

        if (this.mappedArea != null) {
            this.mappedArea.addListener(mappedAreaListener);
        }

        // Notify listeners
        for (BattlefieldListener listener : listeners) {
            listener.onMappedAreaUpdated(this);
        }
    }

    public Array<BattlefieldEntity> getBattlefieldEntities() {
        return battlefieldEntities;
    }

    private BattlefieldEntity findCharacterEntity(GameCharacter character) {
        for (BattlefieldEntity entity : battlefieldEntities) {
            if (entity instanceof BattlefieldCharacter &&
                ((BattlefieldCharacter)entity).getCharacter() == character) {
                return entity;
            }
        }

        return null;
    }
}
