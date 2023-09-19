package com.github.jinahya.hello.misc.c02rfc862;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

final class Rfc862Tcp2ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp2ClientAttachment() {
        super();
    }

    int readFrom(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        buffer.flip(); // limit -> position, position -> zero
        final var r = channel.read(buffer);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        return r;
    }

    int writeTo(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var r = channel.write(getBufferForWriting());
        updateDigest(r);
        decreaseBytes(r);
        return r;
    }
}
