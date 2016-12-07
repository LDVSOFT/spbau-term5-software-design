package net.ldvsoft.spbau.rogue.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LDVSOFT on 02.12.2016.
 */
public class GameStatus {
    private int time = 0;
    private final int height;
    private final int width;
    private final Tile[][] tiles;
    private final List<Creature> creatures = new ArrayList<>();

    public GameStatus(int height, int width) {
        this.height = height;
        this.width = width;
        this.tiles = new Tile[height][width];
    }

    public void tick() {
        for (Tile[] row: tiles)
            for (Tile tile: row)
                tile.tick();
        creatures.stream()
                .filter(creature -> creature.getHealth() > 0)
                .forEach(Creature::tick);
        creatures.removeIf(creature -> creature.getHealth() <= 0);
        time++;
    }

    public int getTime() {
        return time;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Tile getTileAt(Position position) {
        return getTileAt(position.getY(), position.getX());
    }

    public Tile getTileAt(int row, int column) {
        return tiles[row][column];
    }

    public void setTileAt(Position position, Tile tile) {
        setTileAt(position.getY(), position.getX(), tile);
    }

    public void setTileAt(int row, int column, Tile tile) {
        tiles[row][column] = tile;
    }

    public void spawnCreature(Creature creature) {
        creatures.add(creature);
    }

    public List<Creature> getCreatures() {
        return Collections.unmodifiableList(creatures);
    }
}