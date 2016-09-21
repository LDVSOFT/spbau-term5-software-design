package net.ldvsoft.spbau;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic command. Executes given command with new process.
 */
public class ProcessCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        try {
            List<String> fullCommand = new ArrayList<>();
            fullCommand.add(command);
            fullCommand.addAll(args);
            new ProcessBuilder()
                    .command(fullCommand)
                    .redirectInput((inputFile == null) ? Redirect.INHERIT : Redirect.from(inputFile.toFile()))
                    .redirectOutput((outputFile == null) ? Redirect.INHERIT : Redirect.to(outputFile.toFile()))
                    .start()
                    .waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
