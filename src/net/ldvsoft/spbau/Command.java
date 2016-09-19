package net.ldvsoft.spbau;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ldvsoft on 13.09.16.
 */
public interface Command {
    static OutputStream getOutputStream(Shell shell, Path outputFile) throws IOException {
        return (outputFile == null) ? shell.getOutput() : Files.newOutputStream(outputFile);
    }

    static InputStream getInputStream(Shell shell, Path inputFile) throws IOException {
        return (inputFile == null) ? shell.getInput() : Files.newInputStream(inputFile);
    }

    void execute(Shell shell, Path inputFile, Path outputFile, String command, List<String> args) throws IOException;
}
