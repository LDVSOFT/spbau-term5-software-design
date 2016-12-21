package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * Generates game level
 */
public final class Generator {
    private static final int BASE_HEALTH = 30;
    private static final int ROOM_SIZE_BASE = 9;
    private static final double ROOM_SIZE_DEV = 2;
    private static final double ROOMS_PART = 1.6;
    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
    private static final int MONSTERS = 10;
    private static final int ALTARS = 5;
    private int height;
    private int width;
    private Random random;

    private GameStatus gameStatus;
    private Player player;
    private boolean was[][];

    public Generator(int height, int width, long seed) {
        this.height = height;
        this.width = width;
        this.random = new Random(seed);
    }

    private void placeWall(int y, int x) {
        if (gameStatus.getTileAt(y, x) == FlyweightTile.EMPTY)
            gameStatus.setTileAt(y, x, FlyweightTile.WALL);
    }

    private void bfs(Position start) {
        Queue<Position> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Position position = queue.poll();
            if (was[position.getY()][position.getX()])
                continue;
            was[position.getY()][position.getX()] = true;
            if (gameStatus.getTileAt(position) == FlyweightTile.WALL)
                continue;
            for (Direction d: Direction.values()) {
                Position alt = position.move(d);
                if (alt.isInvalid(width, height))
                    continue;
                if (gameStatus.getTileAt(alt) == FlyweightTile.EMPTY)
                    continue;
                queue.add(alt);
            }
        }
    }

    public GameStatus generateWorld(
            Player.ControllerPlayerFacade controllerPlayerFacade,
            ExitTile.ControllerExitFacade exitProxy
    ) {
        LOGGER.info("Generating world {} x {}...", height, width);

        gameStatus = new GameStatus(height, width);
        int roomsCnt = (int) Math.round(width * height / (ROOM_SIZE_BASE * ROOM_SIZE_BASE) * ROOMS_PART);

        for (int i = 0; i != height; i++) {
            for (int j = 0; j != width; j++) {
                gameStatus.setTileAt(i, j, FlyweightTile.EMPTY);
            }
        }
        for (int it = 0; it != roomsCnt; it++) {
            int roomWidth = (int) Math.round(random.nextGaussian() * ROOM_SIZE_DEV + ROOM_SIZE_BASE);
            int roomHeight = (int) Math.round(random.nextGaussian() * ROOM_SIZE_DEV + ROOM_SIZE_BASE);
            if (roomWidth < 3 || roomHeight < 3) {
                continue;
            }
            int roomX = random.nextInt(width - roomWidth);
            int roomY = random.nextInt(height - roomHeight);
            for (int i = 1; i < roomHeight - 1; i++) {
                for (int j = 1; j < roomWidth - 1; j++) {
                    gameStatus.setTileAt(roomY + i, roomX + j, FlyweightTile.FLOOR);
                }
            }
            for (int i = 0; i != roomHeight; i++) {
                placeWall(roomY + i, roomX);
                placeWall(roomY + i, roomX + roomWidth - 1);
            }
            for (int j = 0; j != roomWidth; j++) {
                placeWall(roomY, roomX + j);
                placeWall(roomY + roomHeight - 1, roomX + j);
            }
        }
        player = generatePlayer(controllerPlayerFacade);
        was = new boolean[height][width];
        bfs(player.getPosition());
        for (int i = 0; i != height; i++) {
            for (int j = 0; j != width; j++) {
                if (!was[i][j])
                    gameStatus.setTileAt(i, j, FlyweightTile.EMPTY);
            }
        }
        for (int i = 0; i != MONSTERS; i++)
            gameStatus.spawnCreature(new Monster(gameStatus, getPosition()));
        for (int i = 0; i != ALTARS; i++) {
            Position position = getPosition();
            gameStatus.setTileAt(position, new RegenerationAltar(gameStatus, position));
        }
        Position position = getPosition();
        gameStatus.setTileAt(position, new ExitTile(gameStatus, position, exitProxy));
        return gameStatus;
    }

    private Position getPosition() {
        Position position = null;
        while (position == null) {
            position = new Position(random.nextInt(width), random.nextInt(height));
            if (gameStatus.getTileAt(position) != FlyweightTile.FLOOR)
                position = null;
            for (Creature c: gameStatus.getCreatures())
                if (c.getPosition() == position) {
                    position = null;
                    break;
                }
        }
        return position;
    }

    private Player generatePlayer(Player.ControllerPlayerFacade controllerPlayerFacade) {
        LOGGER.info("Generating player...");
        return new Player(controllerPlayerFacade, gameStatus, getPosition(), BASE_HEALTH);
    }

    public Player getPlayer() {
        return player;
    }
}
