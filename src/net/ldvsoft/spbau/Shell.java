package net.ldvsoft.spbau;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldvsoft on 08.09.16.
 */
public class Shell {
    private InputStream input;
    private OutputStream output;
    private Lexer lexer = new Lexer();

    public static void main(String[] args) {
        Shell shell = new Shell(System.in, System.out);
        shell.work();
    }

    /*package*/ Shell(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /*package*/ boolean processCommand(String command) {
        List<Lexeme> lexemes = lexer.lexCommand(command);
        List<Lexeme> lexemesAfterSubstitution = subtitute(lexemes);
        return !lexemesAfterSubstitution.isEmpty();
    }

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

    private List<Lexeme> subtitute(List<Lexeme> lexems) {
        List<Lexeme> result = new ArrayList<>();
        for (Lexeme l1: lexems) {
            switch (l1.getLexemeType()) {
                case SPACE:
                case DOUBLE_QUOTED:
                case PIPE:
                    result.add(l1);
                    break;
                case QUOTED:
                case BARE:
                    StringBuilder builder = new StringBuilder();
                    for (Lexeme l2: lexems) {
                        switch (l2.getLexemeType()) {
                            case BARE:
                                builder.append(l2.getLexeme());
                                break;
                            case VARIABLE:
                                builder.append("[ VALUE OF " + l2.getLexeme() + " ]"); // FIXME
                                break;
                        }
                    }
                    result.add(new Lexeme(l1.getLexemeType(), builder.toString()));
                    break;
            }
        }
        return result;
    }
}
