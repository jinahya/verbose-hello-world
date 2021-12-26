package com.github.jinahya.hello;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public interface IHelloWorldServer extends Closeable {

    /**
     * Opens this server instance.
     *
     * @throws IOException if an I/O error occurs.
     */
    void open() throws IOException;
}
