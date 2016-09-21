package net.ldvsoft.spbau;

import java.nio.file.Path;
import java.util.List;

/**
 * Assignment command.
 * Takes the form `var=value', this form is specially parsed by the shell.
 */
/*package*/ class AssignmentCommand implements Command {
    @Override
    public void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) {
        shell.setVariable(args.get(0), args.get(1));
    }
}
