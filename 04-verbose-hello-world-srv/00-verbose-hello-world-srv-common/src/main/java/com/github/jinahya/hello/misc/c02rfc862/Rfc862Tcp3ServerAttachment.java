package com.github.jinahya.hello.misc.c02rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp3ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp3ServerAttachment(final SelectionKey clientKey) {
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
        final var r = channel.read(buffer);
        if (r == -1) {
            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
        } else if (r > 0) {
            clientKey.interestOpsOr(SelectionKey.OP_WRITE);
            increaseBytes(r);
        }
        return r;
    }

    int write() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final var channel = (SocketChannel) clientKey.channel();
        buffer.flip();
        final var w = channel.write(buffer);
        updateDigest(w);
        buffer.compact();
        if (buffer.position() == 0 && (clientKey.interestOps() & SelectionKey.OP_READ) == 0) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                clientKey.cancel();
                assert !clientKey.isValid();
            }
            close();
            assert !clientKey.isValid();
        }
        return w;
    }

    private final SelectionKey clientKey;
}
