package net.ldvsoft.spbau;

import net.ldvsoft.spbau.Lexeme.LexemeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Shell command lexer.
 * Splits strings to lexemes in different ways that are required by steps of command parsing.
 */
class Lexer {
    private String parsingString;
    private int stringLen;
    private int currentPos;

    /**
     * Splits command into it's generic parts -- pipe elements and words (bare, quoted, ...)
     * @param command command
     * @return list of lexemes
     * @throws SyntaxError in case quotes cannot be paired
     */
    List<Lexeme> lexCommand(String command) throws SyntaxError {
        init(command);
        List<Lexeme> result = new ArrayList<>();
        for (currentPos = 0; currentPos != stringLen; currentPos++) {
            char c = parsingString.charAt(currentPos);
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
                    throw new SyntaxError(String.format("Unclosed bracket %c at position %d", c, currentPos + 1));
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
    List<Lexeme> lexStringForSubstitutions(String str) {
        init(str);
        List<Lexeme> result = new ArrayList<>();
        while (currentPos != stringLen) {
            /* non-variable */ {
                int j = currentPos;
                while (j != stringLen && parsingString.charAt(j) != '$') {
                    j++;
                }
                if (currentPos != j) {
                    result.add(new Lexeme(LexemeType.BARE, parsingString.substring(currentPos, j)));
                    currentPos = j;
                }
            }
            if (currentPos == stringLen || parsingString.charAt(currentPos) != '$') {
                continue;
            }
            /* variable */ {
                int j = currentPos + 1;
                while (j != stringLen && isVariable(parsingString.charAt(j))) {
                    j++;
                }
                // In case j == currentPos + 1, it's actually just a dollar sign, not a variable name
                if (j == currentPos + 1) {
                    result.add(new Lexeme(LexemeType.BARE, parsingString.substring(currentPos, j)));
                } else {
                    result.add(new Lexeme(LexemeType.VARIABLE, parsingString.substring(currentPos + 1, j)));
                }
                currentPos = j;
            }
        }
        return result;
    }

    /**
     * Expand string by spaces into words.
     * @param str string
     * @return list of lexemes: bare words and spaces.
     */
    List<Lexeme> expand(String str) {
        init(str);
        List<Lexeme> result = new ArrayList<>();
        for (currentPos = 0; currentPos != stringLen; currentPos++) {
            boolean isSpace = isSpace(parsingString.charAt(currentPos));
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
        int start = currentPos + (isQuoted ? 1 : 0);
        int end = start;
        while (end != stringLen && continuePredicate.test(parsingString.charAt(end))) {
            end++;
        }
        if (isQuoted && end == stringLen) {
            //syntax error
            return null;
        }
        currentPos = end + (isQuoted ? 0 : -1);
        return new Lexeme(type, parsingString.substring(start, end));
    }

    private void init(String str) {
        parsingString = str;
        stringLen = parsingString.length();
        currentPos = 0;
    }
}
