package com.github.jinahya.hello;

import java.io.Closeable;
import java.io.IOException;

public interface IHelloWorldServer extends Closeable {

    /**
     * Opens the server.
     * @throws IOException if an I/O error occurs.
     */
    void open() throws IOException;
}
