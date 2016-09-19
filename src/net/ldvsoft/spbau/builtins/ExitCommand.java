package net.ldvsoft.spbau.builtins;

import net.ldvsoft.spbau.Command;
import net.ldvsoft.spbau.Shell;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ldvsoft on 19.09.16.
 */
public class ExitCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException {
        shell.exit();
    }
}
