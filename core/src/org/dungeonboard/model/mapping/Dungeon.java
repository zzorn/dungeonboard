package org.dungeonboard.model.mapping;

import org.dungeonboard.model.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dungeon or other location.
 */
public class Dungeon {

    private World world;

    private String name;
    private String description;

    // TODO: Location in world map / other map?

    private final List<MappedArea> mappedAreas = new ArrayList<MappedArea>();

    public Dungeon(World world, String name) {
        this.world = world;
        this.name = name;
    }

    public MappedArea startMappedArea(int level, String name) {
        final MappedArea mappedArea = new MappedArea(this, level, name);

        mappedAreas.add(mappedArea);

        return mappedArea;
    }


    public World getWorld() {
        return world;
    }

    public List<MappedArea> getMappedAreas() {
        return mappedAreas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
