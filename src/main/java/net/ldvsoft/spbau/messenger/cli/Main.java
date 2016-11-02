package net.ldvsoft.spbau.messenger.cli;

import net.ldvsoft.spbau.messenger.Messenger;
import net.ldvsoft.spbau.messenger.Starter;
import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.PeerInfo;
import net.ldvsoft.spbau.messenger.protocol.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple command-line client
 */
public final class Main {
    private static final Format FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean working;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private Messenger messenger;
    private Logger logger = LoggerFactory.getLogger(Main.class);
    private Messenger.Listener listener = new Messenger.Listener() {
        @Override
        public void onMessage(TextMessage s) {
            logger.info("Received message \"{}\" at {} from {}.",
                    s.getText(),
                    s.getDate(),
                    messenger.getPeer().getName()
            );
            System.out.printf(
                    "<<< (%s at %s) %s\n",
                    messenger.getPeer().getName(),
                    FORMATTER.format(s.getDate()),
                    s.getText()
            );
        }

        @Override
        public void onPeerInfo(PeerInfo s) {
            logger.info("Received new peer name {}.",
                    s.getName(),
                    messenger.getPeer().getName()
            );
            System.out.printf(
                    "<<< (changed name to %s)\n",
                    s.getName()
            );
        }

        @Override
        public void onBye() {
            logger.info("Received bye.");
            System.out.printf("%s left. Press Enter to leave.", messenger.getPeer().getName());
            working = false;
        }

        @Override
        public void onError(Exception e) {
            logger.error("Received exception from messenger.", e);
            System.out.printf("Something is wrong: %s\n", e.getMessage());
        }
    };

    public static void main(String[] args) {
        new Main().work();
    }

    private String prompt(String s) throws IOException {
        System.out.printf("%s> ", s);
        String input = reader.readLine();
        logger.debug("User entered \"{}\" for prompt \"{}\".", input, s);
        return input;
    }

    private void work() {
        try {
            boolean working = true;
            while (working) {
                String mode = prompt("Enter mode: s (server), c (client), q (exit)");
                String host;
                String name;
                int port;
                switch (mode) {
                    case "s":
                        port = Integer.parseInt(prompt("Port"));
                        name = prompt("Enter your name");
                        System.out.printf("Waiting for connection...\n");
                        messaging(name, Starter.startServer(port));
                        break;
                    case "c":
                        host = prompt("Server host");
                        port = Integer.parseInt(prompt("Server port"));
                        name = prompt("Enter your name");
                        System.out.printf("Connecting...\n");
                        messaging(name, Starter.startClient(host, port));
                        break;
                    case "q":
                        working = false;
                        break;
                }
            }
        } catch (IOException e) {
            logger.error("Exception at main.", e);
            System.out.printf("IO error happened: %s\n", e.getMessage());
        }
    }

    private void messaging(String name, Connection connection) throws IOException {
        messenger = new Messenger(name, connection, listener);
        logger.info("Messaging started.");
        System.out.printf("Connected!\nEnter text to type message, name to change name, or quit.\n");
        working = true;
        while (working) {
            String action = reader.readLine();
            logger.debug("User entered action \"{}\".", action);
            if (!working) {
                break;
            }
            switch (action) {
                case "text":
                    String message = prompt("Enter message");
                    logger.info("Sending message \"{}\".", message);
                    TextMessage textMessage = messenger.sendMessage(message);
                    System.out.printf(
                            ">>> (%s at %s) %s\n",
                            messenger.getSelf().getName(),
                            FORMATTER.format(textMessage.getDate()),
                            textMessage.getText()
                    );
                    break;
                case "name":
                    String newName = prompt("Enter new name");
                    logger.info("Setting new name \"{}\".", newName);
                    messenger.setName(newName);
                    System.out.printf(
                            ">>> (changed name to %s)\n",
                            newName
                    );
                    break;
                case "quit":
                    logger.info("Leaving chat.");
                    messenger.stop();
                    working = false;
                    break;
                default:
                    System.out.printf("Unknown command %s.\nAvailable are: text, name, quit.\n", action);
                    break;
            }
        }
        messenger.stop();
    }
}
