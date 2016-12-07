package net.ldvsoft.spbau.rogue.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import net.ldvsoft.spbau.rogue.model.Creature;

/**
 * Renders one creature
 */
interface CreatureRenderer {
    void renderCreature(Creature creature, int column, int row, TextGraphics graphics);
}
