package net.ldvsoft.spbau;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ldvsoft on 10.09.16.
 */
public interface PipeElement {
    void start(InputStream in, OutputStream out);
    boolean join();
}
