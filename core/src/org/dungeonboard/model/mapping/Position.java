package org.dungeonboard.model.mapping;

import com.badlogic.gdx.utils.Array;

/**
 * Position of something on a map.
 */
public final class Position {

    private float x;
    private float y;

    private transient Array<PositionListener> listeners;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        set(x, y);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        set(x, y);
    }

    public void set(float x, float y) {
        float oldX = this.x;
        float oldY = this.y;

        this.x = x;
        this.y = y;

        // Notify any listeners
        if (listeners != null) {
            for (PositionListener listener : listeners) {
                listener.onPositionChanged(this, oldX, oldY, this.x, this.y);
            }
        }
    }

    public void addListener(PositionListener listener) {
        if (listeners == null) {
            listeners = new Array<PositionListener>(3);
        }

        listeners.add(listener);
    }

    public void removeListener(PositionListener listener) {
        if (listeners != null) {
            listeners.removeValue(listener, true);
        }
    }
}
