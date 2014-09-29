package org.dungeonboard.model.battlefield;

import com.badlogic.gdx.utils.Array;
import org.dungeonboard.model.mapping.Position;
import org.dungeonboard.model.mapping.PositionListener;

/**
 * A character or other thing on some battlefield.
 */
public abstract class BattlefieldEntity {

    private final Position position = new Position();
    private final Array<BattlefieldEntityListener> listeners = new Array<BattlefieldEntityListener>(3);


    protected BattlefieldEntity() {
        this(0,0);
    }

    protected BattlefieldEntity(float x, float y) {
        setPos(x, y);

        // Listen to position changes.
        position.addListener(new PositionListener() {
            @Override public void onPositionChanged(Position position, float oldX, float oldY, float x, float y) {
                for (BattlefieldEntityListener listener : listeners) {
                    listener.onEntityMoved(BattlefieldEntity.this);
                }
            }
        });
    }

    public final Position getPosition() {
        return position;
    }

    public final void setPos(float x, float y) {
        position.set(x, y);
    }

    public final void addListener(BattlefieldEntityListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(BattlefieldEntityListener listener) {
        listeners.removeValue(listener, true);
    }

}
