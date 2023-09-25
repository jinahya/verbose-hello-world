package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

final class Rfc863Tcp3ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp3ServerAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
        if (!(clientKey.channel() instanceof SocketChannel)) {
            throw new IllegalArgumentException(
                    "clientKey.channel(" + clientKey.channel() + ")"
                    + " is not an instance of " + SocketChannel.class.getSimpleName()
            );
        }
    }

    @Override
    public void close() throws IOException {
        clientKey.channel().close();
        super.close();
    }

    int read() throws IOException {
        if (!clientKey.isValid()) {
            throw new IllegalStateException("clientKey is currently not valid");
        }
        if (!clientKey.isReadable()) {
            throw new IllegalStateException("clientKey is currently not readable");
        }
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        final int r = ((ReadableByteChannel) clientKey.channel()).read(buffer);
        if (r == -1) {
            close();
            assert !clientKey.isValid();
        } else {
            assert r >= 0;
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    private final SelectionKey clientKey;
}
