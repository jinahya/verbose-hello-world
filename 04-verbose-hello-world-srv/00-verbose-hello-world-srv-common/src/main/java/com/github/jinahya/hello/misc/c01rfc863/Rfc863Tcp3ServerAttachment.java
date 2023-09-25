package com.github.jinahya.hello.misc.c01rfc863;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

final class Rfc863Tcp3ServerAttachment extends _Rfc863Attachment.Server {

    Rfc863Tcp3ServerAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    @Override
    public void close() throws IOException {
        clientKey.channel().close();
        super.close();
    }

    int read() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isReadable();
        final var channel = (SocketChannel) clientKey.channel();
        assert channel != null;
        assert !channel.isBlocking();
        final int r = channel.read(getBufferForReading());
        assert r >= -1;
        if (r == -1) {
            close();
            assert !clientKey.isValid();
        } else {
            increaseBytes(updateDigest(r));
        }
        return r;
    }

    private final SelectionKey clientKey;
}
