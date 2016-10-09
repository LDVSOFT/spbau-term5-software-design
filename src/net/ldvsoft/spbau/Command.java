package net.ldvsoft.spbau;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Command interface.
 * Provides `execute' method that allows to execute shell commands.
 */
public interface Command {
    /**
     * Helper method to open output stream.
     * @param shell Shell, from which the stream will be received in case working not with pipe
     * @param outputFile Output file name, or null to use shell output
     */
    static OutputStream getOutputStream(Shell shell, Path outputFile) throws IOException {
        return (outputFile == null) ? shell.getOutput() : Files.newOutputStream(outputFile);
    }

    /**
     * Helper method to open input stream.
     * @param shell Shell, from which the stream will be received in case working not with pipe
     * @param inputFile Input file name, or null to read shell input
     */
    static InputStream getInputStream(Shell shell, Path inputFile) throws IOException {
        return (inputFile == null) ? shell.getInput() : Files.newInputStream(inputFile);
    }

    /**
     * Execute given command
     * @param shell shell object that is invoking the command
     * @param inputFile path to a file that stores command input, or null to read shell input
     * @param outputFile path to a file to save command output, or null to use shell output
     * @param command command name to be executed
     * @param args list of command arguments
     * @throws IOException in case IO error occurs while launching the command
     */
    void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException;
}
