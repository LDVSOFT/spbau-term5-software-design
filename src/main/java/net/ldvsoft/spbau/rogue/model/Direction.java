package net.ldvsoft.spbau.rogue.model;

/**
 * Directions in which one can move around.
 */
public enum  Direction {
    NONE     ( 0,  0, 0),
    NORTH    ( 0, -1, 3),
    NORTHWEST(-1, -1, 4),
    WEST     (-1,  0, 3),
    SOUTHWEST(-1, +1, 4),
    SOUTH    ( 0, +1, 3),
    SOUTHEAST(+1, +1, 4),
    EAST     (+1,  0, 3),
    NORTHEAST(+1, -1, 4);

    private final int dx;
    private final int dy;
    private final int weight;

    Direction(int dx, int dy, int weight) {
        this.dx = dx;
        this.dy = dy;
        this.weight = weight;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Direction negate() {
        if (this == NONE)
            return NONE;
        return values()[1 + (ordinal() + 3) % 8];
    }

    public int getWeight() {
        return weight;
    }
}
