package net.ldvsoft.spbau.rogue.model;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Buff, that changes given stat of an entity.
 */
public final class Buff implements Comparable<Buff> {
    public enum BuffType {
        ADD,
        MULTIPLY,
        ASSIGN
    }

    private final BuffType type;
    private final int parameter;
    private final long id = ENUMERATOR.getAndAdd(1);

    private static final AtomicLong ENUMERATOR = new AtomicLong();
    private static final Comparator<Buff> COMPARATOR = Comparator
            .<Buff, BuffType>comparing(buff -> buff.type)
            .thenComparingLong(value -> value.id);

    public Buff(BuffType type, int parameter) {
        this.type = type;
        this.parameter = parameter;
    }

    @Override
    public int compareTo(Buff that) {
        return COMPARATOR.compare(this, that);
    }

    public int apply(int value) {
        switch (type) {
            case ADD:
                return value + parameter;
            case MULTIPLY:
                return value * parameter;
            case ASSIGN:
                return parameter;
            default:
                throw new IllegalArgumentException("Unknown buff type.");
        }
    }
}
