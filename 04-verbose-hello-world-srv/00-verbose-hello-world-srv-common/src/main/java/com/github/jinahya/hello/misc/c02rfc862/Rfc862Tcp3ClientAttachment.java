package com.github.jinahya.hello.misc.c02rfc862;

import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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

    int write() throws Exception {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        final var w = client.write(buffer)
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        decreaseBytes(updateDigest(w));
        return w;
    }

    int read() throws Exception {
        buffer.flip(); // limit -> position, position -> zero
        if (getBytes() == 0) {
            buffer.clear();
        }
        final var r = client.read(buffer)
                .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1 && getBytes() > 0) {
            throw new EOFException("unexpected eof");
        }
        return r;
    }

    private final AsynchronousSocketChannel client;
}
