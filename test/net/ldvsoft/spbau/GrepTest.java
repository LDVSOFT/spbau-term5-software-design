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
        new PrintStream(Files.newOutputStream(tmp)).print("1\n2\n3\n4\n5\n6\n98\n99\n100");
        ShellTestUtils.doTest("grep -A 2 '3|99' " + tmp + "\nexit", "3\n4\n5\n--\n99\n100\n--\n");
    }

    @Test
    public void testIgnoreCase() throws IOException {
        ShellTestUtils.doTest("echo 1\n" +
                "echo 'HeLlO WoRlD' | grep -i 'hello world'\n" +
                "echo 2\n" +
                "echo 'HeLlO WoRlD' | grep 'hello world'\n" +
                "exit", "1\nHeLlO WoRlD\n2\n");
    }

    @Test
    public void testRegex1() throws IOException {
        Path tmp = Files.createTempFile("test", ".tmp");
        String lines = "" +
                "+79 01 000 0 0 00\n" +
                "+70919999999\n" +
                "+7 (156) 653-42-65\n" +
                "+7093 546 3566\n" +
                "+7-567-56-66-335";
        String result = "" +
                "+70919999999\n" +
                "+7 (156) 653-42-65\n" +
                "+7093 546 3566\n";
        new PrintStream(Files.newOutputStream(tmp)).print(lines);
        ShellTestUtils.doTest("grep '\\+7 ?(?:[0-9]{3}|\\([0-9]{3}\\)) ?[0-9]{3}[ -]?[0-9]{2}[ -]?[0-9]{2}' "
                + tmp + "\nexit", result);
    }
}
