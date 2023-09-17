package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

final class Rfc863Tcp2ServerAttachment extends _Rfc863Attachment.Server {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp2ServerAttachment() {
        super();
    }

    /**
     * Reads a sequence of bytes from specified channel into {@link #buffer}.
     *
     * @param channel the channel from which bytes are written.
     * @return a number of bytes read from the {@code channel}.
     * {@link ReadableByteChannel#read(ByteBuffer) channel.read(buffer)}.
     * @throws IOException if an I/O error occurs.
     * @see Rfc863Tcp2ClientAttachment#writeTo(WritableByteChannel)
     */
    int readFrom(final ReadableByteChannel channel) throws IOException {
        final int r = channel.read(getBufferForReading());
        if (r == -1) {
            return r;
        }
        updateDigest(r);
        increaseBytes(r);
        return r;
    }
}
