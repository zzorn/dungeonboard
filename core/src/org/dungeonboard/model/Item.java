package org.dungeonboard.model;

import com.badlogic.gdx.graphics.Color;

/**
 * Items that characters can have that should be kept track of, such as lightsources.
 */
public class Item {

    private String name;
    private String icon;
    private Color color = new Color(1, 1, 1, 1);

    public Item(String name) {
        this(name, null);
    }

    public Item(String name, String icon) {
        this(name, icon, new Color(1,1,1,1));
    }

    public Item(String name, String icon, Color color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
