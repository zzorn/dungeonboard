package org.dungeonboard.screens.initiative;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
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
    private final TextureAtlas textureAtlas;
    private Table actorList;
    private Skin skin;

    private GameCharacter selectedCharacter;
    private boolean updateFromCharChanges = true;

    private Map<GameCharacter, Table> characterToRowMapping = new HashMap<GameCharacter, Table>();
    private Drawable glowBackgroundDrawable;
    private Drawable boxBackgroundDrawable;
    private Drawable splatterBackgroundDrawable;
    private Label headerText;

    public InitiativeScreen(World world, TextureAtlas textureAtlas) {
        super(world, "Dunskulauta");
        this.textureAtlas = textureAtlas;
    }

    @Override protected Actor createContent(Skin skin, final World world) {
        this.skin = skin;

        glowBackgroundDrawable = new TextureRegionDrawable(textureAtlas.findRegion("selection_glow"));
        boxBackgroundDrawable = new NinePatchDrawable(textureAtlas.createPatch("selection_outline"));
        splatterBackgroundDrawable = new TiledDrawable(textureAtlas.findRegion("splatter"));

        final float heightPc = Gdx.graphics.getHeight() * 0.01f;
        final float widthPc = Gdx.graphics.getWidth() * 0.01f;

        // Frame
        Table frame = new Table(skin);

        // Top bar with round number
        Table topBar = new Table(skin);
        frame.add(topBar).top().expandX().padBottom(heightPc * 0.5f);
        headerText = new Label(" ", skin);
        headerText.setColor(new Color(0.7f, 0.6f, 0.5f, 1f));
        topBar.add(headerText);
        frame.row();

        // List of actors
        actorList = new Table(skin);

        final ScrollPane scroll = new ScrollPane(actorList, skin);
        //scroll.setColor(new Color(0.3f, 0.25f, 0.2f, 1f));
        scroll.setColor(Color.BLACK);
        /*
        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        };
        */

        frame.add(scroll).expand().fill();

        frame.row().padTop(Gdx.graphics.getHeight() * 0.02f);

        // Edit area header bar
        Table header = new Table(skin);
        final TextField nameEditor = new TextField("", skin);
        header.add(nameEditor).expandX().fill();
        final TextField initiativeEditor = new TextField("", skin);
        initiativeEditor.setMaxLength(3);
       // initiativeEditor.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        header.add(createInitiativeModButton(skin, -10, new Color(0.5f, 0.5f, 1f, 1)));
        header.add(createInitiativeModButton(skin, -5, new Color(0.6f, 0.6f, 1f, 1)));
        header.add(createInitiativeModButton(skin, -1, new Color(0.7f, 0.7f, 1f, 1)));
        header.add(initiativeEditor).width(widthPc * 10).center();
        header.add(createInitiativeModButton(skin, +1, new Color(1f, 0.7f, 0.7f, 1)));
        header.add(createInitiativeModButton(skin, +5, new Color(1f, 0.6f, 0.6f, 1)));
        header.add(createInitiativeModButton(skin, +10, new Color(1f, 0.5f, 0.5f, 1)));

        frame.add(header).expandX().fill();

        final CharacterListener characterListener = new CharacterListener() {
            @Override public void onChanged(GameCharacter character) {
                updateSelectionAreaBar(character, nameEditor, initiativeEditor);
            }

            @Override public void onInitiativeChanged(GameCharacter character) {
            }
        };

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


        // Listen to encounter
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
                updateHeaderText(encounter);
            }

            @Override public void onSelectionChanged(Encounter encounter, GameCharacter character) {
                if (character != selectedCharacter) {

                    if (selectedCharacter != null) selectedCharacter.removeListener(characterListener);

                    selectedCharacter = character;

                    if (selectedCharacter != null) selectedCharacter.addListener(characterListener);

                    updateSelectionAreaBar(character, nameEditor, initiativeEditor);

                    refreshActionButtons();
                }
            }


            @Override public void onInitiativeChanged(Encounter encounter) {
                System.out.println("InitiativeScreen.onInitiativeChanged");
                resortCharList();
            }
        });

        // TODO: Listen to changes in a character (name, initiative, etc)


        setupActions();

        updateHeaderText(world.getCurrentEncounter());

        return frame;
    }

    private void updateHeaderText(Encounter encounter) {
        final int round = encounter.getRoundNumber();
        if (encounter.isBetweenTurns()) {
            if (round == 0) {
                headerText.setText("Battle Starting");
            }
            else {
                headerText.setText("Before Round " + (round + 1));
            }
        }
        else {
            headerText.setText("Round " + round);
        }
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

    private TextButton createInitiativeModButton(Skin skin, final int change, Color color) {
        final TextButton initiativeMod = new TextButton(" " + (change > 0 ? "+" : "") +change + " ", skin);
        initiativeMod.setColor(color);
        initiativeMod.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                if (selectedCharacter != null) {
                    selectedCharacter.changeInitiative(change);
                    StyleSettings.playButtonPressSound();
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

        final float widthPc = Gdx.graphics.getWidth() * 0.01f;

        final Table characterRow = new Table(skin);

        // Name
        final String name = gameCharacter.getName();
        final Label nameLabel = new Label(name, skin, StyleSettings.SCRIPT_FONT, StyleSettings.DEFAULT_NAME_COLOR);
        characterRow.add(nameLabel).left().padLeft(widthPc * 3).expandX().fillX();

        //slider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.

        // Initiative
        final int initiative = gameCharacter.getInitiative();
        final Label initiativeLabel = new Label("" + initiative, skin, StyleSettings.DIALOG_LARGE_FONT, Color.WHITE);
        characterRow.add(initiativeLabel).padLeft(8).right();

        // Ready action button
        final TextButton readyButton = new TextButton("Ready", skin);
        characterRow.add(readyButton).padLeft(8).padRight(widthPc * 2).right();
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

            @Override public void onInitiativeChanged(GameCharacter character) {
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
                                             TextButton readyButton,
                                             GameCharacter character) {
        final Encounter currentEncounter = getWorld().getCurrentEncounter();
        boolean selected = currentEncounter.getSelectedCharacter() == character;
        boolean inTurn = currentEncounter.getCurrentCharacter() == character;
        boolean disabled  = character.isDisabled();
        boolean hasReadyAction = character.isInReadyAction();
        boolean hasDoneTurn = character.isTurnUsed();

        readyButton.setVisible(hasReadyAction);
        if (hasReadyAction) {
            readyButton.setColor(Color.RED);
            readyButton.setDisabled(false);
            readyButton.setText("Ready");
        }


        row.setBackground("white");
        row.setColor(Color.BLACK);

        // Turn done
        name.setColor(new Color(0.55f, 0.45f, 0.3f, 1));
        initiative.setColor(new Color(0.5f,0.5f,0.5f,1));

        if (!hasDoneTurn) {
            name.setColor(new Color(0.9f, 0.8f, 0.7f, 1));
            initiative.setColor(new Color(0.9f,0.9f,0.9f,1));
        }

        if (inTurn) {
            row.setBackground(glowBackgroundDrawable);
            row.setColor(StyleSettings.IN_TURN_NAME_BACKGROUND);
            name.setColor(StyleSettings.IN_TURN_NAME_COLOR);
            initiative.setColor(new Color(1, 1, 1, 1));
        }

        if (disabled) {
            row.setBackground(splatterBackgroundDrawable);
            row.setColor(new Color(0.3f, 0.3f, 0.3f, 1));

            /*
            readyButton.setVisible(true);
            readyButton.setColor(Color.GRAY);
            readyButton.setDisabled(true);
            readyButton.setText("Disabled");
            */

            name.setColor(new Color(0.5f, 0.5f, 0.5f, 1));
            initiative.setColor(new Color(0.4f, 0.4f, 0.4f, 1));
        }

        if (selected && !inTurn) {
            row.setBackground(boxBackgroundDrawable);
            row.setColor(StyleSettings.SELECTED_NAME_BACKGROUND);
            //name.setColor(StyleSettings.SELECTED_NAME_COLOR);
        }

        if (!character.isPlayerCharacter()) {
            initiative.setColor(new Color(StyleSettings.MONSTER_COLOR).lerp(initiative.getColor(), 0.7f));
            name.setColor(new Color(StyleSettings.MONSTER_COLOR).lerp(name.getColor(), 0.7f));
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
        addAction(new EncounterActionBase("    Done    ", Color.GREEN) {
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
        }, true, false);

        // Round done
        addAction(new EncounterActionBase("Next Round", new Color(1f, 1f, 0, 1)) {
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
        }, true, false);

        // Select active character
        addAction(new EncounterActionBase("Resume", new Color(0.3f, 0.6f, 0.9f, 1)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return !hasTurn && !betweenRounds;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.setSelectedCharacter(encounter.getCurrentCharacter());
            }
        }, true, false);

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
                encounter.setExtraTurnInitiativeDropUsed(true);
                encounter.stepToNextTurn();
            }
        }, false, false);

        // Prepare ready action
        addAction(new EncounterActionBase("Ready", new Color(1, 0, 0, 1)) {
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
        }, false, false);

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
        }, false, false);

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
        }, false, false);

        // Add character
        addAction(new EncounterActionBase("Add PC", new Color(0.8f, 0.7f, 0.3f, 1)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return true;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.addCharacter(new PlayerCharacter("Adventurer"));
            }
        }, false, true);

        // Add others
        addAction(new EncounterActionBase("Add Others", new Color(0.3f, 0.8f, 0.6f, 1)) {
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
        }, false, true);

        // Reset encounter
        addAction(new EncounterActionBase("Reset", new Color(0.6f, 0f, 0.6f, 1f)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return true;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                encounter.reset();
            }
        }, false, true);

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
        }, false, true);


    }


}
