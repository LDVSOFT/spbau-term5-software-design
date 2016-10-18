package net.ldvsoft.spbau;

/**
 * Syntax error exception. It reports different errors when shell cannot
 * parse command.
 */
public class SyntaxError extends Exception {
    public SyntaxError(String message) {
        super(message);
    }
}
