package net.ldvsoft.spbau;

/**
 * Created by ldvsoft on 08.09.16.
 */
/*package*/ class Lexeme {
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

    Lexeme(LexemeType lexemeType, String lexeme) {
        this.lexemeType = lexemeType;
        this.lexeme = lexeme;
    }

    public LexemeType getLexemeType() {
        return lexemeType;
    }

    public String getLexeme() {
        return lexeme;
    }

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
