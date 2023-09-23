package com.github.jinahya.hello.misc.c02rfc862;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

class Rfc862Tcp3ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp3ServerAttachment(final AsynchronousSocketChannel client) {
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
    int read() throws ExecutionException, InterruptedException, TimeoutException {
        final var buffer = getBuffer();
        buffer.flip();
        final var result = client.read(buffer).get(_Rfc862Constants.READ_TIMEOUT_DURATION,
                                                   _Rfc862Constants.READ_TIMEOUT_UNIT);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (getBytes() == 0) {
            buffer.clear();
        }
        return result;
    }

    int write() throws ExecutionException, InterruptedException, TimeoutException {
        final var result = client.write(getBufferForWriting()).get(
                _Rfc862Constants.WRITE_TIMEOUT_DURATION, _Rfc862Constants.WRITE_TIMEOUT_UNIT
        );
        updateDigest(result);
        decreaseBytes(result);
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousSocketChannel client;
}
