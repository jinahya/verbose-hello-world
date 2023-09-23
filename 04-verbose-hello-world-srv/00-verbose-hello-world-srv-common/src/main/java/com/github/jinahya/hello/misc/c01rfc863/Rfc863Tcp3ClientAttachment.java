package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

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
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     * @see Rfc863Tcp3ServerAttachment#readAndGet()
     */
    int writeAndGet() throws ExecutionException, InterruptedException, TimeoutException {
        final var result = write().get(_Rfc86_Constants.WRITE_TIMEOUT_DURATION,
                                       _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        updateDigest(result);
        decreaseBytes(result);
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    private final AsynchronousSocketChannel client;
}
