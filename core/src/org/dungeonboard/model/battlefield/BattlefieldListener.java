package org.dungeonboard.model.battlefield;

/**
 *
 */
public interface BattlefieldListener {

    void onMappedAreaUpdated(Battlefield battlefield);
    void onEntityMoved(Battlefield battlefield, BattlefieldEntity entity);
    void onEntityAdded(Battlefield battlefield, BattlefieldEntity entity);
    void onEntityRemoved(Battlefield battlefield, BattlefieldEntity entity);
    void onEntitySelected(Battlefield battlefield, BattlefieldEntity entity);

}
