package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class Rfc863Tcp3ClientAttachment extends _Rfc863Attachment.Client {

    /**
     * Creates a new instance.
     */
    Rfc863Tcp3ClientAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    /**
     * Writes a sequence of bytes to {@code client}, and returns the number of bytes written to the
     * {@code client}.
     *
     * @return a number of bytes written to the {@code client}.
     * @throws Exception if any thrown.
     * @see #getBufferForWriting()
     * @see AsynchronousSocketChannel#write(ByteBuffer)
     * @see _Rfc86_Constants#WRITE_TIMEOUT
     * @see _Rfc86_Constants#WRITE_TIMEOUT_UNIT
     * @see java.util.concurrent.Future#get(long, TimeUnit)
     * @see Rfc863Tcp3ServerAttachment#read()
     */
    int write() throws Exception {
        final var w = client.write(getBufferForWriting())
                .get(_Rfc86_Constants.WRITE_TIMEOUT, _Rfc86_Constants.WRITE_TIMEOUT_UNIT);
        decreaseBytes(updateDigest(w));
        return w;
    }

    private final AsynchronousSocketChannel client;
}
