package net.ldvsoft.spbau.rogue.model;

/**
 * Level tile, represents one cell properties.
 */
public interface Tile {
    String getName();
    boolean canWalk(Creature entity);

    boolean canSeeThrough(Creature creature);

    void tick();
}
