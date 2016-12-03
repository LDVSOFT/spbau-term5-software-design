package net.ldvsoft.spbau.messenger;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import net.ldvsoft.spbau.messenger.protocol.Connection;
import net.ldvsoft.spbau.messenger.protocol.MessengerGrpc;
import net.ldvsoft.spbau.messenger.protocol.P2PMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Messenger starter.
 * Opens Connection instance on given settings.
 */
public class Starter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

    private static class ServerConnection implements Connection {
        private Server server;
        private int port;

        private ServerConnection(int port) {
            this.port = port;
        }

        @Override
        public StreamObserver<P2PMessenger.Message> startChat(StreamObserver<P2PMessenger.Message> reader) throws IOException {
            CompletableFuture<StreamObserver<P2PMessenger.Message>> writerFuture = new CompletableFuture<>();
            server = ServerBuilder.forPort(port)
                    .addService(new MessengerGrpc.MessengerImplBase() {
                        @Override
                        public StreamObserver<P2PMessenger.Message> chat(StreamObserver<P2PMessenger.Message> responseObserver) {
                            writerFuture.complete(responseObserver);
                            return reader;
                        }
                    })
                    .build();
            LOGGER.info("Starting server at port {}.", port);
            server.start();
            LOGGER.info("Started server, waiting for connection.");
            StreamObserver<P2PMessenger.Message> writer;
            try {
                writer = writerFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                // Should never happen
                throw new RuntimeException(e);
            }
            LOGGER.info("Client connected.");
            return writer;
        }

        @Override
        public void close() throws IOException {
            server.shutdown();
        }
    }

    private static class ClientConnection implements Connection {
        private ManagedChannel channel;
        private String host;
        private int port;

        private ClientConnection(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public StreamObserver<P2PMessenger.Message> startChat(StreamObserver<P2PMessenger.Message> reader) throws IOException {
            LOGGER.info("Connecting to server at {}:{}", host, port);
            channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext(true) // Disable TLS
                    .build();
            MessengerGrpc.MessengerStub stub = MessengerGrpc.newStub(channel);
            LOGGER.info("Connected to server.");
            StreamObserver<P2PMessenger.Message> writer = stub.chat(reader);
            LOGGER.info("Chat initiated.");
            return writer;
        }

        @Override
        public void close() throws IOException {
            channel.shutdown();
        }
    }

    private Starter() {
    }

    public static Connection startServer(int port) {
        return new ServerConnection(port);
    }

    public static Connection startClient(String host, int port) throws IOException {
        return new ClientConnection(host, port);
    }
}
