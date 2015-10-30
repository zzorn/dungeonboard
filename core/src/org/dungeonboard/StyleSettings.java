package org.dungeonboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.*;

/**
 *
 */
public class StyleSettings {

    public static final String APPLICATION_NAME = "Dungeon Board";

    public static final Color TITLE_COLOR = new Color(0.9f, 0.8f, 0.7f, 1f);

    public static final Color DEFAULT_NAME_COLOR = new Color(1f, 0.9f, 0.8f, 1f);

    public static final int NUMBER_OF_ROWS_OF_DIALOG_TEXT_TO_FIT_ON_SCREEN = 19;
    public static final int NUMBER_OF_ROWS_OF_LARGE_DIALOG_TEXT_TO_FIT_ON_SCREEN = 16;
    public static final int NUMBER_OF_ROWS_OF_SCRIPT_TEXT_TO_FIT_ON_SCREEN = 16;

    public static final String DIALOG_FONT_NAME = "135atom_sans";
    public static final String SCRIPT_FONT_NAME = "Augusta";

    public static final String DIALOG_FONT = "default-font";
    public static final String DIALOG_LARGE_FONT = "dialog-large-font";
    public static final String SCRIPT_FONT = "script-font";

    public static final String CLICK_SOUND_NAME = "click.wav";

    public static final Color SELECTED_NAME_COLOR = new Color(0.65f, 0.7f, 0.97f, 1);
    public static final Color SELECTED_NAME_BACKGROUND = new Color(0.5f, 0.6f, 0.8f, 1);
    public static final Color IN_TURN_NAME_COLOR = new Color(1f, 0.93f, 0.85f, 1);
    public static final Color IN_TURN_NAME_BACKGROUND = new Color(0.9f, 0.8f, 0.4f, 1);
    public static final Color MONSTER_COLOR = new Color(0, 0.8f, 0.2f, 1);

    private static final String ICONS_PATH_PREFIX = "icons/";
    public static final String ICONS_PATH = ICONS_PATH_PREFIX +"characters/";
    public static final String ITEM_ICONS_PATH = ICONS_PATH_PREFIX +"items/";

    private static Sound sound;

    public static final List<TextureAtlas.AtlasRegion> ICONS = new ArrayList<TextureAtlas.AtlasRegion>();
    public static final List<TextureAtlas.AtlasRegion> ITEM_ICONS = new ArrayList<TextureAtlas.AtlasRegion>();

    private static final Map<String, TextureAtlas.AtlasRegion> ICONS_MAP = new HashMap<String, TextureAtlas.AtlasRegion>();

    public static void playButtonPressSound() {
        getClickSound().play(0.5f);
        Gdx.input.vibrate(20);
    }

    private static Sound getClickSound() {
        if (sound == null) {
            sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + CLICK_SOUND_NAME));
        }
        return sound;
    }

    protected static void loadIcons(TextureAtlas textureAtlas) {
        for (TextureAtlas.AtlasRegion region : textureAtlas.getRegions()) {
            if (region.name.startsWith(ICONS_PATH)) {
                StyleSettings.ICONS.add(region);
            }
            else if (region.name.startsWith(ITEM_ICONS_PATH)) {
                StyleSettings.ITEM_ICONS.add(region);
            }

            if (region.name.startsWith(ICONS_PATH_PREFIX)) {
                ICONS_MAP.put(region.name.replace(ICONS_PATH_PREFIX, ""), region);
            }
        }
    }

    public static String getRandomIconName() {
        Random random = new Random();

        return ICONS.get(random.nextInt(ICONS.size())).name.replace(ICONS_PATH_PREFIX, "");
    }

    public static String getIconName(TextureAtlas.AtlasRegion icon) {
        return icon.name.replace(ICONS_PATH_PREFIX, "");
    }

    public static TextureAtlas.AtlasRegion getIcon(String iconName) {
        return ICONS_MAP.get(iconName);
    }
}
