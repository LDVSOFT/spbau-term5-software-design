package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.*;

/**
 * Created by LDVSOFT on 07.12.2016.
 */
public class Enemies {
    public static final class Monster extends Creature {
        private Creature target = null;

        private static final int START_HEALTH = 10;

        public Monster(GameStatus gameStatus, Position position) {
            super(gameStatus, position, START_HEALTH);
            getStat(StatType.MELEE_ATTACK).setBaseValue(4);
            getStat(StatType.VIEW_DISTANCE).setBaseValue(10);
        }

        @Override
        protected Action chooseAction() {
            if (target != null) {
                if (canSee(target.getPosition()) && target.getPosition() != target.getPosition()) {
                    target = null;
                }
            }
            if (target == null)
                for (Creature creature: getSeenCreatures())
                    if (creature instanceof Player) {
                        target = creature;
                        break;
                    }
            if (target == null)
                return null;
            Direction toGo = Navigator.goTo(this, target.getPosition());
            return new Actions.StepOrAttackAction(getGameStatus(), this, getPosition().move(toGo));
        }
    }
}
