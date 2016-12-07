package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.*;

/**
 * Very simple monster.
 */
final class Monster extends Creature {
    private Creature target = null;

    private static final int START_HEALTH = 10;

    Monster(GameStatus gameStatus, Position position) {
        super(gameStatus, position, START_HEALTH);
        getStat(StatType.MELEE_ATTACK).setBaseValue(4);
        getStat(StatType.VIEW_DISTANCE).setBaseValue(6);
    }

    @Override
    protected Action chooseAction() {
        // Check that our target is still there
        if (target != null) {
            if (canSee(target.getPosition()) && target.getPosition() != target.getPosition()) {
                target = null;
            }
        }
        // Find new target
        if (target == null)
            for (Creature creature : getSeenCreatures())
                if (creature instanceof Player) {
                    target = creature;
                    break;
                }
        if (target == null)
            return null;
        // Finally, go and get it!
        Direction whereToGo = Navigator.getInstance().goTo(this, target.getPosition());
        return new Actions.StepOrAttackAction(getGameStatus(), this, getPosition().move(whereToGo));
    }

    @Override
    public String getRenderName() {
        return "monster";
    }
}
