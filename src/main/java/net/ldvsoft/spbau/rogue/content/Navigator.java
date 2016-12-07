package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.Direction;
import net.ldvsoft.spbau.rogue.model.Position;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Navigates creatures to points
 */
class Navigator {
    static Direction goTo(Creature who, Position to) {
        Queue<Position> queue = new ArrayDeque<>();
        int height = who.getGameStatus().getHeight();
        int width = who.getGameStatus().getWidth();
        Direction prev[][] = new Direction[height][width];
        queue.add(to);
        int tx = who.getPosition().getX();
        int ty = who.getPosition().getY();
        while (!queue.isEmpty() && prev[ty][tx] == null) {
            Position position = queue.poll();
            for (Direction d: Direction.values()) {
                Position alt = position.move(d);
                if (alt.isInvalid(width, height))
                    continue;
                // TODO currently, we use full map here. Maybe switch to remembered by a creature?
                if (!who.getGameStatus().getTileAt(alt).canWalk(who))
                    continue;
                if (prev[alt.getY()][alt.getX()] != null)
                    continue;
                prev[alt.getY()][alt.getX()] = d;
                queue.add(alt);
            }
        }
        if (prev[ty][tx] == null)
            return Direction.NONE;
        return prev[ty][tx].negate();
    }
}
