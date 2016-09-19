package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ldvsoft on 19.09.16.
 */
public class CatCommand implements Command {

    public static final int BUFFER_SIZE = 4096;

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
                byte[] buffer = new byte[BUFFER_SIZE];
                int cnt;
                while ((cnt = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, cnt);
                }
                if (!file.equals("-")) {
                    fileIn.close();
                }
            } catch (IOException e) {
                out.printf("IO error occurred on file \"%s\": %s\n", file, e.getMessage());
            }
        }
    }
}
