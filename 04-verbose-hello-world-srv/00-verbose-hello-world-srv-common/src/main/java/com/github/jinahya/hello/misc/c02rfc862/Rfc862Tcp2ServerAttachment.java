package com.github.jinahya.hello.misc.c02rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
final class Rfc862Tcp2ServerAttachment extends _Rfc862Attachment.Server {

    Rfc862Tcp2ServerAttachment(final SocketChannel client) {
        super();
        this.client = Objects.requireNonNull(client, "client is null");
    }

    @Override
    public void close() throws IOException {
        client.close();
        assert client.socket().isClosed();
        super.close();
    }

    int read() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        int r;
        if (ThreadLocalRandom.current().nextBoolean()) {
            assert buffer.arrayOffset() == 0;
            r = client.socket().getInputStream().read(
                    buffer.array(),
                    buffer.position(),
                    buffer.limit()
            );
            if (r != -1) {
                buffer.position(buffer.position() + r);
            }
        } else {
            r = client.read(buffer);
        }
        if (r == -1) {
            client.shutdownInput();
        } else {
            increaseBytes(r);
        }
        return r;
    }

    int write() throws IOException {
        assert client.isConnected();
        assert client.isOpen();
        assert client.socket().isConnected();
        assert !client.socket().isClosed();
        int w;
        buffer.flip();
        if (ThreadLocalRandom.current().nextBoolean()) {
            w = buffer.remaining();
            client.socket().getOutputStream().write(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining()
            );
            buffer.position(buffer.limit());
        } else {
            w = client.write(buffer);
        }
        assert !buffer.hasRemaining();
        updateDigest(w);
        buffer.compact();
        assert buffer.position() == 0;
        return w;
    }

    private final SocketChannel client;
}
