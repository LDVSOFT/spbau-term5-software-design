package net.ldvsoft.spbau;

import net.ldvsoft.spbau.builtins.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pipe element.
 * Holds command name and it's arguments, and starts it's invocation.
 * Invocation is performed in two ways:
 * -> If there is a builtin for given command, it is invoked
 * -> Else, the generic Process runner is invoked.
 */
class PipeElement {
    static final String COMMAND_ASSIGNMENT = "=";
    private static final Map<String, Command> BUILTINS = new HashMap<>();
    private static final Command PROCESS_COMMAND = new ProcessCommand();

    static {
        BUILTINS.put(COMMAND_ASSIGNMENT, new AssignmentCommand());
        BUILTINS.put("pwd", new PwdCommand());
        BUILTINS.put("exit", new ExitCommand());
        BUILTINS.put("wc", new WcCommand());
        BUILTINS.put("cat", new CatCommand());
        BUILTINS.put("echo", new EchoCommand());
    }

    private Shell shell;
    private String command;
    private List<String> args;

    PipeElement(Shell shell, String command, List<String> args) {
        this.shell = shell;
        this.command = command;
        this.args = args;
        if (command.equals(COMMAND_ASSIGNMENT)) {
            assert args.size() == 2;
        }
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    void execute(Path inputFile, Path outputFile) throws IOException {
        Command commandRunner = BUILTINS.getOrDefault(command, PROCESS_COMMAND);
        commandRunner.execute(shell, inputFile, outputFile, command, args);
    }
}
