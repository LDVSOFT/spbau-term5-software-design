package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.Direction;
import net.ldvsoft.spbau.rogue.model.Position;

import java.util.*;

/**
 * Navigates creatures to points
 */
class Navigator {
    private int d[][];

    private static final Navigator INSTANCE = new Navigator();
    private static final int INF = (int)1e9;

    private Navigator() {
    }

    public static Navigator getInstance() {
        return INSTANCE;
    }

    private final Comparator<Position> positionComparator = new Comparator<Position>() {
        @Override
        public int compare(Position o1, Position o2) {
            int d1 = d[o1.getY()][o1.getX()];
            int d2 = d[o2.getY()][o2.getX()];
            if (d1 != d2)
                return Integer.compare(d1, d2);
            return o1.compareTo(o2);
        }
    };

    Direction goTo(Creature who, Position to) {
        int height = who.getGameStatus().getHeight();
        int width = who.getGameStatus().getWidth();
        d = new int[height][width];
        for (int i = 0; i != height; i++)
            for (int j = 0; j != width; j++)
                d[i][j] = +INF;
        Direction[][] prev = new Direction[height][width];
        NavigableSet<Position> queue = new TreeSet<>(positionComparator);

        d[to.getY()][to.getX()] = 0;
        queue.add(to);

        int tx = who.getPosition().getX();
        int ty = who.getPosition().getY();
        while (!queue.isEmpty() && prev[ty][tx] == null) {
            Position position = queue.pollFirst();
            for (Direction direction: Direction.values()) {
                Position alt = position.move(direction);
                if (alt.isInvalid(width, height))
                    continue;
                // TODO currently, we use full map here. Maybe switch to remembered by a creature?
                // This will require some real interactivity, though
                if (!who.canGoTo(alt, to))
                    continue;
                int altDist = d[position.getY()][position.getX()] + direction.getWeight();
                if (d[alt.getY()][alt.getX()] <= altDist)
                    continue;
                queue.remove(alt);
                d[alt.getY()][alt.getX()] = altDist;
                prev[alt.getY()][alt.getX()] = direction;
                queue.add(alt);
            }
        }
        if (prev[ty][tx] == null)
            return Direction.NONE;
        return prev[ty][tx].negate();
    }
}
