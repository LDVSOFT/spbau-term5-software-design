package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.Tile;

/**
 * Tiles, pretty simple ones
 */
final class FlyweightTile implements Tile {
    static final Tile EMPTY = new FlyweightTile("empty", false, true);
    static final Tile WALL = new FlyweightTile("wall", false, false);
    static final Tile FLOOR = new FlyweightTile("floor", true, true);

    private final String name;
    private final boolean walkable;
    private final boolean transparent;

    private FlyweightTile(String name, boolean walkable, boolean transparent) {
        this.name = name;
        this.walkable = walkable;
        this.transparent = transparent;
    }

    @Override
    public String getRenderName() {
        return name;
    }

    @Override
    public boolean canWalk(Creature entity) {
        return walkable;
    }

    @Override
    public boolean canSeeThrough(Creature creature) {
        return transparent;
    }

    @Override
    public void tick() {
        //Do nothing!
    }

    @Override
    public String getDescription() {
        return "";
    }
}
