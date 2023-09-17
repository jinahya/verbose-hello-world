package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

final class Rfc863Tcp4ClientAttachment extends _Rfc863Attachment.Client {

    static final int COUNT = 2; // connected + all sent

    /**
     * Creates a new instance holds specified client.
     *
     * @param client the client to hold.
     */
    Rfc863Tcp4ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    // ---------------------------------------------------------------------------------------------
    void connectWith(final CompletionHandler<Void, ? super Rfc863Tcp4ClientAttachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        client.connect(
                _Rfc863Constants.ADDR, // <remote>
                this,                  // <attachment>
                handler                // <handler>
        );
    }

    void writeWith(final CompletionHandler<Integer, ? super Rfc863Tcp4ClientAttachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        client.write(
                getBufferForWriting(), // <src>
                this,                  // <attachment>
                handler                // <handler>
        );
    }

    // --------------------------------------------------------------------------------------- latch

    // -------------------------------------------------------------------------------------- client

    // ---------------------------------------------------------------------------------------------
    final CountDownLatch latch = new CountDownLatch(COUNT);

    final AsynchronousSocketChannel client;
}
