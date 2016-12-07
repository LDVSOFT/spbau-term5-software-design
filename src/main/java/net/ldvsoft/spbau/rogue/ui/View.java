package net.ldvsoft.spbau.rogue.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import net.ldvsoft.spbau.rogue.model.Player;
import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Main view
 */
class View {
    private static final Logger LOGGER = LoggerFactory.getLogger(View.class);
    private Controller controller;
    private Screen screen;
    private TextGraphics graphics;
    private String lastMessage = "";

    View(Controller controller) throws IOException {
        Terminal terminal = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(80, 50))
                .createTerminal();
        screen = new TerminalScreen(terminal);
        screen.setCursorPosition(null);
        screen.startScreen();
        graphics = screen.newTextGraphics();
        this.controller = controller;
    }

    void tick() {
        screen.doResizeIfNecessary();
        screen.clear();
        int rows = screen.getTerminalSize().getRows();
        int columns = screen.getTerminalSize().getColumns();
        GameStatus gameStatus = controller.getGameStatus();
        Player player = controller.getPlayer();

        int baseX = player.getPosition().getX() - columns / 2;
        int baseY = player.getPosition().getY() - (rows - 1) / 2;
        baseX = min(gameStatus.getWidth() - columns, baseX);
        baseY = min(gameStatus.getHeight() - rows + 1, baseY);
        baseX = max(0, baseX);
        baseY = max(0, baseY);
        int displayedWidth = min(columns, gameStatus.getWidth() - baseX);
        int displayedHeight = min(rows - 1, gameStatus.getHeight() - baseY);
        for (int i = 0; i != displayedHeight; i++) {
            for (int j = 0; j != displayedWidth; j++) {
                int mapX = j + baseX;
                int mapY = i + baseY;
                if (player.getRememberedMap()[mapY][mapX] == null) {
                    continue;
                }
                boolean canSee = player.canSee(mapY, mapX);
                graphics.setForegroundColor(canSee ? TextColor.ANSI.WHITE : TextColor.ANSI.BLUE);
                switch (gameStatus.getTileAt(mapY, mapX).getName()) {
                    case "wall":
                        graphics.putString(j, i, "#");
                        break;
                    case "floor":
                        graphics.putString(j, i, ".");
                        break;
                    case "empty":
                        graphics.putString(j, i, " ");
                        break;
                    default:
                        graphics.setForegroundColor(TextColor.ANSI.RED);
                        graphics.putString(j, i, "?");
                        break;
                }
            }
        }
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        for (Creature creature: player.getSeenCreatures()) {
            graphics.putString(creature.getPosition().getX() - baseX, creature.getPosition().getY() - baseY, "X", SGR.BOLD);
        }
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(player.getPosition().getX() - baseX, player.getPosition().getY() - baseY, "@", SGR.BOLD);
        graphics.putString(0, rows - 1, lastMessage);
        String status = String.format(
                "HP %d/%d Pos: %d %d Time: %d", player.getHealth(), player.getStat(Creature.StatType.MAX_HEALTH).getValue(),
                player.getPosition().getX(), player.getPosition().getY(), gameStatus.getTime()
        );
        graphics.putString(columns - status.length(), rows - 1, status);
        try {
            screen.refresh();
        } catch (IOException e) {
            LOGGER.warn("Error refreshing the screen", e);
        }
    }

    void addMessage(String message) {
        lastMessage = message;
        tick();
    }

    KeyStroke getKeystroke() throws IOException {
        return screen.readInput();
    }

    void stop() {
        try {
            screen.stopScreen();
        } catch (IOException e) {
            LOGGER.warn("At stop()", e);
        }
    }
}
