package org.dungeonboard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.dungeonboard.Context;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.actions.GameAction;
import org.dungeonboard.actions.GameActionBase;
import org.dungeonboard.model.*;
import org.dungeonboard.utils.CharacterButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For editing a character
 */
public class CharacterEditorScreen extends UiScreenBase {

    private static final float GAP_SIZE = 0.02f;
    private static final int ICON_WIDTH = 128;
    private static final int COLOR_ICON_WIDTH = 80;
    private static final int MIN_ICON_COLUMNS = 6;
    private static final int MIN_COLOR_ICON_COLUMNS = 6;
    private static final Color UNSELECTED_ICON_COLOR = Color.GRAY;
    private static final Color SELECTED_ICON_COLOR = Color.WHITE;
    private GameCharacter editedCharacter;

    private TextField nameEditor;

    private boolean updateUiOnCharacterChanges = true;

    private final CharacterListener characterListener = new CharacterListener() {
        @Override public void onChanged(GameCharacter changedCharacter) {
            if (updateUiOnCharacterChanges &&
                editedCharacter == changedCharacter) {
                updateUi();
            }
        }

        @Override public void onInitiativeChanged(GameCharacter character) {
            // Initiative change already triggers an onChange call
        }
    };
    private Table table;
    private Drawable glowBackgroundDrawable;
    private Drawable iconBackground;
    private Map<String, ImageButton> iconButtons;
    private Map<Color, ImageButton> colorButtons;
    private TextureRegionDrawable color_icon;
    private TextureRegionDrawable color_icon_selected;
    private boolean newCharacter;

    public CharacterEditorScreen(Context context) {
        super(context, "Edit Character");
    }

    public GameCharacter getEditedCharacter() {
        return editedCharacter;
    }

    public void setEditedCharacter(GameCharacter editedCharacter, boolean newCharacter) {
        this.newCharacter = newCharacter;

        if (this.editedCharacter != editedCharacter) {
            if (this.editedCharacter != null) {
                this.editedCharacter.removeListener(characterListener);
            }

            this.editedCharacter = editedCharacter;

            if (this.editedCharacter != null) {
                this.editedCharacter.addListener(characterListener);
            }

            updateUi();

            // Focus name editor initially for new characters
            if (newCharacter) focusNameEditor();
        }
    }

    @Override protected Actor createContent(World world, Skin skin, TextureAtlas textureAtlas) {
        glowBackgroundDrawable = new TextureRegionDrawable(textureAtlas.findRegion("selection_glow"));
        iconBackground = new TextureRegionDrawable(textureAtlas.findRegion("selection_glow"));
        color_icon = new TextureRegionDrawable(textureAtlas.findRegion("color"));
        color_icon_selected = new TextureRegionDrawable(textureAtlas.findRegion("color_selected"));

        final float gap = Gdx.graphics.getHeight() * GAP_SIZE;
        table = new Table(skin).pad(gap, gap, gap, gap);

        // Name editor
        Table layoutTable = new Table(skin);
        layoutTable.add(new Label("Name", skin)).left().padRight(gap);
        layoutTable.add(createNameEditor(skin)).expandX().fillX();

        layoutTable.row();

        // TODO: Add base initiative editor for player characters if they want to autocast initiative, and a box to clear it.
        /*
        // Base initiative
        layoutTable.add(new Label("Base Initiative", skin)).left().padRight(gap);
        layoutTable.add(new TextField("10", skin)).expandX().fillX();

        layoutTable.row();
        */

        table.add(layoutTable).expandX().fillX().row();


        // Icon editor
        table.add(new Label("Icon", skin)).left().row();
        table.add(createIconSelector(skin)).expandX().fillX().row();

        // Color editor
        table.add(new Label("Color", skin)).left().row();
        table.add(createColorSelector(skin)).expandX().fillX().row();

        // Item selector
//        table.add(new Label("Important items", skin)).left().row();
        // TODO: Allow toggling predefined items on or off for this character

        setupActions();

        // Initialize UI
        updateUi();

        // Focus name editor initially for new characters
        if (newCharacter) focusNameEditor();

        return table;
    }

