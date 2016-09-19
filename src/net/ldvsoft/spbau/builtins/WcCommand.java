package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Created by ldvsoft on 19.09.16.
 */
public class WcCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        if (args.size() == 0) {
            args.add("-");
        }
        InputStream in = Command.getInputStream(shell, inputFile);
        PrintStream out = new PrintStream(Command.getOutputStream(shell, outputFile));
        for (String file: args) {
            try {
                InputStream fileIn;
                if (file.equals("-")) {
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
                if (!file.equals("-")) {
                    fileIn.close();
                }
            } catch (IOException e) {
                out.printf("IO error occurred on file \"%s\": %s\n", file, e.getMessage());
            }
        }
    }
}
