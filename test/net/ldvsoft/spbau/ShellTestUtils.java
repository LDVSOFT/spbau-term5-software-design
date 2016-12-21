package net.ldvsoft.spbau;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Base test utils
 */
class ShellTestUtils {
    /**
     * Run shell with given input and test it's output
     * @param input input to give to the shell
     * @param expectedOutput expected output of the shell
     */
    static void doTest(String input, String expectedOutput) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Shell shell = new Shell(new ByteArrayInputStream(input.getBytes()), outputStream);
        shell.work();
        String actualOutput = outputStream.toString();
        assertEquals("Shell output is wrong!", expectedOutput, actualOutput);
    }
}
