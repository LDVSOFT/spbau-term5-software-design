package net.ldvsoft.spbau.rogue.model;

/**
 * Actions performed by creatures.
 */
public interface Action {
    void tick();
    boolean isDone();
}
