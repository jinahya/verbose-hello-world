package com.github.jinahya.hello.misc.c01rfc863;

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class Rfc863Tcp3ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp3ServerAttachment(final AsynchronousSocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        super.close();
    }

    /**
     * Reads a sequence of bytes,, and returns the result.
     *
     * @return a number of bytes read.
     * @throws Exception if any thrown.
     * @see #getBufferForReading()
     * @see AsynchronousSocketChannel#read(ByteBuffer)
     * @see _Rfc86_Constants#READ_TIMEOUT
     * @see _Rfc86_Constants#READ_TIMEOUT_UNIT
     * @see java.util.concurrent.Future#get(long, TimeUnit)
     * @see Rfc863Tcp3ClientAttachment#write()
     */
    int read() throws Exception {
        final var r = client.read(getBufferForReading())
                .get(_Rfc86_Constants.READ_TIMEOUT, _Rfc86_Constants.READ_TIMEOUT_UNIT);
        if (r != -1) {
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    private final AsynchronousSocketChannel client;
}
