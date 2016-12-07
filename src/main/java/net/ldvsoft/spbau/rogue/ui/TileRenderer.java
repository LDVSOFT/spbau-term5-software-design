package net.ldvsoft.spbau.rogue.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import net.ldvsoft.spbau.rogue.model.Tile;

/**
 * Renders one tile
 */
interface TileRenderer {
    void renderTile(Tile tile, int column, int row, TextGraphics graphics, boolean canSee);
}
