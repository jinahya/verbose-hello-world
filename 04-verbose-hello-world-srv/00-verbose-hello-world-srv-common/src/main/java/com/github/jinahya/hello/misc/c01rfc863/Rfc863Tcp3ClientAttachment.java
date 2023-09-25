package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

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

    int write() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final int w = ((WritableByteChannel) clientKey.channel()).write(getBufferForWriting());
        assert w >= 0;
        if (decreaseBytes(updateDigest(w)) == 0) { // all bytes have been sent
            close();
            clientKey.cancel();
            assert !clientKey.isValid();
        }
        return w;
    }

    private final SelectionKey clientKey;
}
