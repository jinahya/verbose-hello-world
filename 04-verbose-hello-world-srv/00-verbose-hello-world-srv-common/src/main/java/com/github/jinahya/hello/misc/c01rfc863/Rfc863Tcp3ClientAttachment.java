package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An attachment class used by {@link Rfc863Tcp3Client}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class Rfc863Tcp3ClientAttachment extends _Rfc863Attachment.Client {

    Rfc863Tcp3ClientAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    @Override
    public void close() throws IOException {
        clientKey.channel().close();
        super.close();
    }

    int write() throws IOException {
        if (!clientKey.isValid()) {
            throw new IllegalStateException("clientKey is currently not valid");
        }
        if (!clientKey.isWritable()) {
            throw new IllegalStateException("clientKey is currently not writable");
        }
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        final int w = ((WritableByteChannel) clientKey.channel()).write(buffer);
        assert w >= 0;
        if (decreaseBytes(updateDigest(w)) == 0) {
            close();
            assert !clientKey.isValid();
        }
        return w;
    }

    private final SelectionKey clientKey;
}
