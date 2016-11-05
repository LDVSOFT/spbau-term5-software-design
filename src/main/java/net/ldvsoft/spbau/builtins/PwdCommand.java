package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Pwd builtin command.
 * Outputs current working directory.
 */
public class PwdCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        PrintStream printStream = new PrintStream(Command.getOutputStream(shell, outputFile));
        printStream.println(Paths.get("").toAbsolutePath().normalize().toString());
    }
}
