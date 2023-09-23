package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

final class Rfc863Tcp3ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp3ServerAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    /**
     * Reads a sequence of bytes.
     *
     * @return A future representing the result of the operation.
     * @see Rfc863Tcp3ClientAttachment#write()
     */
    Future<Integer> read() {
        return client.read(getBufferForReading());
    }

    /**
     * Reads a sequence of bytes,, and returns the result.
     *
     * @return a number of bytes read.
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     * @see Rfc863Tcp3ClientAttachment#writeAndGet()
     */
    int readAndGet() throws ExecutionException, InterruptedException, TimeoutException {
        final var result = read().get(_Rfc863Constants.READ_TIMEOUT_DURATION,
                                      _Rfc863Constants.READ_TIMEOUT_UNIT);
        if (result != -1) {
            updateDigest(result);
            increaseBytes(result);
        }
        return result;
    }

    private final AsynchronousSocketChannel client;
}
