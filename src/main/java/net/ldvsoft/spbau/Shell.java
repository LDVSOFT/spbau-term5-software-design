package net.ldvsoft.spbau;

import net.ldvsoft.spbau.Lexeme.LexemeType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Shell class.
 * Runs a loop of commands reading and executing.
 */
public class Shell {
    private InputStream input;
    private OutputStream output;
    private boolean isWorking = true;
    private Lexer lexer = new Lexer();
    private Map<String, String> environment = new HashMap<>();

    /**
     * Shell main program method
     * @param args program arguments
     */
    public static void main(String... args) {
        Shell shell = new Shell(System.in, System.out);
        shell.work();
    }

    /**
     * Constructs a shell on given input and output.
     * @param input shell input stream.
     * @param output shell output stream.
     */
    public Shell(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Exits shell.
     * This method is for invoking from commands like `exit' to stop shell execution.
     * After invocation of this method, shell won't read the next command.
     */
    public void exit() {
        isWorking = false;
    }

    /**
     * Set environment variable in shell.
     * This method is for invoking from commands like assignments.
     * @param var variable name
     * @param value variable new value
     */
    void setVariable(String var, String value) {
        environment.put(var, value);
    }

    InputStream getInput() {
        return input;
    }

    OutputStream getOutput() {
        return output;
    }

    /**
     * Processes one single command
     * @param command command itself, as given from input
     */
    private void processCommand(String command) {
        try {
            List<Lexeme> lexemes = lexer.lexCommand(command);
            List<Lexeme> lexemesAfterSubstitution = substitute(lexemes);
            List<Lexeme> lexemesAfterExpand = expand(lexemesAfterSubstitution);
            List<Lexeme> lexemesAfterCollapse = collapse(lexemesAfterExpand);

            List<PipeElement> pipeElements = splitPipe(lexemesAfterCollapse);
            Path lastOutput = null;
            for (int i = 0; i != pipeElements.size(); i++) {
                Path currentOutput = null;
                if (i != pipeElements.size() - 1) {
                    currentOutput = Files.createTempFile("", "");
                }
                pipeElements.get(i).execute(lastOutput, currentOutput);
                if (i != 0) {
                    Files.delete(lastOutput);
                }
                lastOutput = currentOutput;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SyntaxError e) {
            new PrintStream(output).println(e.getMessage());
        }
    }

    /**
     * Do work loop: read command, execute it.
     */
    void work() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (isWorking) {
            try {
                String s = reader.readLine();
                if (s != null) {
                    processCommand(s);
                }
            } catch (IOException e) {
                new PrintStream(output).printf("IO error occurred:");
                e.printStackTrace();
                exit();
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
                                builder.append(environment.getOrDefault(l2.getLexeme(), ""));
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
     * Collapses touching words into one, like before `a"b"'c'' was three lexemes, and will become one `abc'.
     * @param lexemes list of lexemes
     * @return list of lexemes after collapsing
     */
    private List<Lexeme> collapse(List<Lexeme> lexemes) {
        List<Lexeme> result = new ArrayList<>();
        int n = lexemes.size();
        for (int i = 0; i < n; i++) {
            Lexeme l1 = lexemes.get(i);
            switch (l1.getLexemeType()) {
                case PIPE:
                case SPACE:
                    // Just leave as is
                    result.add(l1);
                    break;
                case BARE:
                case QUOTED:
                case DOUBLE_QUOTED:
                    // Collapse words together
                    StringBuilder builder = new StringBuilder();
                    int j;
                    for (j = i; j != n; j++) {
                        Lexeme l2 = lexemes.get(j);
                        LexemeType l2type = l2.getLexemeType();
                        if (l2type != LexemeType.BARE
                                && l2type != LexemeType.QUOTED
                                && l2type != LexemeType.DOUBLE_QUOTED) {
                            break;
                        }
                        builder.append(l2.getLexeme());
                    }
                    i = j - 1;
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
     * @throws SyntaxError in case the is empty pipe element (i.e., `echo 123 | | cat')
     */
    private List<PipeElement> splitPipe(List<Lexeme> lexemes) throws SyntaxError {
        ArrayList<PipeElement> result = new ArrayList<>();
        int i = 0;
        int n = lexemes.size();
        while (i < n) {
            if (lexemes.get(i).getLexemeType() == LexemeType.SPACE) {
                i++;
                continue;
            }
            int j = i;
            while (j != n && lexemes.get(j).getLexemeType() != LexemeType.PIPE) {
                j++;
            }
            if (j == i) {
                throw new SyntaxError("Empty pipe element!");
            }
            String command = lexemes.get(i).getLexeme();
            List<String> args = lexemes.subList(i + 1, j).stream()
                    .map(Lexeme::getLexeme)
                    .collect(Collectors.toList());
            if (command.contains(PipeElement.COMMAND_ASSIGNMENT)) {
                int pos = command.indexOf(PipeElement.COMMAND_ASSIGNMENT);
                String var = command.substring(0, pos);
                StringBuilder valueBuilder = new StringBuilder();
                valueBuilder.append(command.substring(Math.min(pos + 1, command.length())));
                args.forEach(valueBuilder::append);
                String value = valueBuilder.toString();
                result.add(new PipeElement(this, PipeElement.COMMAND_ASSIGNMENT, Arrays.asList(var, value)));
            } else {
                args = lexemes.subList(i + 1, j).stream()
                        .filter(lexeme -> lexeme.getLexemeType() != LexemeType.SPACE)
                        .map(Lexeme::getLexeme)
                        .collect(Collectors.toList());
                result.add(new PipeElement(this, command, args));
            }
            i = j + 1;
        }
        return result;
    }
}
