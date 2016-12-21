package net.ldvsoft.spbau;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Shell tests.
 * Here, some Shell unit-tests are present: shell receives full input, we check only output.
 */
public class ShellTest {
    @Test
    public void testSimple() {
        ShellTestUtils.doTest("echo '123'\nexit\n", "123\n");
    }

    @Test
    public void testQuoting() {
        ShellTestUtils.doTest("echo 0' 1  2'  \"  3\"\"    \"4         '    5'\nexit", "0 1  2   3    4     5\n");
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
        ShellTestUtils.doTest(input, output);
    }

    @Test
    public void testPipe() {
        ShellTestUtils.doTest("echo 12345 | cat|cat|cat | cat | cat\nexit\n", "12345\n");
    }

    @Test
    public void testProcess() {
        // As example, we will invoke GNU coreutil `tr'.
        ShellTestUtils.doTest("echo hello, world! | tr a-z A-Z | cat\nexit\n", "HELLO, WORLD!\n");
    }
}