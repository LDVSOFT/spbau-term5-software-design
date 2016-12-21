package net.ldvsoft.spbau;

import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests for grep
 */
public class GrepTest {
    @Test
    public void testSimple() {
        ShellTestUtils.doTest("echo 1 | grep 1\nexit", "1\n");
    }

    @Test
    public void testFileAndAppend() throws IOException {
        Path tmp = Files.createTempFile("test", ".tmp");
        new PrintStream(Files.newOutputStream(tmp)).print("1\n2\n3\n4\n5\n6\n");
        ShellTestUtils.doTest("grep -A 2 '3' " + tmp + "\nexit", "3\n4\n5\n--\n");
    }

    @Test
    public void testIgnoreCase() throws IOException {
        ShellTestUtils.doTest("echo 1\n" +
                "echo 'HeLlO WoRlD' | grep -i 'hello world'\n" +
                "echo 2\n" +
                "echo 'HeLlO WoRlD' | grep 'hello world'\n" +
                "exit", "1\nHeLlO WoRlD\n2\n");
    }
}
