package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.*;

/**
 * Regeneration altar. A little help in a dungeon.
 */
final class RegenerationAltar implements Tile {
    private static final int REGEN_RATE = 3;
    private final GameStatus gameStatus;
    private final Position position;

    RegenerationAltar(GameStatus gameStatus, Position position) {
        this.gameStatus = gameStatus;
        this.position = position;
    }

    @Override
    public String getRenderName() {
        return "altar";
    }

    @Override
    public boolean canWalk(Creature entity) {
        return entity instanceof Player;
    }

    @Override
    public boolean canSeeThrough(Creature creature) {
        return true;
    }

    @Override
    public void tick() {
        for (Creature creature : gameStatus.getCreatures())
            if (creature.getPosition().equals(position))
                creature.healHealth(REGEN_RATE);
    }

    @Override
    public String getDescription() {
        return "Regeneration altar! Rest here to gain health.";
    }
}
