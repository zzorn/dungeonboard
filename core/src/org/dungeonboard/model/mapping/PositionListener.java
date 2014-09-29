package org.dungeonboard.model.mapping;

/**
 *
 */
public interface PositionListener {

    void onPositionChanged(Position position, float oldX, float oldY, float x, float y);

}