    private ScrollPane createIconSelector(Skin skin) {
        final float gap = Gdx.graphics.getHeight() * GAP_SIZE;

        Table iconTable = new Table(skin);
        final float tableWidth = Gdx.graphics.getWidth() - gap * 2;
        int columns = (int) Math.max(MIN_ICON_COLUMNS, tableWidth / ICON_WIDTH);
        float iconSize = tableWidth / columns;
        int column = 0;
        iconButtons = new HashMap<String, ImageButton>();
        for (final TextureAtlas.AtlasRegion icon : StyleSettings.ICONS) {
            // Add rows
            column %= columns;
            if (column == 0) iconTable.row();
            column++;

            // Add icon buttons
            final Drawable unselected = new TextureRegionDrawable(icon);
            final ImageButton imageButton = new ImageButton(unselected);
            final String iconName = StyleSettings.getIconName(icon);
            iconButtons.put(iconName, imageButton);
            imageButton.getImage().setColor(UNSELECTED_ICON_COLOR);
            iconTable.add(imageButton).size(iconSize, iconSize);
            imageButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    editedCharacter.setIcon(iconName);
                    updateSelectedIcon();
                }
            });
        }
        ScrollPane iconScroll = new ScrollPane(iconTable, skin);
        iconScroll.setScrollingDisabled(true, false);
        iconScroll.setHeight(3 * iconSize);
        return iconScroll;
    }

    private ScrollPane createColorSelector(Skin skin) {
        final float gap = Gdx.graphics.getHeight() * GAP_SIZE;

        Table table = new Table(skin);
        final float tableWidth = Gdx.graphics.getWidth() - gap * 2;
        int columns = (int) Math.max(MIN_COLOR_ICON_COLUMNS, tableWidth / COLOR_ICON_WIDTH);
        float iconSize = tableWidth / columns;
        int column = 0;
        colorButtons = new HashMap<Color, ImageButton>();
        for (final Color color : StyleSettings.ICON_COLORS) {
            // Add rows
            column %= columns;
            if (column == 0) table.row();
            column++;

            // Add color buttons
            final ImageButton colorButton = new ImageButton(color_icon);
            colorButtons.put(color, colorButton);
            colorButton.getImage().setColor(color);
            table.add(colorButton).size(iconSize, iconSize);
            colorButton.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    editedCharacter.setColor(color);
                    updateSelectedColor();
                    updateSelectedIcon();
                }
            });
        }
        ScrollPane scroll = new ScrollPane(table, skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setHeight(3 * iconSize);
        return scroll;
    }

    private void updateSelectedIcon() {
        for (ImageButton iconButton : iconButtons.values()) {
            iconButton.getImage().setColor(UNSELECTED_ICON_COLOR);
        }

        final ImageButton currentIconButton = iconButtons.get(editedCharacter.getIcon());
        if (currentIconButton != null) {
            //final Color selectedIconColor = SELECTED_ICON_COLOR;
            final Color selectedIconColor = editedCharacter.getColor();
            currentIconButton.getImage().setColor(selectedIconColor);
        }
    }

    private void updateSelectedColor() {
        for (ImageButton colorButton : colorButtons.values()) {
            CharacterButton.setImageButtonImage(colorButton, color_icon);
        }

        final ImageButton currentColorButton = colorButtons.get(editedCharacter.getColor());
        if (currentColorButton != null) {
            CharacterButton.setImageButtonImage(currentColorButton, color_icon_selected);
        }
    }

    @Override protected void onUpdate(float deltaTimeSeconds, World world) {
    }

    @Override protected void onDispose() {
        setEditedCharacter(null, false);
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
                getContext().switchScreenBack();
            }
        }, true, false);

        // Add character
        addAction(new GameActionBase("Add new Character", new Color(0.8f, 0.7f, 0.3f, 1)) {
            @Override public boolean isAvailable(World world) {
                return true;
            }

            @Override public void doAction(World world) {
                final PlayerCharacter adventurer = new PlayerCharacter();
                world.addPlayerCharacter(adventurer);
                editCharacter(adventurer, true);
            }
        }, false, false);

        // Add Others
        addAction(new GameActionBase("Add Others", new Color(0.3f, 0.8f, 0.6f, 1)) {
            @Override public boolean isAvailable(World world) {
                return true;
            }

            @Override public void doAction(World world) {
                final NonPlayerCharacters other = new NonPlayerCharacters("Others");
                world.getCurrentEncounter().addCharacter(other);
                editCharacter(other, true);
            }
        }, false, false);

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
        }, false, false);


    }


    private TextField createNameEditor(Skin skin) {
        // Name field
        nameEditor = new TextField("", skin);

        // Listen to name field
        nameEditor.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Select whole field content when typing starting, so that the existing text will be replaced
                nameEditor.selectAll();
                return true;
            }

            @Override public boolean keyTyped(InputEvent event, char typedChar) {
                // Update character name when name field edited
                if (editedCharacter != null) {
                    disableUiUpdates();
                    editedCharacter.setName(nameEditor.getText());
                    enableUiUpdates();
                }

                // Handle closing of keyboard when editing is ready
                handleCloseKeyboard(event, nameEditor);

                return true;
            }
        });

        return nameEditor;
    }



    private void updateUi() {
        if (table != null) {
            if (editedCharacter == null) {
                nameEditor.setText("");
                nameEditor.setColor(Color.BLACK);
                nameEditor.setDisabled(true);

                table.setVisible(false);
            }
            else {
                table.setVisible(true);
                nameEditor.setText(editedCharacter.getName());
                nameEditor.setColor(editedCharacter.getColor());
                nameEditor.setDisabled(false);

                updateSelectedIcon();

                updateSelectedColor();
            }
        }
    }


    /**
     * @return true if keyboard was closed, false if not.
     */
    private boolean handleCloseKeyboard(InputEvent event, final TextField editor) {
        // Hide onscreen kb on enter
        if (event.getKeyCode() == Input.Keys.ENTER) {
            hideOnscreenKeyboard(editor);
            return true;
        }
        else {
            return false;
        }
    }




    private void hideOnscreenKeyboard(final TextField editor) {
        final TextField.OnscreenKeyboard onscreenKeyboard = editor.getOnscreenKeyboard();
        if (onscreenKeyboard != null) {
            onscreenKeyboard.show(false);
        }
    }


    private void enableUiUpdates() {updateUiOnCharacterChanges = true;}


    private void disableUiUpdates() {updateUiOnCharacterChanges = false;}

    private void focusNameEditor() {
        final Stage stage = getContext().getStage();
        if (stage != null && nameEditor != null) {
            stage.setKeyboardFocus(nameEditor);
            nameEditor.selectAll();
        }
    }

}
