package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.Action;
import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.GameStatus;
import net.ldvsoft.spbau.rogue.model.Position;

/**
 * Created by LDVSOFT on 05.12.2016.
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
        getStat(StatType.VIEW_DISTANCE).setBaseValue(15);
    }

    @Override
    protected Action chooseAction() {
        return controller.promptAction(this);
    }

    @Override
    public void report(String message) {
        controller.sendMessage(this, message);
    }
}
