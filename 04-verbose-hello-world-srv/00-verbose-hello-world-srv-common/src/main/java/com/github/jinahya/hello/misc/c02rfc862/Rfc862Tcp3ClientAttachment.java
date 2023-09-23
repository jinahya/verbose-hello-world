package com.github.jinahya.hello.misc.c02rfc862;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
final class Rfc862Tcp3ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp3ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    int write() throws ExecutionException, InterruptedException, TimeoutException {
        final var w = client.write(getBufferForWriting()).get(
                _Rfc86_Constants.WRITE_TIMEOUT_DURATION, _Rfc86_Constants.WRITE_TIMEOUT_UNIT
        );
        decreaseBytes(updateDigest(w));
        return w;
    }

    int read() throws ExecutionException, InterruptedException, TimeoutException {
        final var buffer = getBuffer();
        buffer.flip();
        final var result = client.read(getBufferForReading()).get(
                _Rfc862Constants.READ_TIMEOUT_DURATION, _Rfc862Constants.READ_TIMEOUT_UNIT);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (getBytes() == 0) {
            buffer.clear();
        }
        return result;
    }

    private final AsynchronousSocketChannel client;
}
