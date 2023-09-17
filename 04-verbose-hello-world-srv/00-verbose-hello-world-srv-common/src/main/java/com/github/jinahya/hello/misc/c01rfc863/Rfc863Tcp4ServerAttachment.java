package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

final class Rfc863Tcp4ServerAttachment extends _Rfc863Attachment.Server {

    static final int COUNT = 2; // accepted + all received

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    Rfc863Tcp4ServerAttachment() {
        super();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
        super.close();
    }

    // --------------------------------------------------------------------------------------- latch

    // -------------------------------------------------------------------------------------- client

    void setClient(final AsynchronousSocketChannel client) {
        Objects.requireNonNull(client, "client is null");
        synchronized (this) {
            if (this.client != null) {
                throw new IllegalStateException("client is already set");
            }
            this.client = client;
        }
    }

    void readWith(final CompletionHandler<Integer, ? super Rfc863Tcp4ServerAttachment> handler) {
        synchronized (this) {
            if (client == null) {
                throw new IllegalStateException("client is null");
            }
        }
        Objects.requireNonNull(handler, "handler is null");
        client.read(
                getBufferForReading(),                  // <dst>
                _Rfc863Constants.READ_TIMEOUT_DURATION, // <timeout>
                _Rfc863Constants.READ_TIMEOUT_UNIT,     // <unit>
                this,                                   // <attachment>
                handler                                 // <handler>
        );
    }

    // ---------------------------------------------------------------------------------------------

    final CountDownLatch latch = new CountDownLatch(COUNT);

    private AsynchronousSocketChannel client;
}
