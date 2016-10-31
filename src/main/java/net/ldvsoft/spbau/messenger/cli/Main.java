package net.ldvsoft.spbau.messenger.cli;

import net.ldvsoft.spbau.messenger.Messenger;
import net.ldvsoft.spbau.messenger.Starter;
import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.PeerInfo;
import net.ldvsoft.spbau.messenger.protocol.TextMessage;

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
    private Messenger.Listener listener = new Messenger.Listener() {
        @Override
        public void onMessage(TextMessage s) {
            System.out.printf(
                    "<<< (%s at %s) %s\n",
                    messenger.getPeer().getName(),
                    FORMATTER.format(s.getDate()),
                    s.getText()
            );
        }

        @Override
        public void onPeerInfo(PeerInfo s) {
            System.out.printf(
                    "<<< (changed name to %s)\n",
                    s.getName()
            );
        }

        @Override
        public void onBye() {
            System.out.printf("%s left. Press Enter to leave.", messenger.getPeer().getName());
            working = false;
        }

        @Override
        public void onError(Exception e) {
            System.out.printf("Something is wrong: %s\n", e.getMessage());
        }
    };

    public static void main(String[] args) {
        try {
            new Main().work();
        } catch (IOException e) {
            System.out.printf("IO error happened: %s\n", e.getMessage());
        }
    }

    private String prompt(String s) throws IOException {
        System.out.printf("%s> ", s);
        return reader.readLine();
    }

    private void work() throws IOException {
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
    }

    private void messaging(String name, Connection connection1) throws IOException {
        try (Connection connection = connection1) {
            messenger = new Messenger(name, connection, listener);
            System.out.printf("Connected!\nEnter text to type message, name to change name, or quit.\n");
            working = true;
            while (working) {
                String action = reader.readLine();
                if (!working) {
                    break;
                }
                switch (action) {
                    case "text":
                        String message = prompt("Enter message");
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
                        messenger.setName(newName);
                        System.out.printf(
                                ">>> (changed name to %s)\n",
                                newName
                        );
                        break;
                    case "quit":
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
}
