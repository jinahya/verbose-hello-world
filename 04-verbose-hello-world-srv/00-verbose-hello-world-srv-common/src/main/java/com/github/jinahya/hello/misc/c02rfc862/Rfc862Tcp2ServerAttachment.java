package com.github.jinahya.hello.misc.c02rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

@Slf4j
final class Rfc862Tcp2ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp2ServerAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
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
        final var r = ((ReadableByteChannel) clientKey.channel()).read(buffer);
        if (r == -1) {
            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
        } else if (r > 0) {
            clientKey.interestOpsOr(SelectionKey.OP_WRITE);
            increaseBytes(r);
        }
        return r;
    }

    int write() throws IOException {
        if (!clientKey.isValid()) {
            throw new IllegalStateException("clientKey is currently not valid");
        }
        if (!clientKey.isWritable()) {
            throw new IllegalStateException("clientKey is currently not writable");
        }
        buffer.flip();
        final var w = ((WritableByteChannel) clientKey.channel()).write(buffer);
        updateDigest(w);
        buffer.compact();
        if (buffer.position() == 0 && (clientKey.interestOps() & SelectionKey.OP_READ) == 0) {
            close();
            assert !clientKey.isValid();
        }
        return w;
    }

    private final SelectionKey clientKey;
}
