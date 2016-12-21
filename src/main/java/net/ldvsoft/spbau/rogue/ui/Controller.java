package net.ldvsoft.spbau.rogue.ui;

import com.googlecode.lanterna.input.KeyStroke;
import net.ldvsoft.spbau.rogue.content.Actions;
import net.ldvsoft.spbau.rogue.content.Generator;
import net.ldvsoft.spbau.rogue.model.Player;
import net.ldvsoft.spbau.rogue.model.Action;
import net.ldvsoft.spbau.rogue.model.Direction;
import net.ldvsoft.spbau.rogue.model.GameStatus;

import java.io.IOException;

/**
 * Playing controller
 */
final class Controller {
    private View view;
    private GameStatus gameStatus;
    private Player player;
    private boolean isRunning = true;

    Controller(Generator generator) throws IOException {
        Player.ControllerPlayerFacade controllerPlayerFacade = new Player.ControllerPlayerFacade() {
            @Override
            public void sendMessage(Player self, String text) {
                view.addMessage(text);
            }

            @Override
            public Action promptAction(Player self) {
                view.tick();
                try {
                    KeyStroke keyStroke = view.getKeystroke();
                    char c;
                    switch (keyStroke.getKeyType()) {
                        case Character:
                            c = keyStroke.getCharacter();
                            break;
                        case Escape:
                            c = 'q';
                            break;
                        case ArrowLeft:
                            c = '4';
                            break;
                        case ArrowRight:
                            c = '6';
                            break;
                        case ArrowUp:
                            c = '8';
                            break;
                        case ArrowDown:
                            c = '2';
                            break;
                        default:
                            c = 0;
                            break;
                    }
                    switch (c) {
                        case 'q':
                            view.addMessage("You have lost hope...");
                            isRunning = false;
                            return null;
                        case '8':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.NORTH));
                        case '7':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.NORTHWEST));
                        case '4':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.WEST));
                        case '3':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.SOUTHEAST));
                        case '2':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.SOUTH));
                        case '1':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.SOUTHWEST));
                        case '6':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.EAST));
                        case '9':
                            return new Actions.StepOrAttackAction(gameStatus, self, self.getPosition().move(Direction.NORTHEAST));
                        default:
                            return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        gameStatus = generator.generateWorld(controllerPlayerFacade, () -> isRunning = false);
        player = generator.getPlayer();
        gameStatus.spawnCreature(player);
        view = new View(this);
    }

    void work() {
        while (isRunning) {
            long time = System.currentTimeMillis();
            view.tick();
            gameStatus.tick();
            time -= System.currentTimeMillis();
            time += 1000 / 60;
            if (time > 0)
                try {
                    Thread.sleep(time);
                } catch (InterruptedException ignored) {
                }
            isRunning = isRunning && player.getHealth() > 0;
        }
        try {
            view.getKeystroke();
        } catch (IOException ignored) {
        }
        view.stop();
    }

    GameStatus getGameStatus() {
        return gameStatus;
    }

    Player getPlayer() {
        return player;
    }
}
