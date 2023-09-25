package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc863Tcp2ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp2ServerAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    int read() throws IOException {
        assert client != null;
        assert client.isBlocking();
        if (ThreadLocalRandom.current().nextBoolean()) {
            final var buffer = getBufferForReading();
            assert buffer.arrayOffset() == 0;
            final var r = client.socket().getInputStream().read(
                    buffer.array(),    // <b>
                    buffer.position(), // <off>
                    buffer.remaining() // <len>
            );
            if (r != -1) {
                buffer.position(buffer.position() + r);
                increaseBytes(updateDigest(r));
            }
            return r;
        }
        final int r = client.read(getBufferForReading());
        if (r != -1) {
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    private final SocketChannel client;
}
