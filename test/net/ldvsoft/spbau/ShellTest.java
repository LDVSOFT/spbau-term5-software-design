package net.ldvsoft.spbau;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Shell tests.
 * Here, some Shell unit-tests are present: shell receives full input, we check only output.
 */
public class ShellTest {
    private static void doTest(String input, String expectedOutput) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Shell shell = new Shell(new ByteArrayInputStream(input.getBytes()), outputStream);
        shell.work();
        String actualOutput = outputStream.toString();
        assertEquals("Shell output is wrong!", expectedOutput, actualOutput);
    }

    @Test
    public void testSimple() {
        doTest("echo '123'\nexit\n", "123\n");
    }

    @Test
    public void testQuoting() {
        doTest("echo 0' 1  2'  \"  3\"\"    \"4         '    5'\nexit", "0 1  2   3    4     5\n");
    }

    @Test
    public void testEnvironment() {
        String input = "" +
                "X=3\nY='two spaces here:  .'\n" +
                "echo '$X=$X' $X=$X $$\n" +
                "echo '$Y='\"$Y\"\n" +
                "echo '$Y='$Y\n" +
                "exit";
        String output = "" +
                "$X=$X 3=3 $$\n" +
                "$Y=two spaces here:  .\n" +
                "$Y=two spaces here: .\n";
        doTest(input, output);
    }

    @Test
    public void testPipe() {
        doTest("echo 12345 | cat|cat|cat | cat | cat\nexit\n", "12345\n");
    }

    @Test
    public void testProcess() {
        // As example, we will invoke GNU coreutil `tr'.
        doTest("echo hello, world! | tr a-z A-Z | cat\nexit\n", "HELLO, WORLD!\n");
    }
}