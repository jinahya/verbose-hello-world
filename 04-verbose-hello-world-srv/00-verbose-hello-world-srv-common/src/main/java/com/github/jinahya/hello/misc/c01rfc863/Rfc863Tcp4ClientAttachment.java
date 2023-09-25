package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

final class Rfc863Tcp4ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance holding specified client.
     *
     * @param client the client to hold.
     */
    Rfc863Tcp4ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    /**
     * Writes a sequence of bytes to {@code client}, and returns the number of bytes written to the
     * {@code client}.
     *
     * @return a number of bytes written to the {@code client}.
     * @throws Exception if any thrown.
     * @see AsynchronousSocketChannel#write(ByteBuffer)
     * @see Rfc863Tcp4ServerAttachment#read()
     */
    int write() throws Exception {
        final var w = client.write(getBufferForWriting())
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        assert w >= 0;
        assert w != 0 || getBytes() == 0;
        decreaseBytes(updateDigest(w));
        return w;
    }

    private final AsynchronousSocketChannel client;
}
