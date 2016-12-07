package net.ldvsoft.spbau.rogue.model;

import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * One stat of a character
 */
public class Stat {
    private int baseValue;
    private final NavigableSet<Buff> buffs = new TreeSet<>();
    private final Creature owner;

    public Stat(Creature owner, int baseValue) {
        this.owner = owner;
        this.baseValue = baseValue;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
    }

    public int getValue() {
        int value = baseValue;
        for (Buff buff: buffs) {
            value = buff.apply(value);
        }
        return value;
    }
}
