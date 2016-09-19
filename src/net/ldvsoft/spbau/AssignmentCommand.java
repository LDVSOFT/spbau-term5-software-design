package net.ldvsoft.spbau;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by ldvsoft on 13.09.16.
 */
public class AssignmentCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) {
        shell.setVariable(args.get(0), args.get(1));
    }
}
