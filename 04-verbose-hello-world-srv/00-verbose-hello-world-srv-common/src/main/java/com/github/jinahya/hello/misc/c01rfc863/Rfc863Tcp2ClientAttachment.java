package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An attachment class used by {@link Rfc863Tcp3Client}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class Rfc863Tcp2ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp2ClientAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    int write() throws IOException {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        if (!buffer.hasRemaining()) {
            assert getBytes() == 0;
            return 0;
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            final var w = buffer.remaining();
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
        final var w = client.write(buffer);
        assert w > 0;
        decreaseBytes(updateDigest(w));
        return w;
    }

    private final SocketChannel client;
}
