package org.dungeonboard.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.model.CharacterListener;
import org.dungeonboard.model.GameCharacter;

/**
 * Editor for a character.
 */
public class CharacterEditor {

    private GameCharacter character;
    private Table editor;
    private final TextureAtlas textureAtlas;
    private final Skin skin;
    private final boolean includeInitiativeEditor;
    private final float maxWidth;
    private boolean updateUiOnCharacterChanges = true;
    private TextField initiativeEditor;
    private TextField nameEditor;

    private final CharacterListener characterListener = new CharacterListener() {
        @Override public void onChanged(GameCharacter changedCharacter) {
            if (updateUiOnCharacterChanges &&
                character == changedCharacter) {
                updateUi();
            }
        }

        @Override public void onInitiativeChanged(GameCharacter character) {
            // Initiative change already triggers an onChange call
        }
    };
    private Table table;
    private Table initiativeEditorTable;

    public CharacterEditor(TextureAtlas textureAtlas, Skin skin, float maxWidth, boolean includeInitiativeEditor) {
        this.textureAtlas = textureAtlas;
        this.skin = skin;
        this.maxWidth = maxWidth;
        this.includeInitiativeEditor = includeInitiativeEditor;
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public void setCharacter(GameCharacter character) {
        if (this.character != character) {
            if (this.character != null) {
                this.character.removeListener(characterListener);
            }

            this.character = character;

            if (this.character != null) {
                this.character.addListener(characterListener);
            }

            updateUi();
        }
    }

    public Table getEditor() {
        if (editor == null) {
            editor = createEditor();
        }

        return editor;
    }

    public void dispose() {
        setCharacter(null);
    }

    private Table createEditor() {
        table = new Table(skin);

        // Icon editor
        // TODO

        // Color editor
        // TODO

        // Name editor
        table.add(createNameEditor()).expandX().fillX();

        // Initiative editor
        if (includeInitiativeEditor) {
            table.add(createInitiativeEditor());
        }

        // Initialize UI
        updateUi();

        return table;
    }

    private TextField createNameEditor() {
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
                if (character != null) {
                    disableUiUpdates();
                    character.setName(nameEditor.getText());
                    enableUiUpdates();
                }

                // Handle closing of keyboard when editing is ready
                handleCloseKeyboard(event, nameEditor);

                return true;
            }
        });

        return nameEditor;
    }

    private Table createInitiativeEditor() {
        initiativeEditorTable = new Table(skin);

        // Initiative field
        initiativeEditor = new TextField("", skin);
        initiativeEditor.setMaxLength(3);
        initiativeEditor.setOnlyFontChars(true);
        initiativeEditor.setRightAligned(true);
        // initiativeEditor.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        // Initiative quick adjustment buttons
        //initiativeEditorTable.add(createInitiativeModButton(skin, -10, new Color(0.5f, 0.5f, 1f, 1)));
        initiativeEditorTable.add(createInitiativeModButton(skin, -5, new Color(0.6f, 0.6f, 1f, 1)));
        initiativeEditorTable.add(createInitiativeModButton(skin, -1, new Color(0.7f, 0.7f, 1f, 1)));
        initiativeEditorTable.add(initiativeEditor).width(maxWidth * 0.1f).center();
        initiativeEditorTable.add(createInitiativeModButton(skin, +1, new Color(1f, 0.7f, 0.7f, 1)));
        initiativeEditorTable.add(createInitiativeModButton(skin, +5, new Color(1f, 0.6f, 0.6f, 1)));
        //initiativeEditorTable.add(createInitiativeModButton(skin, +10, new Color(1f, 0.5f, 0.5f, 1)));


        // Listen to initiative field
        initiativeEditor.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Select whole field content when typing starting, so that the existing text will be replaced
                initiativeEditor.selectAll();
                return true;
            }

            @Override public boolean keyTyped(InputEvent event, char typedChar) {
                try {
                    final int initiative = Integer.parseInt(initiativeEditor.getText());
                    if (character != null) {
                        disableUiUpdates();
                        character.setInitiative(initiative);
                        enableUiUpdates();
                    }
                }
                catch (NumberFormatException e) {
                    // Don't change initiative if there was mistyping
                }

                // Handle closing of keyboard when editing is ready
                handleCloseKeyboard(event, initiativeEditor);

                return true;
            }
        });

        return initiativeEditorTable;
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

    private TextButton createInitiativeModButton(Skin skin, final int change, Color color) {
        final TextButton initiativeMod = new TextButton(" " + (change > 0 ? "+" : "") +change + " ", skin);
        initiativeMod.setColor(color);
        initiativeMod.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                if (character != null) {
                    character.changeInitiative(change);
                    StyleSettings.playButtonPressSound();
                }
            }
        });
        return initiativeMod;
    }


    private void updateUi() {
        if (character == null) {
            nameEditor.setText("");
            nameEditor.setColor(Color.BLACK);
            nameEditor.setDisabled(true);

            if (includeInitiativeEditor) {
                initiativeEditor.setText("");
                initiativeEditor.setDisabled(true);
            }

            table.setVisible(false);
        }
        else {
            table.setVisible(true);
            nameEditor.setText(character.getName());
            nameEditor.setColor(character.getColor());
            nameEditor.setDisabled(false);

            if (includeInitiativeEditor) {
                initiativeEditor.setText("" + character.getInitiative());
                initiativeEditor.setDisabled(false);
            }
        }
    }



}
