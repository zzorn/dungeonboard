package org.dungeonboard.screens.initiative;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.World;
import org.dungeonboard.actions.EncounterActionBase;
import org.dungeonboard.actions.GameAction;
import org.dungeonboard.model.*;
import org.dungeonboard.screens.UiScreenBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class InitiativeScreen extends UiScreenBase {

    private static final int POINTS_ABOVE_NEXT_TO_GET_FREE_TURN = 10;
    public static final Color BETWEEN_TURNS_COLOR = new Color(0.8f, 0.8f, 0.3f, 1f);
    private Table actorList;
    private Skin skin;

    private GameCharacter selectedCharacter;
    private boolean updateFromCharChanges = true;

    private Map<GameCharacter, Table> characterToRowMapping = new HashMap<GameCharacter, Table>();

    public InitiativeScreen(World world) {
        super(world, "DungeonBoard");
    }

    @Override protected Actor createContent(Skin skin, final World world) {
        this.skin = skin;

        // Frame
        Table frame = new Table(skin);

        // List of actors
        actorList = new Table(skin);
        actorList.pad(10).defaults().expandX().space(4);

        final ScrollPane scroll = new ScrollPane(actorList, skin);
        //scroll.setColor(new Color(0.3f, 0.25f, 0.2f, 1f));
        scroll.setColor(Color.BLACK);
        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };

        final float widthPc = Gdx.graphics.getWidth() * 0.01f;
        frame.add(scroll).expand().padLeft(widthPc*2).padRight(widthPc*2).fill();

        frame.row().padTop(Gdx.graphics.getHeight() * 0.02f);

        // Edit area header bar
        Table header = new Table(skin);
        final TextField nameEditor = new TextField("", skin);
        header.add(nameEditor).expandX().fill();
        final TextField initiativeEditor = new TextField("", skin);
        initiativeEditor.setMaxLength(3);
       // initiativeEditor.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        header.add(createInitiativeModButton(skin, -10));
        header.add(createInitiativeModButton(skin, -5));
        header.add(createInitiativeModButton(skin, -1));
        header.add(initiativeEditor);
        header.add(createInitiativeModButton(skin, +1));
        header.add(createInitiativeModButton(skin, +5));
        header.add(createInitiativeModButton(skin, +10));

        frame.add(header).expandX().fill();

        final CharacterListener characterListener = new CharacterListener() {
            @Override public void onChanged(GameCharacter character) {
                updateSelectionAreaBar(character, nameEditor, initiativeEditor);
            }
        };

        world.getCurrentEncounter().addListener(new EncounterListenerAdapter() {
            @Override public void onSelectionChanged(Encounter encounter, GameCharacter character) {
                if (character != selectedCharacter) {

                    if (selectedCharacter != null) selectedCharacter.removeListener(characterListener);

                    selectedCharacter = character;

                    if (selectedCharacter != null) selectedCharacter.addListener(characterListener);

                    updateSelectionAreaBar(character, nameEditor, initiativeEditor);
                }
            }
        });

        // Listen to name field
        nameEditor.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                nameEditor.selectAll();
                return true;
            }

            @Override public boolean keyTyped(InputEvent event, char character) {
                final GameCharacter selectedCharacter = world.getCurrentEncounter().getSelectedCharacter();
                if (selectedCharacter != null) {
                    updateFromCharChanges = false;
                    selectedCharacter.setName(nameEditor.getText());
                    updateFromCharChanges = true;
                }

                // Hide onscreen kb on enter
                if (event.getKeyCode() == Input.Keys.ENTER) {
                    final TextField.OnscreenKeyboard onscreenKeyboard = nameEditor.getOnscreenKeyboard();
                    if (onscreenKeyboard != null) {
                        onscreenKeyboard.show(false);
                    }
                }

                return true;
            }
        });

        // Listen to initiative field
        initiativeEditor.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                initiativeEditor.selectAll();
                return true;
            }
            @Override public boolean keyTyped(InputEvent event, char character) {
                // Hide onscreen kb on enter
                if (event.getKeyCode() == Input.Keys.ENTER) {
                    final TextField.OnscreenKeyboard onscreenKeyboard = nameEditor.getOnscreenKeyboard();
                    if (onscreenKeyboard != null) {
                        onscreenKeyboard.show(false);
                    }
                }

                final GameCharacter selectedCharacter = world.getCurrentEncounter().getSelectedCharacter();
                try {
                    final int initiative = Integer.parseInt(initiativeEditor.getText());
                    if (selectedCharacter != null) {
                        updateFromCharChanges = false;
                        selectedCharacter.setInitiative(initiative);
                        updateFromCharChanges = true;
                    }
                }
                catch (NumberFormatException e) {
                    // Don't change initiative
                }
                return true;
            }
        });




        initCharacterList(world);


        // Listen to characters added or removed from encounter
        world.getCurrentEncounter().addListener(new EncounterListenerAdapter() {
            @Override public void onCharacterAdded(Encounter encounter, GameCharacter character) {
                addCharacter(character);
            }

            @Override public void onCharacterRemoved(Encounter encounter, GameCharacter character) {
                // Just recreate list
                initCharacterList(world);
            }

            @Override public void onTurnChanged(Encounter encounter) {
                refreshActionButtons();
                resortCharList();
            }

            @Override public void onSelectionChanged(Encounter encounter, GameCharacter selectedCharacter) {
                refreshActionButtons();
            }
        });

        // TODO: Listen to changes in a character (name, initiative, etc)


        setupActions();

        return frame;
    }

    private void resortCharList() {
        actorList.clear();
        for (GameCharacter character : getWorld().getCurrentEncounter().getCharacters()) {
            final Table row = characterToRowMapping.get(character);
            if (row != null) {
                addCharacterRow(row);
            }
            else {
                addCharacter(character);
            }
        }
    }

    private TextButton createInitiativeModButton(Skin skin, final int change) {
        final TextButton initiativeMod = new TextButton(" " + (change > 0 ? "+" : "") +change + " ", skin);
        initiativeMod.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                if (selectedCharacter != null) {
                    selectedCharacter.changeInitiative(change);
                }
            }
        });
        return initiativeMod;
    }

    private void updateSelectionAreaBar(GameCharacter character, TextField nameEditor, TextField initiativeEditor) {
        if (updateFromCharChanges) {
            if (character == null) {
                nameEditor.setText("Between Turns");
                nameEditor.setColor(BETWEEN_TURNS_COLOR);
                initiativeEditor.setText("");
                nameEditor.setDisabled(true);
                initiativeEditor.setDisabled(true);
            }
            else {
                nameEditor.setText(selectedCharacter.getName());
                nameEditor.setColor(selectedCharacter.getColor());
                initiativeEditor.setText("" + selectedCharacter.getInitiative());
                nameEditor.setDisabled(false);
                initiativeEditor.setDisabled(false);
            }
        }
    }

    private void initCharacterList(World world) {// Add characters in encounter
        actorList.clear();
        characterToRowMapping.clear();

        for (GameCharacter gameCharacter : world.getCurrentEncounter().getCharacters()) {
            addCharacter(gameCharacter);
        }
    }

    private void addCharacter(final GameCharacter gameCharacter) {

        final Table characterRow = new Table(skin);

        // Name
        final String name = gameCharacter.getName();
        final Label nameLabel = new Label(name, skin, StyleSettings.SCRIPT_FONT, StyleSettings.DEFAULT_NAME_COLOR);
        characterRow.add(nameLabel).left().expandX().fillX();

        //slider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.

        // Initiative
        final int initiative = gameCharacter.getInitiative();
        final Label initiativeLabel = new Label("" + initiative, skin, StyleSettings.DIALOG_LARGE_FONT, Color.WHITE);
        characterRow.add(initiativeLabel).padLeft(8).right();

        // Ready action button
        final TextButton readyButton = new TextButton("Ready", skin);
        characterRow.add(readyButton).padLeft(8).right();
        readyButton.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                getWorld().getCurrentEncounter().setCurrentCharacter(gameCharacter);
                StyleSettings.playButtonPressSound();
            }
        });


        // Select character on click
        characterRow.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                //System.out.println("InitiativeScreen.clicked " + gameCharacter.getName());
                getWorld().getCurrentEncounter().setSelectedCharacter(gameCharacter);
            }
        });

        // Update coloring
        updateInitiativeRowColoring(characterRow, nameLabel, initiativeLabel, readyButton, gameCharacter);

        gameCharacter.addListener(new CharacterListener() {
            @Override public void onChanged(GameCharacter character) {
                nameLabel.setText(character.getName());
                //nameLabel.setColor(character.getColor()); // TODO: Use colored icon instead if wanted
                initiativeLabel.setText("" + character.getInitiative());

                // Update coloring
                updateInitiativeRowColoring(characterRow, nameLabel, initiativeLabel, readyButton, gameCharacter);
            }
        });

        getWorld().getCurrentEncounter().addListener(new EncounterListenerAdapter() {
            @Override public void onSelectionChanged(Encounter encounter, GameCharacter selectedCharacter) {
                // Update coloring
                updateInitiativeRowColoring(characterRow, nameLabel, initiativeLabel, readyButton, gameCharacter);
            }

            @Override public void onTurnChanged(Encounter encounter) {
                // Update coloring
                updateInitiativeRowColoring(characterRow, nameLabel, initiativeLabel, readyButton, gameCharacter);
            }
        });

        characterToRowMapping.put(gameCharacter, characterRow);

        addCharacterRow(characterRow);
    }

    private void addCharacterRow(Table characterRow) {
        actorList.row();
        actorList.add(characterRow).expandX().fill();
    }

    private void updateInitiativeRowColoring(Table row,
                                             Label name,
                                             Label initiative,
                                             Button readyButton,
                                             GameCharacter character) {
        final Encounter currentEncounter = getWorld().getCurrentEncounter();
        boolean selected = currentEncounter.getSelectedCharacter() == character;
        boolean inTurn = currentEncounter.getCurrentCharacter() == character;
        boolean disabled  = character.isDisabled();
        boolean hasReadyAction = character.isInReadyAction();
        boolean hasDoneTurn = character.isTurnUsed();

        readyButton.setVisible(hasReadyAction);
        readyButton.setColor(Color.RED);

        // Turn done
        name.setColor(new Color(0.45f, 0.37f, 0.28f, 1));
        initiative.setColor(new Color(0.5f,0.5f,0.5f,1));

        if (!hasDoneTurn) {
            name.setColor(new Color(0.7f, 0.6f, 0.5f, 1));
            initiative.setColor(new Color(0.8f,0.8f,0.8f,1));
        }

        if (selected) {
            name.setColor(new Color(0.65f, 0.7f, 0.97f, 1));
        }

        if (inTurn) {
            name.setColor(new Color(1f, 0.93f, 0.85f, 1));
            initiative.setColor(new Color(1,1,1,1));
        }

        if (disabled) {
            name.setColor(new Color(0.4f, 0.05f, 0.05f, 1).lerp(name.getColor(), 0.5f));
            initiative.setColor(new Color(0.6f,0.2f,0.2f,1));
        }

        if (!character.isPlayerCharacter()) {
            initiative.setColor(new Color(0, 0.8f, 0, 1).lerp(initiative.getColor(), 0.7f));
            name.setColor(new Color(0, 0.8f, 0, 1).lerp(name.getColor(), 0.7f));
        }

        //if (selected) row.setColor(new Color(0.3f, 0.4f, 0.5f, 1));
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

    public void setupActions() {

        // Done
        addAction(new EncounterActionBase("  Done  ", Color.GREEN) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return character != null && hasTurn;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setTurnUsed(true);
                encounter.stepToNextTurn();
            }
        });

        // Round done
        addAction(new EncounterActionBase("Next Round", new Color(0.5f, 1f, 0, 1)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return betweenRounds;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.stepToNextTurn();
            }
        });

        // Free turn
        addAction(new EncounterActionBase("Drop "+POINTS_ABOVE_NEXT_TO_GET_FREE_TURN+" and act again") {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null &&
                       hasTurn &&
                       !encounter.isExtraTurnInitiativeDropUsed() &&
                       encounter.canGetFreeTurn(character, POINTS_ABOVE_NEXT_TO_GET_FREE_TURN);

            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.changeInitiative(-POINTS_ABOVE_NEXT_TO_GET_FREE_TURN);
                character.setTurnUsed(false);
                encounter.stepToNextTurn();
            }
        });

        // Prepare ready action
        addAction(new EncounterActionBase("Ready for Action") {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return hasTurn && !character.isInReadyAction();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setTurnUsed(false);
                encounter.stepToNextTurn();
                character.setInReadyAction(true);
            }
        });

        /*
        // Use ready action
        addAction(new EncounterActionBase("Use Ready Action") {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null && !character.isDisabled() && character.isInReadyAction();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.setCurrentCharacter(character);
            }
        });
        */

        // Disable
        addAction(new EncounterActionBase("Disable") {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null && !character.isDisabled();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setDisabled();
            }
        });

        // Enable
        addAction(new EncounterActionBase("Enable") {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null && character.isDisabled();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setEnabled();
            }
        });

        // Add character
        addAction(new EncounterActionBase("Add PC", Color.OLIVE) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return true;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.addCharacter(new NonPlayerCharacters("Adventurer"));
            }
        });

        // Add others
        addAction(new EncounterActionBase("Add Others", Color.OLIVE) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return true;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.addCharacter(new NonPlayerCharacters("Others"));
            }
        });

        // Remove
        addAction(new EncounterActionBase("Remove", new Color(0.6f, 0, 0, 1f)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.removeCharacter(character);
            }
        });


    }


}
