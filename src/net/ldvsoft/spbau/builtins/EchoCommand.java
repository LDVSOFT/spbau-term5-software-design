package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ldvsoft on 19.09.16.
 */
public class EchoCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        PrintStream out = new PrintStream(Command.getOutputStream(shell, outputFile));
        out.print(String.join(" ", args));
    }
}
