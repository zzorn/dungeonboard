package org.dungeonboard.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.dungeonboard.Context;
import org.dungeonboard.actions.GameAction;
import org.dungeonboard.actions.GameActionBase;
import org.dungeonboard.model.GameCharacter;
import org.dungeonboard.model.PlayerCharacter;
import org.dungeonboard.model.World;

import java.util.List;

/**
 * For editing a character
 */
public class CharacterEditorScreen extends UiScreenBase {

    private GameCharacter editedCharacter;

    public CharacterEditorScreen(Context context) {
        super(context, "Edit Character");
    }

    @Override protected Actor createContent(World world, Skin skin, TextureAtlas textureAtlas) {
        final Table table = new Table(skin);


        setupActions();

        return table;
    }

    @Override protected void onUpdate(float deltaTimeSeconds, World world) {
    }

    @Override protected void onDispose() {
    }

    @Override public List<GameAction> getAvailableActions() {
        return null;
    }


    public void setupActions() {

        // Done
        addAction(new GameActionBase("    Done    ", Color.GREEN) {
            @Override public boolean isAvailable(World world) {
                return true;
            }

            @Override public void doAction(World world) {
                // TODO: Switch screen to the one that we came here from
            }
        }, true, false);

        // Add character
        addAction(new GameActionBase("Add new Character", new Color(0.8f, 0.7f, 0.3f, 1)) {
            @Override public boolean isAvailable(World world) {
                return true;
            }

            @Override public void doAction(World world) {
                world.addPlayerCharacter(new PlayerCharacter());
            }
        }, false, true);

        // Remove current character
        addAction(new GameActionBase("Delete", new Color(0.6f, 0, 0, 1f)) {
            @Override public boolean isAvailable(World world) {
                return world.getPlayerCharacters().contains(editedCharacter);
            }

            @Override public void doAction(World world) {
                if (editedCharacter != null && editedCharacter instanceof PlayerCharacter) {
                    world.removePlayerCharacter((PlayerCharacter) editedCharacter);
                }
            }
        }, false, true);


    }

}
