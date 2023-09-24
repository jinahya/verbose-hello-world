package com.github.jinahya.hello.misc.c02rfc862;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

final class Rfc862Tcp2ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp2ClientAttachment(final SelectionKey clientKey) {
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
        final var w = ((WritableByteChannel) clientKey.channel()).write(buffer);
        if (decreaseBytes(updateDigest(w)) == 0) {
            logDigest();
            ((SocketChannel) clientKey.channel()).shutdownOutput();
            clientKey.interestOpsAnd(~SelectionKey.OP_WRITE);
        }
        return w;
    }

    int read() throws IOException {
        if (!clientKey.isValid()) {
            throw new IllegalStateException("clientKey is currently not valid");
        }
        if (!clientKey.isReadable()) {
            throw new IllegalStateException("clientKey is currently not readable");
        }
        buffer.flip(); // limit -> position, position -> zero
        if (getBytes() == 0) {
            buffer.clear();
        }
        final var r = ((ReadableByteChannel) clientKey.channel()).read(buffer);
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1) {
            if (getBytes() > 0) {
                throw new EOFException("unexpected eof");
            }
            close();
            assert !clientKey.isValid();
        }
        return r;
    }

    private final SelectionKey clientKey;
}
