package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * An attachment class used by {@link Rfc863Tcp2Client}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class Rfc863Tcp2ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp2ClientAttachment() {
        super();
    }

    /**
     * Writes a sequence of random bytes to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @return number of bytes written to the {@code channel}, possibly zero.
     * @throws IOException if an I/O error occurs.
     * @see WritableByteChannel#write(ByteBuffer)
     * @see Rfc863Tcp2ServerAttachment#read(ReadableByteChannel)
     */
    int write(final WritableByteChannel channel) throws IOException {
        final int w = channel.write(getBufferForWriting());
        assert w >= 0;
        decreaseBytes(updateDigest(w));
        return w;
    }
}
