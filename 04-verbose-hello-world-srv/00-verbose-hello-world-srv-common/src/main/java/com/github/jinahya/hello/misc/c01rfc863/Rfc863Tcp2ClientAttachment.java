package com.github.jinahya.hello.misc.c01rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An attachment class used by {@link Rfc863Tcp3Client}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class Rfc863Tcp2ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp2ClientAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    int write() throws IOException {
        assert client.isBlocking();
        if (ThreadLocalRandom.current().nextBoolean()) {
            final var buffer = getBufferForWriting();
            final var w = buffer.remaining();
            assert w > 0;
            assert buffer.arrayOffset() == 0;
            client.socket().getOutputStream().write(
                    buffer.array(),    // <b>
                    buffer.position(), // <off>
                    buffer.remaining() // <len>
            );
            buffer.position(buffer.limit());
            decreaseBytes(updateDigest(w));
            return w;
        }
        final var w = client.write(getBufferForWriting());
        assert w > 0;
        assert !buffer.hasRemaining();
        decreaseBytes(updateDigest(w));
        return w;
    }

    private final SocketChannel client;
}
