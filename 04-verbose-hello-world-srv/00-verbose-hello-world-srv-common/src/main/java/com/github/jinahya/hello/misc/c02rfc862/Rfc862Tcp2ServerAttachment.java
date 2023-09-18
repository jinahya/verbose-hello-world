package com.github.jinahya.hello.misc.c02rfc862;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

// @formatter:on
final class Rfc862Tcp2ServerAttachment extends _Rfc862Attachment.Server {

    /**
     * Creates a new instance.
     */
    Rfc862Tcp2ServerAttachment() {
        super();
    }

    @Override
    public void close() throws IOException {
        // does nothing
    }

    int readFrom(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var r = channel.read(buffer);
        if (r != -1) {
            increaseBytes(r);
        }
        return r;
    }

    int writeTo(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        buffer.flip();
        final var w = channel.write(buffer);
        updateDigest(w);
        buffer.compact();
        return w;
    }
}
