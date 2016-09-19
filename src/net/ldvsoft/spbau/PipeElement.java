package net.ldvsoft.spbau;

import net.ldvsoft.spbau.builtins.PwdCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by ldvsoft on 10.09.16.
 */
/*package*/ class PipeElement {
    public static final String COMMAND_ASSIGNMENT = "=";
    private static final Map<String, Command> BUILTINS = new HashMap<>();
    private static final Command PROCESS_COMMAND = new ProcessCommand();

    static {
        BUILTINS.put(COMMAND_ASSIGNMENT, new AssignmentCommand());
        BUILTINS.put("pwd", new PwdCommand());
    }

    private Shell shell;
    private String command;
    private List<String> args;
    private Future<Boolean> result;

    /*package*/ PipeElement(Shell shell, String command, List<String> args) {
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

    public void execute(Path inputFile, Path outputFile) throws IOException {
        Command commandRunner = BUILTINS.getOrDefault(command, PROCESS_COMMAND);
        commandRunner.execute(shell, inputFile, outputFile, command, args);
    }

    public boolean join() {
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        result.cancel(true);
    }
}
