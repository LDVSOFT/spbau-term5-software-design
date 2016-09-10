package net.ldvsoft.spbau;

import net.ldvsoft.spbau.Lexeme.LexemeType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ldvsoft on 08.09.16.
 */
public class Shell {
    private InputStream input;
    private OutputStream output;
    private Lexer lexer = new Lexer();
    private Map<String, String> environment = new HashMap<>();

    public static void main(String[] args) {
        Shell shell = new Shell(System.in, System.out);
        shell.work();
    }

    /*package*/ Shell(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Processes one single command
     * @param command command itself, as given from input
     * @return true, if next command if waited for
     */
    /*package*/ boolean processCommand(String command) {
        List<Lexeme> lexemes = lexer.lexCommand(command);
        List<Lexeme> lexemesAfterSubstitution = substitute(lexemes);
        List<Lexeme> lexemesAfterExpand = expand(lexemesAfterSubstitution);
        List<Lexeme> lexemesAfterCollapse = collapse(lexemesAfterExpand);

        List<PipeElement> pipeElements = splitPipe(lexemesAfterCollapse);
        return !pipeElements.isEmpty();
    }

    /**
     * Do work loop: read command, execute it.
     */
    private void work() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        boolean continueWork = true;
        while (continueWork) {
            continueWork = false;
            try {
                String s = reader.readLine();
                if (s != null) {
                    continueWork = processCommand(s);
                }
            } catch (IOException e) {
                // FIXME Очень жаль
                e.printStackTrace();
            }
        }
    }

    /**
     * Substitute variables values in bare and double-quoted words.
     * @param lexemes list of lexemes
     * @return list of lexemes after substitution
     */
    private List<Lexeme> substitute(List<Lexeme> lexemes) {
        List<Lexeme> result = new ArrayList<>();
        for (Lexeme l1: lexemes) {
            switch (l1.getLexemeType()) {
                case SPACE:
                case PIPE:
                case QUOTED:
                    result.add(l1);
                    break;
                case DOUBLE_QUOTED:
                case BARE:
                    StringBuilder builder = new StringBuilder();
                    for (Lexeme l2: lexer.lexStringForSubstitutions(l1.getLexeme())) {
                        switch (l2.getLexemeType()) {
                            case BARE:
                                builder.append(l2.getLexeme());
                                break;
                            case VARIABLE:
                                builder.append(environment.getOrDefault(l2.getLexeme(), "")); // FIXME
                                break;
                        }
                    }
                    result.add(new Lexeme(l1.getLexemeType(), builder.toString()));
                    break;
            }
        }
        return result;
    }

    /**
     * Expand bare words which after substitution could receive spaces in them.
     * @param lexemes list of lexemes after substitution
     * @return list of lexemes after expanding
     */
    private List<Lexeme> expand(List<Lexeme> lexemes) {
        List<Lexeme> result = new ArrayList<>();
        for (Lexeme l1: lexemes) {
            if (l1.getLexemeType() != LexemeType.BARE) {
                result.add(l1);
                continue;
            }
            result.addAll(lexer.expand(l1.getLexeme()));
        }
        return result;
    }

    /**
     * Strims spaces lexemes and collapses touching words into one.
     * @param lexemes list of lexemes
     * @return list of lexemes after collapsing
     */
    private List<Lexeme> collapse(List<Lexeme> lexemes) {
        List<Lexeme> result = new ArrayList<>();
        int i = 0;
        int n = lexemes.size();
        while (i != n) {
            Lexeme l1 = lexemes.get(i);
            switch (l1.getLexemeType()) {
                case SPACE:
                    // Trim spaces
                    break;
                case PIPE:
                    // Just leave as is
                    result.add(l1);
                    break;
                case BARE:
                case QUOTED:
                case DOUBLE_QUOTED:
                    // Collapse words together
                    StringBuilder builder = new StringBuilder();
                    for (int j = i; j != n; j++) {
                        Lexeme l2 = lexemes.get(j);
                        LexemeType l2type = l2.getLexemeType();
                        if (l2type != LexemeType.BARE
                                && l2type != LexemeType.QUOTED
                                && l2type != LexemeType.DOUBLE_QUOTED) {
                            break;
                        }
                        builder.append(l2.getLexeme());
                    }
                    result.add(new Lexeme(LexemeType.BARE, builder.toString()));
                    break;
            }
        }
        return result;
    }

    /**
     * Split lexemes into pipe elements
     * @param lexemes list of lexemes
     * @return list of pipe elements
     */
    private List<PipeElement> splitPipe(List<Lexeme> lexemes) {
        ArrayList<PipeElement> result = new ArrayList<>();
        int i = 0;
        int n = lexemes.size();
        while (i < n) {
            int j = i;
            while (j != n && lexemes.get(j).getLexemeType() != LexemeType.PIPE) {
                j++;
            }
            if (j == i) {
                // FIXME Syntax error: empty pipe element
            }
            List<String> args = lexemes.subList(i + 1, j).stream()
                    .map(Lexeme::getLexeme)
                    .collect(Collectors.toList());
            result.add(new CommandInvocation(lexemes.get(i).getLexeme(), args)); // FIXME Assignments!
            i = j + 1;
        }
        return result;
    }
}
