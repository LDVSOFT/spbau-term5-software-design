package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Wc builtin command.
 * Counts characters, words and lines in given files.
 * If no files are given, input is being read.
 */
public class WcCommand implements Command {
    private static final String STDIN_FILE_NAME = "-";

    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        if (args.size() == 0) {
            args.add(STDIN_FILE_NAME);
        }
        InputStream in = Command.getInputStream(shell, inputFile);
        PrintStream out = new PrintStream(Command.getOutputStream(shell, outputFile));
        for (String file: args) {
            try {
                InputStream fileIn;
                if (file.equals(STDIN_FILE_NAME)) {
                    fileIn = in;
                } else {
                    fileIn = new FileInputStream(file);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));
                String s;
                int lineCount = 0;
                int wordCount = 0;
                int charCount = 0;
                // It does not count linefeeds, though...
                while ((s = reader.readLine()) != null) {
                    lineCount++;
                    charCount += s.length();
                    s = s.trim();
                    if (!s.isEmpty()) {
                        wordCount += s.split("\\s+").length;
                    }
                }
                out.printf("%8d %8d %8d %s\n", lineCount, wordCount, charCount, file);
                if (!file.equals(STDIN_FILE_NAME)) {
                    fileIn.close();
                }
            } catch (IOException e) {
                out.printf("IO error occurred on file \"%s\": %s\n", file, e.getMessage());
            }
        }
    }
}
