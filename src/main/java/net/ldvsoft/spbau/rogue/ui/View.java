package net.ldvsoft.spbau.rogue.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.GameStatus;
import net.ldvsoft.spbau.rogue.model.Player;
import net.ldvsoft.spbau.rogue.model.Tile;
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
    private final RendererFactory rendererFactory = new RendererFactory();
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
        int rows = screen.getTerminalSize().getRows();
        int columns = screen.getTerminalSize().getColumns();

        GameStatus gameStatus = controller.getGameStatus();
        Player player = controller.getPlayer();

        int displayRows = rows - 1;
        int dislayColumns = columns;
        int baseX = player.getPosition().getX() - dislayColumns / 2;
        int baseY = player.getPosition().getY() - displayRows / 2;
        baseX = min(gameStatus.getWidth() - dislayColumns, baseX);
        baseY = min(gameStatus.getHeight() - displayRows, baseY);
        baseX = max(0, baseX);
        baseY = max(0, baseY);
        for (int i = 0; i < displayRows; i++) {
            for (int j = 0; j < dislayColumns; j++) {
                int mapX = j + baseX;
                int mapY = i + baseY;
                if (mapX < 0 || mapY < 0 || mapX >= gameStatus.getWidth() || mapY >= gameStatus.getHeight()
                        || player.getRememberedMap()[mapY][mapX] == null) {
                    rendererFactory.getTileRenderer("empty").renderTile(null, j, i, graphics, false);
                    continue;
                }

                boolean canSee = player.canSee(mapY, mapX);
                Tile tile = gameStatus.getTileAt(mapY, mapX);
                rendererFactory.getTileRenderer(tile.getRenderName()).renderTile(tile, j, i, graphics, canSee);
                graphics.setForegroundColor(canSee ? TextColor.ANSI.WHITE : TextColor.ANSI.BLUE);
            }
        }
        graphics.setForegroundColor(TextColor.ANSI.RED);
        for (Creature creature: gameStatus.getCreatures()) {
            if (!player.canSee(creature.getPosition())) {
                continue;
            }
            int mapX = creature.getPosition().getX() - baseX;
            int mapY = creature.getPosition().getY() - baseY;
            rendererFactory.getCreatureRenderer(creature.getRenderName()).renderCreature(creature, mapX, mapY, graphics);
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        graphics.fillRectangle(new TerminalPosition(0, displayRows), new TerminalSize(columns, 1), ' ');
        graphics.putString(0, displayRows, lastMessage);
        String status = String.format(
                "HP %d/%d Pos: %d %d Time: %d", player.getHealth(), player.getStat(Creature.StatType.MAX_HEALTH).getValue(),
                player.getPosition().getX(), player.getPosition().getY(), gameStatus.getTime()
        );
        graphics.putString(columns - status.length(), displayRows, status);
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
