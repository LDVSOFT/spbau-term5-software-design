package net.ldvsoft.spbau;

import org.junit.Test;

/**
 * Tests for grep
 */
public class GrepTest {
    @Test
    public void testSimple() {
        ShellTestUtils.doTest("echo 1 | grep 1\nexit", "1\n");
    }
}
