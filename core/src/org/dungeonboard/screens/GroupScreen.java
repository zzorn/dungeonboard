package org.dungeonboard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.dungeonboard.Context;
import org.dungeonboard.model.Party;
import org.dungeonboard.model.PartyListener;
import org.dungeonboard.model.PlayerCharacter;
import org.dungeonboard.model.World;
import org.dungeonboard.actions.GameAction;
import org.dungeonboard.model.battlefield.Battlefield;
import org.dungeonboard.utils.BattlefieldUi;
import org.dungeonboard.utils.CharacterEditor;

import java.util.List;

/**
 *
 */
public class GroupScreen extends UiScreenBase {

    private static final String TITLE = "Party Organization";
    public static final float INITIAL_GROUP_RADIUS_M = 5;
    private final PartyListener partyListener = new PartyListener() {
        @Override public void onMemberAdded(PlayerCharacter character) {
            addPlayerToGroupLayout(character);
        }

        @Override public void onMemberRemoved(PlayerCharacter character) {
            removePlayerFromGroupLayout(character);
        }
    };

    private CharacterEditor characterEditor;

    private final Battlefield battlefield = new Battlefield(null);
    private Party party;

    public GroupScreen(Context context) {
        super(context, TITLE);
    }

    @Override protected Actor createContent(World world, Skin skin, TextureAtlas textureAtlas) {
        final Table table = new Table(skin);

        characterEditor = new CharacterEditor(getContext(), Gdx.graphics.getWidth(), false);

        // Small 2D map view with characters
        BattlefieldUi battlefieldUi = new BattlefieldUi(getContext());
        battlefieldUi.setBattlefield(battlefield);
        table.add(battlefieldUi.getUi()).expand().fill();
        table.row();

        // Character editor
        table.add(characterEditor.getUi()).bottom().expandX().fillX();

        // Init
        // TODO: Support multiple parties later -> listen to change in current party in world.
        setParty(world.getParty());

        return table;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        if (this.party != null) {
            this.party.removeListener(partyListener);

            for (PlayerCharacter playerCharacter : party.getPartyMembers()) {
                removePlayerFromGroupLayout(playerCharacter);
            }
        }

        this.party = party;

        if (this.party != null) {
            for (PlayerCharacter playerCharacter : party.getPartyMembers()) {
                addPlayerToGroupLayout(playerCharacter);
            }

            this.party.addListener(partyListener);
        }
    }

    public void addPlayerToGroupLayout(PlayerCharacter playerCharacter) {
        battlefield.addCharacter(playerCharacter,
                                 (float) Math.random() * INITIAL_GROUP_RADIUS_M,
                                 (float) Math.random() * INITIAL_GROUP_RADIUS_M);
    }

    public void removePlayerFromGroupLayout(PlayerCharacter playerCharacter) {
        battlefield.removeCharacter(playerCharacter);
    }

    @Override protected void onUpdate(float deltaTimeSeconds, World world) {
        // TODO: Implement

    }

    @Override protected void onDispose() {
        // TODO: Implement

    }

    @Override public List<GameAction> getAvailableActions() {
        // TODO: Implement
        return null;
    }
}
