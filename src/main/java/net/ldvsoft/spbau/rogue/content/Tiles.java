package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.Tile;

/**
 * Created by LDVSOFT on 04.12.2016.
 */
public class Tiles {
    private static final class FlyweightTile implements Tile {
        private final String name;
        private final boolean walkable;
        private final boolean transparent;

        private FlyweightTile(String name, boolean walkable, boolean transparent) {
            this.name = name;
            this.walkable = walkable;
            this.transparent = transparent;
        }

        @Override
        public String getName() {
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
    }

    static final Tile EMPTY = new FlyweightTile("empty", false, true);
    static final Tile WALL = new FlyweightTile("wall", false, false);
    static final Tile FLOOR = new FlyweightTile("floor", true, true);
}
