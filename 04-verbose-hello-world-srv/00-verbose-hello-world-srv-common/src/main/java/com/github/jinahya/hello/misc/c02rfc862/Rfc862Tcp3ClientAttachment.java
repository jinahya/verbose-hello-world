package com.github.jinahya.hello.misc.c02rfc862;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

final class Rfc862Tcp3ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp3ClientAttachment(final SelectionKey clientKey) {
        super();
        this.clientKey = Objects.requireNonNull(clientKey, "clientKey is null");
    }

    int write() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isWritable();
        final var channel = (SocketChannel) clientKey.channel();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        final var w = channel.write(buffer);
        if (decreaseBytes(updateDigest(w)) == 0) {
            logDigest();
            channel.shutdownOutput();
            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
            buffer.limit(buffer.capacity()).position(buffer.limit());
        }
        return w;
    }

    int read() throws IOException {
        assert clientKey.isValid();
        assert clientKey.isReadable();
        buffer.flip(); // limit -> position, position -> zero
        final var r = ((ReadableByteChannel) clientKey.channel()).read(buffer);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1) {
            if (getBytes() > 0) {
                throw new EOFException("unexpected eof");
            }
            clientKey.interestOpsAnd(~SelectionKey.OP_READ);
            clientKey.cancel();
            assert !clientKey.isValid();
        }
        return r;
    }

    private final SelectionKey clientKey;
}
