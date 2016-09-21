package net.ldvsoft.spbau;

import net.ldvsoft.spbau.Lexeme.LexemeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Shell command lexer.
 * Splits strings to lexemes in different ways that are required by steps of command parsing.
 */
/*package*/ class Lexer {
    private String s;
    private int n;
    private int i;

    /**
     * Splits command into it's generic parts -- pipe elements and words (bare, quoted, ...)
     * @param command command
     * @return list of lexemes
     * @throws SyntaxError in case quotes cannot be paired
     */
    /*package*/ List<Lexeme> lexCommand(String command) throws SyntaxError {
        init(command);
        List<Lexeme> result = new ArrayList<>();
        for (i = 0; i != n; i++) {
            char c = s.charAt(i);
            if (isSpace(c)) {
                result.add(nextLexeme(false, Lexer::isSpace, LexemeType.SPACE));
                continue;
            }
            if (c == '|') {
                result.add(new Lexeme(LexemeType.PIPE, "|"));
                continue;
            }
            if ("\'\"".indexOf(c) != -1) {
                Lexeme lexeme = nextLexeme(
                        true,
                        character -> character != c,
                        (c == '\'' ? LexemeType.QUOTED : LexemeType.DOUBLE_QUOTED)
                );
                if (lexeme == null) {
                    throw new SyntaxError(String.format("Unclosed bracket %c at position %d", c, i + 1));
                }
                result.add(lexeme);
                continue;
            }
            /* bare word */ {
                result.add(nextLexeme(false, Lexer::isWord, LexemeType.BARE));
            }
        }
        return result;
    }

    /**
     * Finds variable names in given string.
     * That is used in the substitution of variable values.
     * @param str string to find variable names withing
     * @return lexified string: bare words and variable names
     */
    /*package*/ List<Lexeme> lexStringForSubstitutions(String str) {
        init(str);
        List<Lexeme> result = new ArrayList<>();
        while (i != n) {
            /* non-variable */ {
                int j = i;
                while (j != n && s.charAt(j) != '$') {
                    j++;
                }
                if (i != j) {
                    result.add(new Lexeme(LexemeType.BARE, s.substring(i, j)));
                    i = j;
                }
            }
            if (i == n || s.charAt(i) != '$') {
                continue;
            }
            /* variable */ {
                int j = i + 1;
                while (j != n && isVariable(s.charAt(j))) {
                    j++;
                }
                // In case j == i + 1, it's actually just a dollar sign, not a variable name
                if (j == i + 1) {
                    result.add(new Lexeme(LexemeType.BARE, s.substring(i, j)));
                } else {
                    result.add(new Lexeme(LexemeType.VARIABLE, s.substring(i + 1, j)));
                }
                i = j;
            }
        }
        return result;
    }

    /**
     * Expand string by spaces into words.
     * @param str string
     * @return list of lexemes: bare words and spaces.
     */
    /*package*/ List<Lexeme> expand(String str) {
        init(str);
        List<Lexeme> result = new ArrayList<>();
        for (i = 0; i != n; i++) {
            boolean isSpace = isSpace(s.charAt(i));
            result.add(nextLexeme(false, ch -> isSpace(ch) == isSpace, isSpace ? LexemeType.SPACE : LexemeType.BARE));
        }
        return result;
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\t';
    }

    private static boolean isWord(char c) {
        return "\'\"| \t".indexOf(c) == -1;
    }

    private static boolean isVariable(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || (c == '_');
    }

    private Lexeme nextLexeme(boolean isQuoted, Predicate<Character> continuePredicate, LexemeType type) {
        int start = i + (isQuoted ? 1 : 0);
        int end = start;
        while (end != n && continuePredicate.test(s.charAt(end))) {
            end++;
        }
        if (isQuoted && end == n) {
            //syntax error
            return null;
        }
        i = end + (isQuoted ? 0 : -1);
        return new Lexeme(type, s.substring(start, end));
    }

    private void init(String str) {
        s = str;
        n = s.length();
        i = 0;
    }
}
