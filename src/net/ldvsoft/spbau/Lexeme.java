package net.ldvsoft.spbau;

/**
 * Shell command lexeme.
 */
/*package*/ class Lexeme {
    /**
     * Lexeme type enum
     * 1. SPACE - spaces
     * 2. BARE - bare word, unquoted
     * 3. QUOTED - single quoted string (with ')
     * 4. DOUBLE_QUOTED - double quoted string (with ")
     * 5. PIPE - pipe symbol (only `|' has it)
     * 6. VARIABLE - variable name
     */
    /*package*/ enum LexemeType {
        SPACE,
        BARE,
        QUOTED,
        DOUBLE_QUOTED,
        PIPE,
        VARIABLE,
    }

    private LexemeType lexemeType;
    private String lexeme;

    /*package*/ Lexeme(LexemeType lexemeType, String lexeme) {
        this.lexemeType = lexemeType;
        this.lexeme = lexeme;
    }

    /*package*/ LexemeType getLexemeType() {
        return lexemeType;
    }

    /*package*/ String getLexeme() {
        return lexeme;
    }

    /**
     * String representation of lexeme, just for debug purposes.
     * To gain the real lexeme content, use @see getLexeme method
     * @return string representation of lexeme
     */
    @Override
    public String toString() {
        switch (lexemeType) {
            case SPACE:
                return String.format("[spaces:%s]", lexeme);
            case BARE:
            case PIPE: // Pipe is actually "|"
                return lexeme;
            case QUOTED:
                return String.format("\'%s\'", lexeme);
            case DOUBLE_QUOTED:
                return String.format("\"%s\"", lexeme);
            case VARIABLE:
                return String.format("$%s", lexeme);
        }
        return null;
    }
}
