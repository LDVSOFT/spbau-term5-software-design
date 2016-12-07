package net.ldvsoft.spbau.rogue.ui;

import net.ldvsoft.spbau.rogue.content.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main class
 */
public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            new Main().main();
        } catch (IOException e) {
            LOGGER.error("Fatal IO exception", e);
        }
    }

    private void main() throws IOException {
        Generator generator = new Generator(100, 100, System.currentTimeMillis());
        Controller controller = new Controller(generator);
        controller.work();
    }
}
