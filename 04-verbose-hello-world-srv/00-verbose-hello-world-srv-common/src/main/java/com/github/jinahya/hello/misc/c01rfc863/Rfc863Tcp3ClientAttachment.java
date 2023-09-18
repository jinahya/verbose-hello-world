package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

final class Rfc863Tcp3ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp3ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    // -------------------------------------------------------------------------------------- client

    /**
     * Writes a sequence of bytes.
     *
     * @return a future representing a number of bytes written.
     * @see Rfc863Tcp3ServerAttachment#read()
     */
    Future<Integer> write() {
        return client.write(getBufferForWriting());
    }

    /**
     * Writes a sequence of bytes, and returns the result.
     *
     * @return a number of bytes written.
     * @see Rfc863Tcp3ServerAttachment#readAndGet()
     */
    int writeAndGet() throws ExecutionException, InterruptedException {
        final var result = write().get();
        updateDigest(result);
        decreaseBytes(result);
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    private final AsynchronousSocketChannel client;
}
