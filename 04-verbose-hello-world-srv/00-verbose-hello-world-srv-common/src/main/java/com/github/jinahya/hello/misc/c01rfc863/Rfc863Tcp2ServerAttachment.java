package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

final class Rfc863Tcp2ServerAttachment extends _Rfc863Attachment.Server {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp2ServerAttachment() {
        super();
    }

    /**
     * Reads a sequence of bytes from specified channel.
     *
     * @param channel the channel from which bytes are read.
     * @return a number of bytes read from the {@code channel}.
     * @throws IOException if an I/O error occurs.
     * @see ReadableByteChannel#read(ByteBuffer)
     * @see Rfc863Tcp2ClientAttachment#write(WritableByteChannel)
     */
    int read(final ReadableByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final int r = channel.read(getBufferForReading());
        if (r == -1) {
            return r;
        }
        increaseBytes(updateDigest(r));
        return r;
    }
}
