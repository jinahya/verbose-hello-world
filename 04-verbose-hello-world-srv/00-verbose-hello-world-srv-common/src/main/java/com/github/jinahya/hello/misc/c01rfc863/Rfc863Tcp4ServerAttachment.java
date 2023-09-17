package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
final class Rfc863Tcp4ServerAttachment extends _Rfc863Attachment.Server {

    static final int LATCH_COUNT = 2; // accepted + all received

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
        synchronized (this) {
            if (client != null) {
                client.close();
            }
        }
        super.close();
    }

    // -------------------------------------------------------------------------------------- client

    void setClient(final AsynchronousSocketChannel client) {
        Objects.requireNonNull(client, "client is null");
        synchronized (this) {
            if (this.client != null) {
                throw new IllegalStateException("client is already set");
            }
            this.client = client;
        }
        logConnected();
    }

    private void logConnected() {
        try {
            log.info("accepted from {}, through {}", client.getRemoteAddress(),
                     client.getLocalAddress());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    /**
     * Reads a sequence of bytes from {@code client}.
     *
     * @param handler a handler for consuming the result.
     * @see Rfc863Tcp4ClientAttachment#writeWith(CompletionHandler)
     */
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
        return latch.await(_Rfc863Constants.SERVER_TIMEOUT_DURATION,
                           _Rfc863Constants.SERVER_TIMEOUT_UNIT);
    }

    // ---------------------------------------------------------------------------------------------
    private AsynchronousSocketChannel client;

    private final CountDownLatch latch = new CountDownLatch(LATCH_COUNT);
}
