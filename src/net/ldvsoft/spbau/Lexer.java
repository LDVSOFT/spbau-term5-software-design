package net.ldvsoft.spbau;

import net.ldvsoft.spbau.Lexeme.LexemeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by ldvsoft on 08.09.16.
 */
public class Lexer {
    private String s;
    private int n;
    private int i;

    public List<Lexeme> lexCommand(String command) {
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
                    // FIXME syntax error
                }
                result.add(lexeme);
                continue;
            }
            /* bare word */ {
                result.add(nextLexeme(false, Lexer::isVariable, LexemeType.BARE));
            }
        }
        return result;
    }

    public List<Lexeme> lexStringForSubtitutions(String str) {
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
                if (j == i + 1) {
                    // FIXME syntax error
                }
                result.add(new Lexeme(LexemeType.VARIABLE, s.substring(i + 1, j)));
                i = j;
            }
        }
        return result;
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\t';
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
