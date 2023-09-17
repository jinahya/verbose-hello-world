package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
final class Rfc863Tcp4ClientAttachment extends _Rfc863Attachment.Client {

    static final int LATCH_COUNT = 2; // connected + all sent

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

    // -------------------------------------------------------------------------------------- client

    void connectWith(final CompletionHandler<Void, ? super Rfc863Tcp4ClientAttachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        client.connect(
                _Rfc863Constants.ADDR, // <remote>
                this,                  // <attachment>
                handler                // <handler>
        );
    }

    /**
     * Reads a sequence of byte from {@code client}.
     *
     * @param handler a handler for consuming the result.
     * @see Rfc863Tcp4ServerAttachment#readWith(CompletionHandler)
     */
    void writeWith(final CompletionHandler<Integer, ? super Rfc863Tcp4ClientAttachment> handler) {
        Objects.requireNonNull(handler, "handler is null");
        client.write(
                getBufferForWriting(), // <src>
                this,                  // <attachment>
                handler                // <handler>
        );
    }

    void logConnected() {
        try {
            log.info("connected to {}, through {}", client.getRemoteAddress(),
                     client.getLocalAddress());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    // --------------------------------------------------------------------------------------- latch

    void countDownLatch(final long expectedCount) {
        if (expectedCount <= 0) {
            throw new IllegalArgumentException(
                    "expectedCount(" + expectedCount + ") is not positive"
            );
        }
        synchronized (latch) {
            if (latch.getCount() != expectedCount) {
                throw new IllegalArgumentException(
                        "latch.count(" + latch.getCount() + ')' +
                        " != expectedCount(" + expectedCount + ')'
                );
            }
            latch.countDown();
        }
    }

    boolean awaitLatch() throws InterruptedException {
        return latch.await(_Rfc863Constants.CLIENT_TIMEOUT_DURATION,
                           _Rfc863Constants.CLIENT_TIMEOUT_UNIT);
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;

    private final CountDownLatch latch = new CountDownLatch(LATCH_COUNT);
}
