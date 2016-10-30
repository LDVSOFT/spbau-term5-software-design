package net.ldvsoft.spbau.messenger.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Connection interface. Wraps underlying network.
 */
public interface Connection extends AutoCloseable {
    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    void close() throws IOException;
}
