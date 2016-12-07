package net.ldvsoft.spbau.rogue.model;

/**
 * Created by LDVSOFT on 04.12.2016.
 */
public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position move(Direction direction) {
        return move(direction, 1);
    }

    public Position move(Direction direction, int distance) {
        return new Position(x + direction.getDx() * distance, y + direction.getDy() * distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position))
            return false;
        Position that = (Position) obj;
        return this.x == that.x && this.y == that.y;
    }

    public boolean isInvalid(int width, int height) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }
}
