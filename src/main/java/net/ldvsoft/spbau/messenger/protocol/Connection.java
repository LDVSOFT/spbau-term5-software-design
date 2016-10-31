package net.ldvsoft.spbau.messenger.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Connection interface. Wraps underlying network.
 */
public interface Connection extends AutoCloseable {
    /**
     * Gets input stream to read from
     * @return input stream
     * @throws IOException if something goes wrong
     */
    InputStream getInputStream() throws IOException;

    /**
     * Gets output stream to write to
     * @return output stream
     * @throws IOException if something goes wrong
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Closes the connection
     * @throws IOException is something goes wrong
     */
    void close() throws IOException;
}
