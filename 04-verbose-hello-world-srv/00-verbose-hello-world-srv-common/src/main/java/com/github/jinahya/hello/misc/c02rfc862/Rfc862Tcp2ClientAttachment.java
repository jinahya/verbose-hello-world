package com.github.jinahya.hello.misc.c02rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp2ClientAttachment extends _Rfc862Attachment.Client {

    Rfc862Tcp2ClientAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    int write() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), getBytes()));
        }
        int w;
        if (ThreadLocalRandom.current().nextBoolean()) {
            w = buffer.remaining();
            client.socket().getOutputStream().write(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.limit()
            );
            client.socket().getOutputStream().flush();
            buffer.position(w);
        } else {
            w = client.write(buffer);
            assert w == buffer.position();
        }
        assert !buffer.hasRemaining();
        if (decreaseBytes(updateDigest(w)) == 0) {
            buffer.limit(buffer.capacity()).position(buffer.limit());
        }
        if (w == 0) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.socket().shutdownOutput();
            } else {
                client.shutdownOutput();
            }
        }
        return w;
    }

    int read() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        int r;
        buffer.flip(); // limit -> position, position -> zero
        if (ThreadLocalRandom.current().nextBoolean()) {
            assert buffer.arrayOffset() == 0;
            r = client.socket().getInputStream().read(
                    buffer.array(),
                    0,
                    buffer.remaining()
            );
            if (r != -1) {
                buffer.position(r);
            }
        } else {
            r = client.read(buffer);
        }
        buffer.position(buffer.limit()).limit(buffer.capacity());
        if (r == -1 && getBytes() > 0) {
            throw new EOFException("unexpected eof");
        }
        return r;
    }

    private final SocketChannel client;
}
