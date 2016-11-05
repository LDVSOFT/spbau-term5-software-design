package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Echo builtin command.
 * Just outputs all arguments.
 */
public class EchoCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        PrintStream out = new PrintStream(Command.getOutputStream(shell, outputFile));
        out.println(String.join(" ", args));
    }
}
