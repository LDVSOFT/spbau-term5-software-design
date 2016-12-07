package net.ldvsoft.spbau.rogue.model;

import java.sql.Array;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creature - player, monster, or any other crazy creature
 */
public abstract class Creature {
    public enum StatType {
        MAX_HEALTH,
        MELEE_ATTACK,
        WALK_DELAY,
        VIEW_DISTANCE
    }

    private Position position;
    private int health;
    private Action performingAction;
    private final Map<StatType, Stat> stats = new EnumMap<>(StatType.class);
    private final GameStatus gameStatus;
    private final Tile rememberedMap[][];

    public Creature(GameStatus gameStatus, Position position, int health) {
        this.gameStatus = gameStatus;
        this.position = position;
        this.rememberedMap = new Tile[gameStatus.getHeight()][gameStatus.getWidth()];

        getStat(StatType.MAX_HEALTH).setBaseValue(health);
        this.health = health;

        updateMap();
    }

    final void tick() {
        updateMap();
        if (performingAction == null) {
            performingAction = chooseAction();
            if (performingAction == null) {
                // Ok then, no action
                return;
            }
        }
        performingAction.tick();
        if (performingAction.isDone()) {
            performingAction = null;
        }
    }

    protected abstract Action chooseAction();

    public boolean canSee(int y, int x) {
        double dy = y - position.getY();
        double dx = x - position.getX();
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > getStat(StatType.VIEW_DISTANCE).getValue())
            return false;
        int iterations = (int) Math.ceil(distance / 0.4);
        for (int i = 0; i < iterations - 1; i++) {
            int tx = position.getX() + (int)Math.round(dx / iterations * i);
            int ty = position.getY() + (int)Math.round(dy / iterations * i);
            if (tx == x && ty == y)
                break;
            if (!gameStatus.getTileAt(ty, tx).canSeeThrough(this))
                return false;
        }
        return true;
    }

    public boolean canSee(Position pos) {
        return canSee(pos.getY(), pos.getX());
    }

    public boolean canGoTo(Position pos, Position... allowedCreaturesPositions) {
        if (!gameStatus.getTileAt(pos).canWalk(this))
            return false;
        if (Arrays.asList(allowedCreaturesPositions).contains(pos))
            return true;
        for (Creature creature: gameStatus.getCreatures())
            if (creature.getPosition() == pos)
                return false;
        return true;
    }

    public List<Creature> getSeenCreatures() {
        return gameStatus.getCreatures()
                .stream()
                .filter(creature -> canSee(creature.getPosition()))
                .collect(Collectors.toList());
    }

    private void updateMap() {
        for (int i = 0; i != gameStatus.getHeight(); i++) {
            for (int j = 0; j != gameStatus.getWidth(); j++) {
                if (canSee(i, j))
                    rememberedMap[i][j] = gameStatus.getTileAt(i, j);
            }
        }
    }

    public Tile[][] getRememberedMap() {
        return rememberedMap;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Stat getStat(StatType type) {
        if (!stats.containsKey(type))
            stats.put(type, new Stat(this, 0));
        return stats.get(type);
    }

    public int getHealth() {
        return health;
    }

    public void damageHealth(int damage) {
        health -= damage;
        if (health < 0)
            report("You died!");
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void report(String message) {
    }
}
