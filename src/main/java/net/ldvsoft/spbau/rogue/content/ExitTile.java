package net.ldvsoft.spbau.rogue.content;

import net.ldvsoft.spbau.rogue.model.*;

/**
 * Exit tile. Will complete the game when no one is left except the player.
 */
class ExitTile implements Tile {
    public interface ControllerExitProxy {
        void win();
    }

    private final GameStatus gameStatus;
    private final Position position;
    private final ControllerExitProxy controller;

    ExitTile(GameStatus gameStatus, Position position, ControllerExitProxy controller) {
        this.gameStatus = gameStatus;
        this.position = position;
        this.controller = controller;
    }

    @Override
    public String getRenderName() {
        return "exit";
    }

    @Override
    public boolean canWalk(Creature entity) {
        return true;
    }

    @Override
    public boolean canSeeThrough(Creature creature) {
        return true;
    }

    @Override
    public void tick() {
        if (gameStatus.getCreatures().size() != 1)
            return;
        Creature creature = gameStatus.getCreatures().get(0);
        if (creature instanceof Player && creature.getPosition().equals(position)) {
            creature.report("You won!");
            controller.win();
        }
    }

    @Override
    public String getDescription() {
        return "Exit. Will open when this dungeon will be cleaned from cursed souls.";
    }
}
