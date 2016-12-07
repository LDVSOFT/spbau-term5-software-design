package net.ldvsoft.spbau.rogue.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import net.ldvsoft.spbau.rogue.model.Creature;
import net.ldvsoft.spbau.rogue.model.Tile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.googlecode.lanterna.SGR.*;
import static com.googlecode.lanterna.TextColor.ANSI.*;

/**
 * Factory of renderers!
 */
class RendererFactory {
    private static final class SimpleRenderer implements TileRenderer, CreatureRenderer {
        private static final TextColor FG_UNSEEN = BLUE;
        private static final TextColor BG_UNSEEN = BG_DEFAULT;

        private TextColor fgColor;
        private TextColor bgColor;
        private char c;
        private SGR[] sgrs;

        private SimpleRenderer(TextColor fgColor, TextColor bgColor, char c, SGR... sgrs) {
            this.fgColor = fgColor;
            this.bgColor = bgColor;
            this.c = c;
            this.sgrs = sgrs;
        }

        private void doRender(int column, int row, TextGraphics graphics, boolean canSee) {
            if (canSee) {
                graphics.setForegroundColor(fgColor);
                graphics.setBackgroundColor(bgColor);
            } else {
                graphics.setBackgroundColor(BG_UNSEEN);
                graphics.setForegroundColor(FG_UNSEEN);
            }
            graphics.putString(column, row, String.valueOf(c), Arrays.asList(sgrs));
        }

        @Override
        public void renderTile(Tile tile, int column, int row, TextGraphics graphics, boolean canSee) {
            doRender(column, row, graphics, canSee);
        }

        @Override
        public void renderCreature(Creature creature, int column, int row, TextGraphics graphics) {
            doRender(column, row, graphics, true);
        }
    }

    private static final TextColor BG_DEFAULT = BLACK;
    private static final Map<String, TileRenderer> TILE_RENDERER_MAP = new HashMap<>();
    private static final SimpleRenderer NO_TILE_RENDERER = new SimpleRenderer(WHITE, RED, '?', BOLD);
    private static final Map<String, CreatureRenderer> CREATURE_RENDERER_MAP = new HashMap<>();
    private static final SimpleRenderer NO_CREATURE_RENDERER = new SimpleRenderer(RED, BG_DEFAULT, '?', BOLD);

    static {
        TILE_RENDERER_MAP.put("empty", new SimpleRenderer(DEFAULT, BG_DEFAULT, ' '));
        TILE_RENDERER_MAP.put("wall" , new SimpleRenderer(WHITE  , BG_DEFAULT, '#'));
        TILE_RENDERER_MAP.put("floor", new SimpleRenderer(DEFAULT, BG_DEFAULT, '.'));
        TILE_RENDERER_MAP.put("altar", new SimpleRenderer(YELLOW , WHITE     , '+', BOLD));
        TILE_RENDERER_MAP.put("exit" , new SimpleRenderer(GREEN  , WHITE     , 'E', BOLD));

        CREATURE_RENDERER_MAP.put("player" , new SimpleRenderer(WHITE, BG_DEFAULT, '@', BOLD));
        CREATURE_RENDERER_MAP.put("monster", new SimpleRenderer(RED  , BG_DEFAULT, 'Z', BOLD));
    }

    TileRenderer getTileRenderer(String name) {
        return TILE_RENDERER_MAP.getOrDefault(name, NO_TILE_RENDERER);
    }

    CreatureRenderer getCreatureRenderer(String name) {
        return CREATURE_RENDERER_MAP.getOrDefault(name, NO_CREATURE_RENDERER);
    }
}
