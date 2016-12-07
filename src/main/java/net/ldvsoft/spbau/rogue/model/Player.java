package net.ldvsoft.spbau.rogue.model;

/**
 * Player creature, controlled by user.
 */
public class Player extends Creature {
    public interface ControllerPlayerProxy {
        void sendMessage(Player self, String text);
        Action promptAction(Player self);
    }

    private ControllerPlayerProxy controller;

    public Player(ControllerPlayerProxy controller, GameStatus gameStatus, Position position, int health) {
        super(gameStatus, position, health);
        this.controller = controller;
        getStat(StatType.MELEE_ATTACK).setBaseValue(5);
        getStat(StatType.VIEW_DISTANCE).setBaseValue(10);
    }

    @Override
    protected Action chooseAction() {
        return controller.promptAction(this);
    }

    @Override
    public String getRenderName() {
        return "player";
    }

    @Override
    public void report(String message) {
        controller.sendMessage(this, message);
    }
}
