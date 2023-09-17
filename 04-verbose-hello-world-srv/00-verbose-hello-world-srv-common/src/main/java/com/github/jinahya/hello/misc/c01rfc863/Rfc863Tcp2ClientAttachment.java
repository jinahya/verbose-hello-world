package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

final class Rfc863Tcp2ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp2ClientAttachment() {
        super();
    }

    /**
     * Writes a sequence of random bytes to specified channel from {@link #buffer}.
     *
     * @param channel the channel to which bytes are written.
     * @return number of bytes written to the {@code channel}, possibly zero.
     * @throws IOException if an I/O error occurs.
     * @see Rfc863Tcp2ServerAttachment#readFrom(ReadableByteChannel)
     */
    int writeTo(final WritableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final int w = channel.write(getBufferForWriting());
        assert w >= 0;
        updateDigest(w);
        decreaseBytes(w);
        return w;
    }
}
