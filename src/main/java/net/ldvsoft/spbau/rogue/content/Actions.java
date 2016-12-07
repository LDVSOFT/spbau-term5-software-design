package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.*;

/**
 * Generic actions
 */
public class Actions {
    /**
     * Delayed action -- waits some time, then happens once.
     */
    private abstract static class AbstractDelayedAction implements Action {
        private final int delay;
        private int progress = 0;
        private boolean success;

        AbstractDelayedAction(int delay) {
            this.delay = delay;
        }

        public final void tick() {
            if (progress <= delay) {
                ++progress;
            }
            if (progress == delay + 1) {
                success = doTick();
            }
        }

        public final boolean isDone() {
            return progress > delay;
        }

        protected abstract boolean doTick();
    }

    /**
     * Step or attack action: make a step to / attack enemy at a given position.
     */
    public static final class StepOrAttackAction extends AbstractDelayedAction {
        private GameStatus gameStatus;
        private Creature creature;
        private Position target;

        public StepOrAttackAction(GameStatus gameStatus, Creature creature, Position target) {
            super(creature.getStat(Creature.StatType.WALK_DELAY).getValue());
            this.gameStatus = gameStatus;
            this.creature = creature;
            this.target = target;
        }

        @Override
        protected boolean doTick() {
            if (target == creature.getPosition())
                return true;
            Tile targetTile = gameStatus.getTileAt(target);
            if (!targetTile.canWalk(creature)) {
                creature.report("Can't go there, sorry.");
                return false;
            }
            Creature targetCreature = null;
            for (Creature otherCreature: gameStatus.getCreatures()) {
                if (otherCreature != creature && otherCreature.getPosition().equals(target)) {
                    targetCreature = otherCreature;
                    break;
                }
            }
            if (targetCreature == null) {
                creature.setPosition(target);
                creature.report("");
            } else {
                targetCreature.damageHealth(creature.getStat(Creature.StatType.MELEE_ATTACK).getValue());
                creature.report(String.format("Dealt damage, health is at %d.", targetCreature.getHealth()));
            }
            return true;
        }
    }
}
