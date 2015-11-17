package org.dungeonboard.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import org.dungeonboard.Context;
import org.dungeonboard.StyleSettings;
import org.dungeonboard.model.World;
import org.dungeonboard.actions.EncounterActionBase;
import org.dungeonboard.actions.GameAction;
import org.dungeonboard.model.*;
import org.dungeonboard.utils.CharacterButton;
import org.dungeonboard.utils.CharacterEditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class InitiativeScreen extends UiScreenBase {

    private static final int POINTS_TO_DROP_FOR_EXTRA_TURN = 10;
    public static final String TITLE = "Initiatives";
    private Table actorList;

    private GameCharacter selectedCharacter;

    private Map<GameCharacter, Table> characterToRowMapping = new HashMap<GameCharacter, Table>();
    private Map<GameCharacter, Table> characterToItemsMapping = new HashMap<GameCharacter, Table>();
    private Drawable glowBackgroundDrawable;
    private Drawable boxBackgroundDrawable;
    private Drawable splatterBackgroundDrawable;
    private Label headerText;

    private CharacterEditor characterEditor;
    private ScrollPane actorScrollPane;


    public InitiativeScreen(Context context) {
        super(context, TITLE);
    }

    @Override protected Actor createContent(final World world, Skin skin, TextureAtlas textureAtlas) {

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
        actorScrollPane = new ScrollPane(actorList, skin);
        actorScrollPane.setColor(Color.BLACK);
        frame.add(actorScrollPane).expand().fill();
        frame.row().padTop(Gdx.graphics.getHeight() * 0.02f);

        // Character editor
        characterEditor = new CharacterEditor(getContext(), Gdx.graphics.getWidth(), true);
        frame.add(characterEditor.getUi()).expandX().fillX();

        // Initialize character list
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

                // Focus the current character
                focusCurrentCharacter(encounter);
            }

            @Override public void onSelectionChanged(Encounter encounter, GameCharacter character) {
                if (character != selectedCharacter) {

                    selectedCharacter = character;
                    characterEditor.setCharacter(character);

                    refreshActionButtons();
                }
            }

            @Override public void onInitiativeChanged(Encounter encounter) {
                System.out.println("InitiativeScreen.onInitiativeChanged");
                resortCharList();
                refreshActionButtons();
            }
        });


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



    private void initCharacterList(World world) {// Add characters in encounter
        actorList.clear();
        characterToRowMapping.clear();
        characterToItemsMapping.clear();

        for (GameCharacter gameCharacter : world.getCurrentEncounter().getCharacters()) {
            addCharacter(gameCharacter);
        }
    }

    private void addCharacter(final GameCharacter gameCharacter) {

        final Skin skin = getSkin();

        final float widthPc = Gdx.graphics.getWidth() * 0.01f;

        final Table characterRow = new Table(skin);

        // Icon
        //gameCharacter.setIcon(StyleSettings.getRandomIconName());
        final CharacterButton characterButton = new CharacterButton(skin, gameCharacter, getTextureAtlas(), widthPc * 10);
        characterRow.add(characterButton).left().padLeft(widthPc * 3);

        // Name
        final String name = gameCharacter.getName();
        final Label nameLabel = new Label(name, skin, StyleSettings.SCRIPT_FONT, StyleSettings.DEFAULT_NAME_COLOR);
        characterRow.add(nameLabel).left().padLeft(widthPc * 1).expandX().fillX();

        // Items
        final Table itemTable = new Table();
        characterToItemsMapping.put(gameCharacter, itemTable);
        characterRow.add(itemTable);
        updateCharacterItems(gameCharacter);

        //slider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.

        // Initiative
        final int initiative = gameCharacter.getInitiative();
        final Label initiativeLabel = new Label("" + initiative, skin, StyleSettings.DIALOG_LARGE_FONT, Color.WHITE);
        characterRow.add(initiativeLabel).padLeft(8).right();

        // Ready action button
        final TextButton readyButton = new TextButton("Ready", skin);
        characterRow.add(readyButton).padLeft(8).padRight(widthPc * 2).right();
        readyButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                gameCharacter.setTurnUsed(false);
                getWorld().getCurrentEncounter().setCurrentCharacter(gameCharacter);
                StyleSettings.playButtonPressSound();
            }
        });


        // Select character on click
        characterRow.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                //System.out.println("InitiativeScreen.clicked " + gameCharacter.getName());
                selectCharacter(gameCharacter);
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

                // Update items
                updateCharacterItems(gameCharacter);
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

    private void updateCharacterItems(GameCharacter gameCharacter) {
        final float widthPc = Gdx.graphics.getWidth() * 0.01f;
        final Table itemsTable = characterToItemsMapping.get(gameCharacter);
        final List<Item> items = gameCharacter.getItems();
        itemsTable.clear();
        for (Item item : items) {
            final Drawable icon = new TextureRegionDrawable(StyleSettings.getIcon(item.getIcon()));
            final ImageButton imageButton = new ImageButton(icon);
            imageButton.getImage().setColor(item.getColor());
            itemsTable.add(imageButton).size(widthPc*6.5f);
        }
    }

    private void selectCharacter(GameCharacter gameCharacter) {
        getWorld().getCurrentEncounter().setSelectedCharacter(gameCharacter);
    }

    private void focusCurrentCharacter(Encounter encounter) {
        if (encounter.getCurrentCharacter() != null) {
            focusCharacter(encounter.getCurrentCharacter());
        }
    }

    private void focusCharacter(GameCharacter currentCharacter) {
        if (actorScrollPane != null) {
            final Table characterRow = characterToRowMapping.get(currentCharacter);
            actorScrollPane.scrollTo(characterRow.getX(),
                                     characterRow.getY(),
                                     characterRow.getWidth(),
                                     characterRow.getHeight());
        }
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
        characterEditor.dispose();
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
                return character != null && hasTurn && !encounter.severalWithSameInitiativeAndTurnNotDone();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setTurnUsed(true);
                encounter.stepToNextTurn(false);
            }
        }, true, false);

        // Drop initiative and act now
        addAction(new EncounterActionBase(" Act Now ", Color.GREEN) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return character != null && hasTurn && encounter.severalWithSameInitiativeAndTurnNotDone();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setTurnUsed(true);
                character.changeInitiative(-1);
                encounter.stepToNextTurn(false);
            }
        }, false, false);

        addAction(new EncounterActionBase("Act Later", new Color(1f, 1f, 0f, 1f)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {
                return character != null && hasTurn && encounter.severalWithSameInitiativeAndTurnNotDone();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setTurnUsed(false);
                final GameCharacter nextCharacter = encounter.getNextCharacter(false);
                nextCharacter.changeInitiative(-1);
                encounter.setSelectedCharacter(nextCharacter);
                encounter.setCurrentCharacter(nextCharacter);
            }
        }, false, true);

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
                encounter.stepToNextTurn(false);
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
        addAction(new EncounterActionBase("Act Twice: -"+ POINTS_TO_DROP_FOR_EXTRA_TURN, new Color(0.2f, 0.8f, 0.2f, 1f)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null &&
                       hasTurn &&
                       !encounter.isExtraTurnInitiativeDropUsed() &&
                       encounter.canGetFreeTurn(character, POINTS_TO_DROP_FOR_EXTRA_TURN);

            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.changeInitiative(-POINTS_TO_DROP_FOR_EXTRA_TURN);
                character.setTurnUsed(false);
                encounter.setExtraTurnInitiativeDropUsed(true);
                encounter.stepToNextTurn(true);
            }
        }, false, false);

        // Prepare ready action
        addAction(new EncounterActionBase("Ready", new Color(1, 0, 0, 1)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return hasTurn && !character.isInReadyAction() && !encounter.severalWithSameInitiativeAndTurnNotDone();
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setTurnUsed(true);
                encounter.stepToNextTurn(false);
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

        // Set turn
        addAction(new EncounterActionBase("Give Turn") {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return character != null && !hasTurn;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                character.setEnabled();
                character.setTurnUsed(false);
                encounter.setCurrentCharacter(character);
            }
        }, false, false);

        // Edit character
        addAction(new EncounterActionBase(" Edit ", new Color(0, 0.2f, 0.8f, 1)) {
            @Override public boolean availableFor(GameCharacter character,
                                                  Encounter encounter,
                                                  boolean hasTurn,
                                                  boolean turnUsed,
                                                  boolean betweenRounds) {

                return getWorld().getCurrentEncounter().getSelectedCharacter() != null;
            }

            @Override public void doAction(World world, GameCharacter character, Encounter encounter) {
                editCharacter(character, false);
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
                final PlayerCharacter adventurer = new PlayerCharacter("Adventurer");
                world.addPlayerCharacter(adventurer);
                editCharacter(adventurer, true);
                selectCharacter(adventurer);
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
                final NonPlayerCharacters other = new NonPlayerCharacters("Others");
                encounter.addCharacter(other);
                editCharacter(other, true);
                selectCharacter(other);
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
                if (character instanceof PlayerCharacter) {
                    encounter.getParty().removeMember((PlayerCharacter) character);
                }
                else {
                    encounter.removeCharacter(character);
                }
            }
        }, false, true);


    }



}
