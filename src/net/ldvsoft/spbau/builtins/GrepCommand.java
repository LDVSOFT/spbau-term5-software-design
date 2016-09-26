package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Grep builtin command.
 * Usage: grep [options] [filename]...
 * If no files are given, grep reads input
 * Options:
 * 1. -i: ignore case
 * 2. -w: match only whole words only
 * 3. -A [n]: add n lines of context after the match to output
 */
public class GrepCommand implements Command {
    private static final Options OPTIONS = new Options();
    private static final String STDIN_FILE_NAME = "-";

    static {
        OPTIONS.addOption("i", "ignore case");
        OPTIONS.addOption("w", "whole words only");
        OPTIONS.addOption(
                Option.builder("A")
                        .hasArg()
                        .argName("n")
                        .desc("append n lines after match")
                        .build()
        );
    }

    private static void tryMatch(List<String> buffer, Pattern pattern, int appendLines, PrintStream out) {
        if (pattern.matcher(buffer.get(0)).find()) {
            for (int j = 0; j < Math.min(appendLines + 1, buffer.size()); ++j) {
                out.printf("%s\n", buffer.get(j));
            }
            if (appendLines > 0) {
                out.printf("--\n");
            }
        }
    }

    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        InputStream in = Command.getInputStream(shell, inputFile);
        PrintStream out = new PrintStream(Command.getOutputStream(shell, outputFile));
        String[] argsArray = args.toArray(new String[0]);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine parsed = parser.parse(OPTIONS, argsArray, true);
            boolean ignoreCase = parsed.hasOption('i');
            boolean wholeWords = parsed.hasOption('w');
            int appendLines = Integer.parseUnsignedInt(parsed.getOptionValue('A', "0"));
            List<String> leftArgs = parsed.getArgList();
            if (leftArgs.size() == 0) {
                out.printf("grep: No pattern was given!\n");
                return;
            }
            int flags = 0;
            String patternString = leftArgs.get(0);
            List<String> fileArgs = leftArgs.subList(1, leftArgs.size());
            if (ignoreCase) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (wholeWords) {
                patternString = String.format("\\b%s\\b", patternString);
            }
            Pattern pattern = Pattern.compile(patternString, flags);
            if (fileArgs.isEmpty()) {
                fileArgs.add(STDIN_FILE_NAME);
            }
            for (String file: fileArgs) {
                try {
                    InputStream fileIn;
                    if (file.equals(STDIN_FILE_NAME)) {
                        fileIn = in;
                    } else {
                        fileIn = new FileInputStream(file);
                    }
                    ArrayList<String> buffer = new ArrayList<>();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));
                    String s;
                    while ((s = reader.readLine()) != null) {
                        if (buffer.size() < appendLines + 1) {
                            buffer.add(s);
                        } else {
                            Collections.rotate(buffer, -1);
                            buffer.set(appendLines, s);
                            tryMatch(buffer, pattern, appendLines, out);
                        }
                    }
                    if (buffer.size() == appendLines + 1) {
                        buffer.remove(0);
                    }
                    while (!buffer.isEmpty()) {
                        tryMatch(buffer, pattern, appendLines, out);
                        buffer.remove(0);
                    }
                    if (!file.equals(STDIN_FILE_NAME)) {
                        fileIn.close();
                    }
                } catch (IOException e) {
                    out.printf("grep: IO error occurred on file \"%s\": %s\n", file, e.getMessage());
                }
            }
        } catch (ParseException | NumberFormatException e) {
            out.printf("grep: Command line error: %s\n", e.getMessage());
        }
    }
}
