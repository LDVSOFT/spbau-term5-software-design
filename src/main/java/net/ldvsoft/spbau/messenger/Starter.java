package net.ldvsoft.spbau.messenger;

import net.ldvsoft.spbau.messenger.protocol.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ldvsoft on 30.10.16.
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

    public static Connection startServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();
        return new ServerSocketConnection(socket, serverSocket);
    }

    public static Connection startClient(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        return new SocketConnection(socket);
    }
}
