package net.ldvsoft.spbau.messenger.protocol;

import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Connection interface. Wraps underlying network.
 */
public interface Connection extends AutoCloseable {
    /**
     * Starts chat
     * @param reader reader to read messages
     * @return writer to write messages
     * @throws IOException in case of errors
     */
    StreamObserver<P2PMessenger.Message> startChat(StreamObserver<P2PMessenger.Message> reader) throws IOException;

    /**
     * Closes the connection
     * @throws IOException is something goes wrong
     */
    void close() throws IOException;
}
