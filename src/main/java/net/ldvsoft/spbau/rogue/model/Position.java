package net.ldvsoft.spbau.rogue.model;

/**
 * Created by LDVSOFT on 04.12.2016.
 */
public final class Position implements Comparable<Position> {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public boolean isInvalid(int width, int height) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }

    @Override
    public int compareTo(Position that) {
        if (this.y != that.y)
            return Integer.compare(this.y, that.y);
        return Integer.compare(this.x, that.x);
    }
}
