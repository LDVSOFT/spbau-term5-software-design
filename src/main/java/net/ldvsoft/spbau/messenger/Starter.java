package net.ldvsoft.spbau.messenger;

import net.ldvsoft.spbau.messenger.protocol.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Messenger starter.
 * Opens Connection instance on given settings.
 */
public class Starter {
    private static class SocketConnection implements Connection {
        private Socket socket;

        private SocketConnection(Socket socket) {
            this.socket = socket;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }

    private static class ServerSocketConnection extends SocketConnection {
        private ServerSocket serverSocket;

        private ServerSocketConnection(Socket socket, ServerSocket serverSocket) {
            super(socket);
            this.serverSocket = serverSocket;
        }

        @Override
        public void close() throws IOException {
            super.close();
            serverSocket.close();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

    private Starter() {
    }

    public static Connection startServer(int port) throws IOException {
        LOGGER.info("Starting server at port {}.", port);
        ServerSocket serverSocket = new ServerSocket(port);
        LOGGER.info("Started server, waiting for connection.");
        Socket socket = serverSocket.accept();
        LOGGER.info("Client connected.");
        return new ServerSocketConnection(socket, serverSocket);
    }

    public static Connection startClient(String host, int port) throws IOException {
        LOGGER.info("Connecting to server at {}:{}", host, port);
        Socket socket = new Socket(host, port);
        LOGGER.info("Connected to server.");
        return new SocketConnection(socket);
    }
}
